package com.cjcrafter.weaponmechanicsplus.weapon.firemode

import java.util.concurrent.ConcurrentHashMap

object FireModeRegistry {
    private val byWeaponTitle = ConcurrentHashMap<String, FireMode>()

    fun register(title: String, fireMode: FireMode) {
        byWeaponTitle[title] = fireMode
    }

    fun get(title: String): FireMode? = byWeaponTitle[title]

    fun clear() = byWeaponTitle.clear()
}