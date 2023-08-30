package com.cjcrafter.weaponmechanicsplus.placeholders

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import me.deecaad.core.file.SerializeData
import me.deecaad.core.placeholder.NumericPlaceholderHandler
import me.deecaad.core.placeholder.PlaceholderData
import me.deecaad.core.utils.NumberUtil
import java.util.*


class NumericPlaceholderFormat : PlaceholderFormat<NumericPlaceholderHandler> {

    var defaultMode: Mode = Mode.VALUE

    // Options available for all modes
    var prefix: String = ""
    var suffix: String = ""
    var nullFormat: String = "null"
    var colors: TreeMap<Double, String> = TreeMap()

    // Mode.PERCENTAGE & Mode.BAR options
    var min: Double = 0.0
    var max: Double = 20.0
    lateinit var bar: StringBar

    // Mode.EMOJI options
    lateinit var emojis: TreeMap<Double, String>

    /**
     * Default constructor for serializer
     */
    constructor() : super(NumericPlaceholderHandler::class.java)
    constructor(
        defaultMode: Mode,
        prefix: String,
        suffix: String,
        nullFormat: String,
        colors: TreeMap<Double, String>,
        min: Double,
        max: Double,
        bar: StringBar,
        emojis: TreeMap<Double, String>
    ) : super(NumericPlaceholderHandler::class.java) {
        this.defaultMode = defaultMode
        this.prefix = prefix
        this.suffix = suffix
        this.nullFormat = nullFormat
        this.colors = colors
        this.min = min
        this.max = max
        this.bar = bar
        this.emojis = emojis
    }

    override fun format(placeholder: NumericPlaceholderHandler, data: PlaceholderData): String {
        val value = placeholder.requestValue(data) ?: return nullFormat

        val builder = StringBuilder(prefix)
        colors.floorEntry(value.toDouble())?.value?.let { builder.append(it) }

        when (defaultMode) {
            Mode.VALUE -> builder.append(value)
            Mode.ROMAN_NUMERAL -> builder.append(NumberUtil.toRomanNumeral(Math.round(value.toFloat())))
            Mode.EMOJI -> appendEmoji(data, value.toDouble(), builder)
            Mode.PERCENTAGE -> (100 * NumberUtil.invLerp(min, max, value.toDouble())).toInt()
            Mode.BAR -> bar.append(value, builder)
        }

        return builder.append(suffix).toString()
    }

    fun appendEmoji(data: PlaceholderData, value: Double, builder: StringBuilder) {
        var remainingValue = value
        var counter = 0 // Count the number of iterations

        while (counter < 50) {  // Limit to 50 iterations
            val unit = emojis.floorKey(remainingValue)?.toDouble() ?: return
            builder.append(emojis[unit])
            remainingValue -= unit
            counter++
        }

        WeaponMechanicsPlus.getDebug().debug("Could not add emojis correctly when formatting '$builder' for $data")
    }


    override fun serialize(data: SerializeData): NumericPlaceholderFormat {
        val defaultMode = data.of("Default_Mode").assertExists().getEnum(Mode::class.java)
        val prefix = data.of("Prefix").assertExists().adventure
        val suffix = data.of("Suffix").assertExists().adventure
        val nullFormat = data.of("Null_Format").assertExists().adventure

        val colors = TreeMap<Double, String>()
        for ((index, colorLine) in data.of("Colors").assertExists().assertType(List::class.java).get<List<String>>().withIndex()) {
            val splitIndex = colorLine.indexOf(' ')
            val numberString = colorLine.substring(0, splitIndex)
            val colorString = colorLine.substring(splitIndex + 1)
            val double = numberString.toDoubleOrNull() ?: throw data.listException(
                "Colors",
                index,
                "Expected a number before the color, but got '$numberString'"
            )
            colors[double] = colorString
        }

        val min = data.of("Min").assertExists().double
        val max = data.of("Max").assertExists().double

        val leftColor = data.of("Bar.Left_Color").assertExists().adventure
        val rightColor = data.of("Bar.Right_Color").assertExists().adventure
        val leftSymbol = data.of("Bar.Left_Symbol").assertExists().adventure
        val rightSymbol = data.of("Bar.Right_Symbol").getAdventure(leftSymbol)
        val symbolAmount = data.of("Bar.Symbol_Amount").assertExists().assertPositive().int
        val bar = StringBar(leftColor, rightColor, leftSymbol, rightSymbol, symbolAmount)

        val emojis = TreeMap<Double, String>()
        for ((index, colorLine) in data.of("Emojis").assertExists().assertType(List::class.java).get<List<String>>().withIndex()) {
            val splitIndex = colorLine.indexOf(' ')
            val numberString = colorLine.substring(0, splitIndex)
            val emojiString = colorLine.substring(splitIndex + 1)
            val double = numberString.toDoubleOrNull() ?: throw data.listException(
                "Emojis",
                index,
                "Expected a number before the emoji, but got '$numberString'"
            )
            emojis[double] = emojiString
        }

        return NumericPlaceholderFormat(defaultMode, prefix, suffix, nullFormat, colors, min, max, bar, emojis)
    }

    inner class StringBar(
        val leftColor: String,
        val rightColor: String,
        val leftSymbol: String,
        val rightSymbol: String,
        val symbolAmount: Int,
    ) {
        fun append(value: Number, builder: StringBuilder) {
            val delimiterIndex: Int = (NumberUtil.invLerp(min, max, value.toDouble()) * symbolAmount).toInt()

            builder.append(leftColor)

            for (i in 0 until symbolAmount) {
                if (i < delimiterIndex) {
                    builder.append(leftSymbol)
                } else if (i == delimiterIndex) {
                    builder.append(rightColor)
                    builder.append(rightSymbol)
                } else {
                    builder.append(rightSymbol)
                }
            }
        }
    }

    enum class Mode {
        VALUE,
        ROMAN_NUMERAL,
        EMOJI,
        PERCENTAGE,
        BAR
    }
}