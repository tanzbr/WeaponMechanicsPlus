/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*

class InfoModifier : Serializer<InfoModifier> {

    @Throws(SerializerException::class)
    override fun serialize(serializeData: SerializeData): InfoModifier {
        return InfoModifier()
    }
}