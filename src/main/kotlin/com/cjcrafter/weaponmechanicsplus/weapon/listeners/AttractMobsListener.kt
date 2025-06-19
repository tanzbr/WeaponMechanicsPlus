package com.cjcrafter.weaponmechanicsplus.weapon.listeners

import com.cjcrafter.weaponmechanicsplus.weapon.attract.AttractMobs
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.weapon.weaponevents.PrepareWeaponShootEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Listens for weapon shots and attracts mobs to the shooter using the
 * [AttractMobs] class.
 */
class AttractMobsListener : Listener {

    @EventHandler (ignoreCancelled = true)
    fun onShoot(event: PrepareWeaponShootEvent) {
        val attractMobs = WeaponMechanics.getInstance().weaponConfigurations.get<AttractMobs>("${event.weaponTitle}.Shoot.Attract_Mobs") ?: return
        attractMobs.attract(event.shooter, event.weaponTitle, event.weaponStack)
    }
}