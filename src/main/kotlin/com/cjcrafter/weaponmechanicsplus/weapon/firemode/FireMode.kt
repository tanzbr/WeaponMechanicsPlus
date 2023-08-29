package com.cjcrafter.weaponmechanicsplus.weapon.firemode

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import me.deecaad.core.mechanics.Mechanics
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.weapon.trigger.Trigger
import org.bukkit.scheduler.BukkitRunnable

class FireMode : Serializer<FireMode> {

    lateinit var trigger: Trigger
    lateinit var nextMode: String
    var switchMechanics: Mechanics? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(trigger: Trigger, nextMode: String, switchMechanics: Mechanics?) {
        this.trigger = trigger
        this.nextMode = nextMode
        this.switchMechanics = switchMechanics
    }

    /**
     * Path is `WeaponTitle.Info.Fire_Mode`
     */
    override fun getKeyword() = "Fire_Mode"

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): FireMode {
        val trigger = data.of("Trigger").assertExists().serialize(Trigger::class.java)
        val nextMode = data.of("Next_Mode").assertExists().assertType(String::class.java).get<String>()
        val switchMechanics = data.of("Mechanics").serialize(Mechanics::class.java)

        // This is just to check to make sure the weapons exist. Weapon titles
        // have not yet been added to the info handler on this tick, so we have
        // to wait for the next tick.
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