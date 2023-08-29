package com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments

import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.weapon.stats.WeaponStat
import org.bukkit.inventory.ItemStack

class Unlockable : Serializer<Unlockable> {

    private val itemStack: ItemStack? = null
    private val experience = 0
    private val stats: Map<WeaponStat, Number>? = null

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): Unlockable {
        return Unlockable()
    }
}