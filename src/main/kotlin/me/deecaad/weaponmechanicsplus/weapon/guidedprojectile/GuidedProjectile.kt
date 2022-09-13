package me.deecaad.weaponmechanicsplus.weapon.guidedprojectile

import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import me.deecaad.weaponmechanics.weapon.projectile.AProjectile
import org.bukkit.util.Vector;
import kotlin.math.abs
import kotlin.math.max

class GuidedProjectile : Serializer<GuidedProjectile> {

    private var maximumCurvePerTick: Float = 0F

    /**
     * Default constructor for serializer
     */
    constructor() {
        maximumCurvePerTick = 0.12F
    }

    fun rotateVector(currentDirection: Vector, aPoint: Vector, bPoint: Vector): Vector {
        return rotateVector(currentDirection, bPoint.subtract(aPoint).normalize())
    }

    fun rotateVector(currentDirection: Vector, otherDirection: Vector): Vector {
        // Current and other direction HAVE to be normalized

        val angle = currentDirection.angle(otherDirection)

        if (abs(angle) < maximumCurvePerTick) return currentDirection

        return currentDirection.multiply((angle - maximumCurvePerTick) / angle)
            .add(otherDirection.multiply(maximumCurvePerTick / angle))
            .normalize()
    }

    override fun getKeyword(): String {
        return "Guided_Projectile"
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): GuidedProjectile {

        return GuidedProjectile();
    }
}