package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.core.utils.EnumUtil
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.*
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier
import java.util.*

class ScopeModifier : Serializer<ScopeModifier> {

    var zoomAmount: DoubleModifier? = null
    var isNightVision: Boolean? = null
    var zoomStacking: List<DoubleModifier?> = listOf()
    var mechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(zoomAmount: DoubleModifier?, isNightVision: Boolean?, zoomStacking: List<DoubleModifier?>,
                mechanicsModifier: MechanicsModifier?) {
        this.zoomAmount = zoomAmount
        this.isNightVision = isNightVision
        this.zoomStacking = zoomStacking
        this.mechanicsModifier = mechanicsModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ScopeModifier {
        val zoomAmount = data.of("Zoom_Amount").serialize(DoubleModifier::class.java)
        val isNightVision = if (data.has("Night_Vision")) data.of("Night_Vision").assertExists().bool else null

        val splits = data.ofList("Zoom_Stacking")
            .addArgument(Operation::class.java, true)
            .addArgument(Double::class.javaPrimitiveType, true)
            .assertList().get()

        val zoomStacking: MutableList<DoubleModifier> = ArrayList()
        for (split in splits) {
            val math = EnumUtil.getIfPresent(Operation::class.java, split[0]).orElseThrow()
            zoomStacking.add(DoubleModifier(math, split[1].toDouble()))
        }

        val mechanicsModifier = data.serializeMechanicsModifier()

        return ScopeModifier(zoomAmount, isNightVision, zoomStacking, mechanicsModifier)
    }
}