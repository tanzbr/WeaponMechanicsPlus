package me.deecaad.weaponmechanicsplus.weapon.modifiers.ammotype

import me.deecaad.core.file.*
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ModifierBase

class AmmoTypeModifier : ModifierBase() {
    override fun getKeyword(): String {
        return "Ammo_Type_Modifier"
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): AmmoTypeModifier {
        val base = super.serialize(data)
        return null
    }
}