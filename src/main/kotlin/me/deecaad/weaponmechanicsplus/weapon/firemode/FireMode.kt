package me.deecaad.weaponmechanicsplus.weapon.firemode

import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.mechanics.Mechanics
import me.deecaad.weaponmechanics.weapon.trigger.Trigger
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlus
import org.bukkit.scheduler.BukkitRunnable

class FireMode : Serializer<FireMode> {

    var trigger: Trigger? = null
        private set

    var nextMode: String? = null
        private set

    var switchMechanics: Mechanics? = null
        private set

    /**
     * Default constructor for serializer
     */
    constructor() {}

    constructor(trigger: Trigger, nextMode: String, switchMechanics: Mechanics?) {
        this.trigger = trigger
        this.nextMode = nextMode
        this.switchMechanics = switchMechanics
    }

    override fun getKeyword(): String {
        return "Fire_Mode"
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): FireMode {
        val trigger = data.of("Trigger").assertExists().serialize(Trigger::class.java)
        val nextMode = data.of("Next_Mode").assertExists().assertType(String::class.java).get<String>()
        val switchMechanics = data.of("Mechanics").serialize(Mechanics::class.java)

        // Late check on weapon name
        object : BukkitRunnable() {
            override fun run() {
                if (!WeaponMechanics.getWeaponHandler().infoHandler.hasWeapon(nextMode)) {
                    data.exception(
                        "Next_Mode", "Could not find weapon named $nextMode",
                        SerializerException.didYouMean(
                            nextMode,
                            WeaponMechanics.getWeaponHandler().infoHandler.sortedWeaponList
                        )
                    ).log(WeaponMechanicsPlus.getDebug())
                }
            }
        }.runTask(WeaponMechanicsPlus.getPlugin())

        return FireMode(trigger, nextMode, switchMechanics)
    }
}