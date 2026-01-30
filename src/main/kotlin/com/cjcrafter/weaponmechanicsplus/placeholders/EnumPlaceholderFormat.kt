/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.placeholders

import me.deecaad.core.file.SerializeData
import me.deecaad.core.placeholder.EnumPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderData
import kotlin.collections.HashMap

class EnumPlaceholderFormat : PlaceholderFormat<EnumPlaceholderHandler> {

    // Options available for all modes
    var enums: Map<String, String> = mapOf()

    /**
     * Default constructor for serializer
     */
    constructor() : super(EnumPlaceholderHandler::class.java)
    constructor(
        enums: Map<String, String>,
    ) : super(EnumPlaceholderHandler::class.java) {
        this.enums = enums
    }

    override fun format(placeholder: EnumPlaceholderHandler, data: PlaceholderData): String {
        return enums[placeholder.onRequest(data)]!!
    }

    override fun serialize(data: SerializeData): EnumPlaceholderFormat {
        val enums = HashMap<String, String>()

        for (colorObj in data.of().assertExists().get(List::class.java).get()) {
            val colorLine = colorObj as String
            val splitIndex = colorLine.indexOf(' ')
            val enumString = colorLine.substring(0, splitIndex)
            val textString = colorLine.substring(splitIndex + 1)
            enums[enumString] = textString
        }
        return EnumPlaceholderFormat(enums)
    }
}