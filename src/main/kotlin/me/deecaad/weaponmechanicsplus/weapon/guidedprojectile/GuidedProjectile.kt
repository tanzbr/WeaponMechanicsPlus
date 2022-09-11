package me.deecaad.weaponmechanicsplus.weapon.guidedprojectile

import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import org.bukkit.util.Vector;
import kotlin.math.abs
import kotlin.math.max

class GuidedProjectile : Serializer<GuidedProjectile> {

    private var maximumCurvePerTick: Float = 0F

    /**
     * Default constructor for serializer
     */
    constructor() {}

    fun rotateVector(currentDirection: Vector, aPoint: Vector, bPoint: Vector): Vector {
        return rotateVector(currentDirection, bPoint.subtract(aPoint).normalize())
    }

    fun rotateVector(currentDirection: Vector, otherDirection: Vector): Vector {
        // Current and other direction HAVE to be normalized

        // Calling angle is kinda expensive
        // I will still investigate if there is a better way
        val angle = currentDirection.angle(otherDirection)

        // If the angle is smaller than 0.05, we don't want to do anything
        // Default curve per tick is around 0.12
        if (abs(angle) < 0.05) return currentDirection

        return currentDirection.multiply((angle - maximumCurvePerTick) / angle)
            .add(otherDirection.multiply(maximumCurvePerTick / angle))
            .normalize()
    }

    fun rotateVectorAxis(motionVector: Vector, currentDirection: Vector, otherDirection: Vector): Vector {
        // Alternative way, gotta test which is better

        // At least rotateAroundAxis in Vector class requires compatibility

        val axis = currentDirection.getCrossProduct(otherDirection)

        // With this motion vector should be modified to new one without affecting the length
        return motionVector.rotateAroundNonUnitAxis(axis, maximumCurvePerTick.toDouble())
    }

    override fun getKeyword(): String {
        return "Guided_Projectile"
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): GuidedProjectile {

        return GuidedProjectile();
    }
}