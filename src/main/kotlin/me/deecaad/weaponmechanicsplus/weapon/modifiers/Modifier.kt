package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*

class Modifier : Serializer<Modifier> {
    var damageModifier: DamageModifier? = null
        private set
    var explosionModifier: ExplosionModifier? = null
        private set
    var infoModifier: InfoModifier? = null
        private set
    var meleeModifier: MeleeModifier? = null
        private set
    var reloadModifier: ReloadModifier? = null
        private set
    var scopeModifier: ScopeModifier? = null
        private set
    var shootModifier: ShootModifier? = null
        private set

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(damageModifier: DamageModifier?, explosionModifier: ExplosionModifier?, infoModifier: InfoModifier?, meleeModifier: MeleeModifier?, reloadModifier: ReloadModifier?, scopeModifier: ScopeModifier?, shootModifier: ShootModifier?) {
        this.damageModifier = damageModifier
        this.explosionModifier = explosionModifier
        this.infoModifier = infoModifier
        this.meleeModifier = meleeModifier
        this.reloadModifier = reloadModifier
        this.scopeModifier = scopeModifier
        this.shootModifier = shootModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): Modifier {
        val damageModifier = data.of("Damage_Modifier").serialize(DamageModifier::class.java)
        val explosionModifier = data.of("Explosion_Modifier").serialize(ExplosionModifier::class.java)
        val infoModifier = data.of("Info_Modifier").serialize(InfoModifier::class.java)
        val meleeModifier = data.of("Melee_Modifier").serialize(MeleeModifier::class.java)
        val reloadModifier = data.of("Reload_Modifier").serialize(ReloadModifier::class.java)
        val scopeModifier = data.of("Scope_Modifier").serialize(ScopeModifier::class.java)
        val shootModifier = data.of("Shoot_Modifier").serialize(ShootModifier::class.java)
        return Modifier(damageModifier, explosionModifier, infoModifier, meleeModifier, reloadModifier, scopeModifier, shootModifier)
    }
}