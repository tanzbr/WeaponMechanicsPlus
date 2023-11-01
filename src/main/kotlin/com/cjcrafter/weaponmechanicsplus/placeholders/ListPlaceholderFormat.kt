package com.cjcrafter.weaponmechanicsplus.placeholders

import me.deecaad.core.file.SerializeData
import me.deecaad.core.placeholder.ListPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderData

class ListPlaceholderFormat : PlaceholderFormat<ListPlaceholderHandler> {

    var nullFormat = "None"
    var prefix = ""
    var suffix = ""
    var perLine = 0
    var skipFirstLine = false
    var separator = ""
    var newLine = ""

    constructor() : super(ListPlaceholderHandler::class.java)
    constructor(
        nullFormat: String,
        prefix: String,
        suffix: String,
        perLine: Int,
        skipFirstLine: Boolean,
        separator: String,
        newLine: String,
    ) : super(ListPlaceholderHandler::class.java) {
        this.nullFormat = nullFormat
        this.prefix = prefix
        this.suffix = suffix
        this.perLine = perLine
        this.skipFirstLine = skipFirstLine
        this.separator = separator
        this.newLine = newLine
    }

    override fun format(placeholder: ListPlaceholderHandler, data: PlaceholderData): String {
        val list = placeholder.requestValue(data) ?: return nullFormat
        val builder = StringBuilder()
        for ((index, element) in list.withIndex()) {
            if (index == 0 && skipFirstLine)
                builder.append("\n")

            builder.append(prefix)
            builder.append(element)
            builder.append(suffix)

            // Last element in list, do not add a separator
            if (index != list.size - 1) {
                if (index % perLine == 0) {
                    builder.append("\n").append(newLine)
                } else {
                    builder.append(separator)
                }
            }
        }

        return builder.toString()
    }

    override fun serialize(data: SerializeData): PlaceholderFormat<ListPlaceholderHandler> {
        val nullFormat = data.of("Null_Format").assertExists().adventure!!
        val prefix = data.of("Prefix").assertExists().adventure!!
        val suffix = data.of("Suffix").assertExists().adventure!!
        val perLine = data.of("Elements_Per_Line").assertExists().assertPositive().int
        val skipFirstLine = data.of("Skip_First_Line").assertExists().bool
        val separator = data.of("Separator").assertExists().adventure!!
        val newLine = data.of("New_Line").assertExists().adventure!!
        return ListPlaceholderFormat(nullFormat, prefix, suffix, perLine, skipFirstLine, separator, newLine)
    }
}