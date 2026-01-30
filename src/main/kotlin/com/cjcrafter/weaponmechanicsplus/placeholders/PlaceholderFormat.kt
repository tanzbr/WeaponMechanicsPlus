/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.placeholders

import me.deecaad.core.file.Serializer
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.core.placeholder.PlaceholderHandler

abstract class PlaceholderFormat <T : PlaceholderHandler> (
    val clazz: Class<T>
) : Serializer<PlaceholderFormat<T>> {
    abstract fun format(placeholder: T, data: PlaceholderData): String
}