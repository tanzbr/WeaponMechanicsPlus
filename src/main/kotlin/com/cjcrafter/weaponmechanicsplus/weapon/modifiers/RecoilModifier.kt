/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.util.FloatModifier
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import kotlin.jvm.optionals.getOrNull

class RecoilModifier : Serializer<RecoilModifier> {

    var recoilMeanX: FloatModifier? = null
    var recoilMeanY: FloatModifier? = null
    var recoilVarianceX: FloatModifier? = null
    var recoilVarianceY: FloatModifier? = null
    var recoilSpeed: FloatModifier? = null
    var damping: FloatModifier? = null
    var dampingRecovery: FloatModifier? = null
    var smoothingFactor: FloatModifier? = null

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(
        meanX: FloatModifier?,
        meanY: FloatModifier?,
        varianceX: FloatModifier?,
        varianceY: FloatModifier?,
        speed: FloatModifier?,
        damping: FloatModifier?,
        dampingRecovery: FloatModifier?,
        smoothingFactor: FloatModifier?,
    ) {
        this.recoilMeanX = meanX
        this.recoilMeanY = meanY
        this.recoilVarianceX = varianceX
        this.recoilVarianceY = varianceY
        this.recoilSpeed = speed
        this.damping = damping
        this.dampingRecovery = dampingRecovery
        this.smoothingFactor = smoothingFactor
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): RecoilModifier {
        val meanX = data.of("Mean_X").serialize(FloatModifier::class.java).getOrNull()
        val meanY = data.of("Mean_Y").serialize(FloatModifier::class.java).getOrNull()
        val varianceX = data.of("Variance_X").serialize(FloatModifier::class.java).getOrNull()
        val varianceY = data.of("Variance_Y").serialize(FloatModifier::class.java).getOrNull()
        val speed = data.of("Speed").serialize(FloatModifier::class.java).getOrNull()
        val damping = data.of("Damping").serialize(FloatModifier::class.java).getOrNull()
        val dampingRecovery = data.of("Damping_Recovery").serialize(FloatModifier::class.java).getOrNull()
        val smoothingFactor = data.of("Smoothing_Factor").serialize(FloatModifier::class.java).getOrNull()

        return RecoilModifier(
            meanX,
            meanY,
            varianceX,
            varianceY,
            speed,
            damping,
            dampingRecovery,
            smoothingFactor,
        )
    }
}
