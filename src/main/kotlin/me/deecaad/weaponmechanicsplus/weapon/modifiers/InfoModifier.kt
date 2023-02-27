package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*

class InfoModifier : Serializer<InfoModifier> {

    @Throws(SerializerException::class)
    override fun serialize(serializeData: SerializeData): InfoModifier {
        return InfoModifier()
    }
}