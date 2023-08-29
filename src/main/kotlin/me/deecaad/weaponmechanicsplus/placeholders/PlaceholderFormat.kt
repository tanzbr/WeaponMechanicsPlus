package me.deecaad.weaponmechanicsplus.placeholders

import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.core.placeholder.PlaceholderHandler

abstract class PlaceholderFormat <T : PlaceholderHandler> {
    abstract fun format(placeholder: T, data: PlaceholderData): String
}