package com.cjcrafter.weaponmechanicsplus.placeholders

import me.deecaad.core.file.Serializer
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.core.placeholder.PlaceholderHandler

abstract class PlaceholderFormat <T : PlaceholderHandler> (
    val clazz: Class<T>
) : Serializer<PlaceholderFormat<T>> {
    abstract fun format(placeholder: T, data: PlaceholderData): String
}