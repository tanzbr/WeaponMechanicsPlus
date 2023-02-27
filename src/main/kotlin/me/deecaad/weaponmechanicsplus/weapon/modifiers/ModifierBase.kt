package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.configuration.ConfigurationSection

abstract class ModifierBase : Serializer<ModifierBase> {
    var priority = 0
        protected set
    var modifier: Modifier = Modifier()
        protected set
    var perWeaponModifiers: Map<String, Modifier> = mapOf()
        protected set

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(priority: Int, modifier: Modifier, perWeaponModifiers: Map<String, Modifier>) {
        this.priority = priority
        this.modifier = modifier
        this.perWeaponModifiers = perWeaponModifiers
    }

    fun getModifier(weaponTitle: String): Modifier {
        return perWeaponModifiers.getOrDefault(weaponTitle, modifier)
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ModifierBase {
        val priority = data.of("Priority").getInt(0) // can be negative
        val modifier = data.of("Modifiers").assertExists().serialize(Modifier::class.java)

        val perWeaponModifiers = HashMap<String, Modifier>()
        if (data.has("Per_Weapon_Modifiers")) {
            val config = data.of("Per_Weapon_Modifiers").assertExists().assertType(ConfigurationSection::class.java).get<ConfigurationSection>()

            for (key in config.getKeys(false)) {
                val options = WeaponMechanics.getWeaponHandler().infoHandler.sortedWeaponList
                if (!options.contains(key))
                    throw SerializerOptionsException(data.serializer, "Weapon Title", options, key, data.of(key).location)

                val temp = data.of("Per_Weapon_Modifiers.$key").serialize(Modifier::class.java)
                perWeaponModifiers[key] = temp
            }
        }

        return object : ModifierBase(priority, modifier, perWeaponModifiers) {}
    }
}