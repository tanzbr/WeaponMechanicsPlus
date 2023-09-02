package com.cjcrafter.weaponmechanicsplus.placeholders

import me.deecaad.core.file.SerializeData
import me.deecaad.core.placeholder.EnumPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderData
import kotlin.collections.HashMap

class EnumPlaceholderFormat : PlaceholderFormat<EnumPlaceholderHandler> {

    // Options available for all modes
    var enums: Map<String?, String> = mapOf()

    /**
     * Default constructor for serializer
     */
    constructor() : super(EnumPlaceholderHandler::class.java)
    constructor(
        enums: Map<String?, String>,
    ) : super(EnumPlaceholderHandler::class.java) {
        this.enums = enums
    }

    override fun format(placeholder: EnumPlaceholderHandler, data: PlaceholderData): String {
        return enums[placeholder.onRequest(data)]!!
    }

    override fun serialize(data: SerializeData): EnumPlaceholderFormat {
        val enums = HashMap<String?, String>()

        for (colorLine in data.of().assertExists().assertType(List::class.java).get<List<String>>()) {
            val splitIndex = colorLine.indexOf(' ')
            val enumString = colorLine.substring(0, splitIndex)
            val textString = colorLine.substring(splitIndex + 1)
            enums[enumString] = textString
        }
        return EnumPlaceholderFormat(enums)
    }
}