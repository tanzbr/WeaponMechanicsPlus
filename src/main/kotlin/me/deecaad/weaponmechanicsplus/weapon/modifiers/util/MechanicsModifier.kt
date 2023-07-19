package me.deecaad.weaponmechanicsplus.weapon.modifiers.util

import me.deecaad.core.file.SerializeData
import me.deecaad.core.mechanics.Mechanics

data class MechanicsModifier(val isReplace: Boolean, val mechanics: Mechanics) {

    fun add(base: Mechanics) {
        if (isReplace)
            throw IllegalArgumentException("Tried to add mechanics when they should have been replace")

        base.addDirty(mechanics.mechanics)
    }

    companion object {

        fun SerializeData.serializeMechanicsModifier(key: String = "Mechanics"): MechanicsModifier? {

            val replaceMechanics: Mechanics? = of("Replace_$key").serialize(Mechanics::class.java)
            val addMechanics: Mechanics? = of("Add_$key").serialize(Mechanics::class.java)

            val isReplace = replaceMechanics != null
            val mechanics = if (isReplace) replaceMechanics else addMechanics

            if (replaceMechanics != null && addMechanics != null) {
                throw exception(null,
                    "You cannot use both 'Replace_$key' and 'Add_$key' on the same attachment at the same time",
                    "If you want to replace the weapon's mechanics, use 'Replace_$key'",
                    "If you want to add new mechanics to the existing mechanics, use 'Add_$key'")
            }

            if (mechanics == null)
                return null

            return MechanicsModifier(isReplace, mechanics)
        }
    }
}