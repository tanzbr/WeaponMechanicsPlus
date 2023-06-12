package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.weapon.explode.Explosion

class ExplosionModifier : Serializer<ExplosionModifier> {

    var overrideExplosion: Explosion? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(overrideExplosion: Explosion?) {
        this.overrideExplosion = overrideExplosion
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ExplosionModifier {
        val overrideExplosion = data.of("Override_Explosion").serialize(Explosion::class.java)
        return ExplosionModifier(overrideExplosion)
    }
}