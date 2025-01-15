package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.core.utils.EnumUtil
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.*
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier
import me.deecaad.core.file.simple.DoubleSerializer
import me.deecaad.core.file.simple.EnumValueSerializer
import java.util.*
import kotlin.jvm.optionals.getOrNull

class ScopeModifier : Serializer<ScopeModifier> {

    var zoomAmount: DoubleModifier? = null
    var isNightVision: Boolean? = null
    var isPumpkinOverlay: Boolean? = null
    var zoomStacking: List<DoubleModifier?> = listOf()
    var mechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(
        zoomAmount: DoubleModifier?,
        isNightVision: Boolean?,
        isPumpkinOverlay: Boolean?,
        zoomStacking: List<DoubleModifier?>,
        mechanicsModifier: MechanicsModifier?,
    ) {
        this.zoomAmount = zoomAmount
        this.isNightVision = isNightVision
        this.isPumpkinOverlay = isPumpkinOverlay
        this.zoomStacking = zoomStacking
        this.mechanicsModifier = mechanicsModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ScopeModifier {
        val zoomAmount = data.of("Zoom_Amount").serialize(DoubleModifier::class.java).getOrNull()
        val isNightVision = data.of("Night_Vision").assertExists().getBool().getOrNull()
        val isPumpkinOverlay = data.of("Pumpkin_Overlay").assertExists().getBool().getOrNull()

        val splits = data.ofList("Zoom_Stacking")
            .addArgument(EnumValueSerializer(Operation::class.java, false))
            .addArgument(DoubleSerializer())
            .requireAllPreviousArgs()
            .assertList()

        val zoomStacking: MutableList<DoubleModifier> = ArrayList()
        for (split in splits) {
            val operation = (split[0].get() as List<Operation>).first()
            val number = split[1].get() as Double
            zoomStacking.add(DoubleModifier(operation, number))
        }

        val mechanicsModifier = data.serializeMechanicsModifier()

        return ScopeModifier(zoomAmount, isNightVision, isPumpkinOverlay, zoomStacking, mechanicsModifier)
    }
}