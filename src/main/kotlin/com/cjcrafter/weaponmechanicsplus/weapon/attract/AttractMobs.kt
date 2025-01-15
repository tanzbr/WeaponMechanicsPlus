package com.cjcrafter.weaponmechanicsplus.weapon.attract

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusAPI
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.serializers.ChanceSerializer
import me.deecaad.core.file.simple.BooleanSerializer
import me.deecaad.core.file.simple.DoubleSerializer
import me.deecaad.core.file.simple.EnumValueSerializer
import me.deecaad.core.file.simple.RegistryValueSerializer
import me.deecaad.core.mechanics.CastData
import me.deecaad.core.mechanics.Mechanics
import me.deecaad.core.utils.EnumUtil
import me.deecaad.core.utils.NumberUtil
import me.deecaad.core.utils.RandomUtil
import me.deecaad.core.utils.chance
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
import java.util.EnumMap
import java.util.Random
import kotlin.math.sqrt

/**
 * This feature is for attracting mobs to the shooter when they shoot.
 */
class AttractMobs : Serializer<AttractMobs> {

    enum class AttractMode {
        CANCEL, // Skip this entity
        ATTRACT, // Attract this entity
    }

    /**
     * Stores the config for a single mob type.
     *
     * @property distance How far away the mob can be attracted from
     * @property chance The chance that the mob will be attracted
     */
    data class MobData(
        val mode: AttractMode,
        val distanceSquared: Double? = null,
        val chance: Double? = null,
        val overrideCurrentTarget: Boolean? = null,
    )

    var skipChance: Double = 0.0
    var defaultMode: AttractMode = AttractMode.CANCEL
    var defaultDistanceSquared: Double = 20.0
    var defaultChance: Double = 1.0
    var defaultOverrideCurrentTarget: Boolean = false
    lateinit var mobs: Map<EntityType, MobData>
    var mechanics: Mechanics? = null
    var isOnlyPlayerShooter: Boolean = true

    // Not configurable... Calculated for performance
    var maxDistance: Double = 0.0

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(
        skipChance: Double,
        defaultMode: AttractMode,
        defaultDistanceSquared: Double,
        defaultChance: Double,
        defaultOverrideCurrentTarget: Boolean,
        mobs: Map<EntityType, MobData>,
        mechanics: Mechanics?,
        isOnlyPlayerShooter: Boolean,
    ) {
        this.skipChance = skipChance
        this.defaultMode = defaultMode
        this.defaultDistanceSquared = defaultDistanceSquared
        this.defaultChance = defaultChance
        this.defaultOverrideCurrentTarget = defaultOverrideCurrentTarget
        this.mobs = mobs
        this.mechanics = mechanics
        this.isOnlyPlayerShooter = isOnlyPlayerShooter

        // Calculate the max distance
        maxDistance = sqrt(defaultDistanceSquared)
        for (mob in mobs.values) {
            val distance = sqrt(mob.distanceSquared ?: continue)
            if (distance > maxDistance)
                maxDistance = distance
        }
    }

    /**
     * Triggers an attraction event for the given shooter.
     *
     * @param shooter Who shot the weapon
     * @param weaponTitle The weapon title which was shot
     * @param weaponStack The weapon stack which was shot
     */
    fun attract(shooter: LivingEntity, weaponTitle: String, weaponStack: ItemStack) {
        if (RandomUtil.chance(skipChance))
            return
        if (isOnlyPlayerShooter && shooter.type != EntityType.PLAYER)
            return

        // Attachments, like suppressors can skip mob attraction
        var skipAttractMobs = false
        WeaponMechanicsPlusAPI.forEachModifier(shooter, weaponStack) { modifier ->
            if (modifier.getWeaponModifier(weaponTitle)?.shoot?.skipAttractMobs == true)
                skipAttractMobs = true
        }

        if (skipAttractMobs)
            return

        val nearbyEntities = shooter.world.getNearbyEntities(shooter.location, maxDistance, maxDistance, maxDistance) { mob ->

            // Can only attract mobs, since mobs can have targets
            if (mob !is Mob)
                return@getNearbyEntities false
            if (mob.uniqueId == shooter.uniqueId)
                return@getNearbyEntities false

            // Filter out mobs that should be attracted based on config, distance,
            // current target, and chance.
            val mobData = mobs[mob.type]
            if ((mobData?.mode ?: defaultMode) == AttractMode.CANCEL)
                return@getNearbyEntities false
            if ((mobData?.distanceSquared ?: defaultDistanceSquared) < mob.location.distanceSquared(shooter.location))
                return@getNearbyEntities false
            if (mob.target != null && !(mobData?.overrideCurrentTarget ?: defaultOverrideCurrentTarget))
                return@getNearbyEntities false
            if (!RandomUtil.chance(mobData?.chance ?: defaultChance))
                return@getNearbyEntities false

            // Ok! We can attract this mob!
            return@getNearbyEntities true
        }

        val cast = CastData(shooter, weaponTitle, weaponStack)
        for (mob in nearbyEntities) {
            mob as Mob
            mob.target = shooter

            cast.setTargetEntity(mob)
            mechanics?.use(cast)
        }
    }

    override fun getKeyword() = "Attract_Mobs"

    override fun getParentKeywords(): MutableList<String> {
        // weapon_title.Shoot.Attract_Mobs
        return mutableListOf("Shoot")
    }

    override fun serialize(data: SerializeData): AttractMobs {
        val skipChance: Double = data.of("Skip_Chance").serialize(ChanceSerializer()).orElse(0.0)

        val defaultMode = data.of("Default_Mode").getEnum(AttractMode::class.java).orElse(AttractMode.CANCEL)
        val defaultDistance = data.of("Default_Distance").assertRange(0.0, null).getDouble().orElse(30.0)
        val defaultChance = data.of("Default_Chance").serialize(ChanceSerializer()).orElse(1.0)
        val defaultOverrideCurrentTarget = data.of("Default_Override_Current_Target").getBool().orElse(false)

        val mobs = EnumMap<EntityType, MobData>(EntityType::class.java)
        val list = data.ofList("Mobs")
            .addArgument(RegistryValueSerializer(EntityType::class.java, true))
            .addArgument(EnumValueSerializer(AttractMode::class.java, false))
            .requireAllPreviousArgs()
            .addArgument(DoubleSerializer(min = 0.0))
            .addArgument(ChanceSerializer())
            .addArgument(BooleanSerializer())
            .assertExists()
            .assertList()

        for (split in list) {
            val entities = split[0].get() as List<EntityType>
            val mode = (split[1].get() as List<AttractMode>).first()
            val distance = split[2].orElse(null) as? Double?
            val chance = split[3].orElse(null) as? Double?
            val overrideCurrentTarget = split[4].orElse(null) as? Boolean?

            for (entity in entities)
                mobs[entity] = MobData(mode, distance?.times(distance), chance, overrideCurrentTarget)
        }

        val mechanics = data.of("Mechanics").serialize(Mechanics::class.java).get()
        val isOnlyPlayerShooter = data.of("Only_Player_Shooter").getBool().orElse(true)

        return AttractMobs(
            skipChance,
            defaultMode,
            defaultDistance * defaultDistance, // squared distance for performance
            defaultChance,
            defaultOverrideCurrentTarget,
            mobs,
            mechanics,
            isOnlyPlayerShooter,
        )
    }
}