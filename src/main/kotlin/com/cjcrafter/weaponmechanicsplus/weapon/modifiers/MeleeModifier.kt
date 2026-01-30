/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*

class MeleeModifier : Serializer<MeleeModifier> {

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): MeleeModifier {
        return MeleeModifier()
    }
}