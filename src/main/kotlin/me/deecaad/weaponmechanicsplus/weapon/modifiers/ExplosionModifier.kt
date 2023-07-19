package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.core.mechanics.Mechanics
import me.deecaad.weaponmechanics.weapon.explode.Explosion
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier

class ExplosionModifier : Serializer<ExplosionModifier> {

    var overrideExplosion: Explosion? = null
    var mechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(overrideExplosion: Explosion?, mechanicsModifier: MechanicsModifier?) {
        this.overrideExplosion = overrideExplosion
        this.mechanicsModifier = mechanicsModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ExplosionModifier {
        val overrideExplosion = data.of("Override_Explosion").serialize(Explosion::class.java)
        val mechanicsModifier = data.serializeMechanicsModifier()

        return ExplosionModifier(overrideExplosion, mechanicsModifier)
    }
}