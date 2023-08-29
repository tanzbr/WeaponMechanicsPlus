package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*

class MeleeModifier : Serializer<MeleeModifier> {

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): MeleeModifier {
        return MeleeModifier()
    }
}