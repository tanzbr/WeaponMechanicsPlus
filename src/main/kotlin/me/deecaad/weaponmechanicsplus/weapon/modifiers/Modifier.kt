package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*

class Modifier : Serializer<Modifier> {

    val damage: DamageModifier?
    val explosion: ExplosionModifier?
    val info: InfoModifier?
    val melee: MeleeModifier?
    val projectile: ProjectileModifier?
    val reload: ReloadModifier?
    val scope: ScopeModifier?
    val shoot: ShootModifier?

    /**
     * Default constructor for serializer.
     */
    constructor() {
        this.damage = null
        this.explosion = null
        this.info = null
        this.melee = null
        this.projectile = null
        this.reload = null
        this.scope = null
        this.shoot = null
    }

    constructor(
        damageModifier: DamageModifier?,
        explosionModifier: ExplosionModifier?,
        infoModifier: InfoModifier?,
        meleeModifier: MeleeModifier?,
        projectileModifier: ProjectileModifier?,
        reloadModifier: ReloadModifier?,
        scopeModifier: ScopeModifier?,
        shootModifier: ShootModifier?
    ) {
        this.damage = damageModifier
        this.explosion = explosionModifier
        this.info = infoModifier
        this.melee = meleeModifier
        this.projectile = projectileModifier
        this.reload = reloadModifier
        this.scope = scopeModifier
        this.shoot = shootModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): Modifier {
        val damageModifier = data.of("Damage_Modifier").serialize(DamageModifier::class.java)
        val explosionModifier = data.of("Explosion_Modifier").serialize(ExplosionModifier::class.java)
        val infoModifier = data.of("Info_Modifier").serialize(InfoModifier::class.java)
        val meleeModifier = data.of("Melee_Modifier").serialize(MeleeModifier::class.java)
        val projectileModifier = data.of("Projectile_Modifier").serialize(ProjectileModifier::class.java)
        val reloadModifier = data.of("Reload_Modifier").serialize(ReloadModifier::class.java)
        val scopeModifier = data.of("Scope_Modifier").serialize(ScopeModifier::class.java)
        val shootModifier = data.of("Shoot_Modifier").serialize(ShootModifier::class.java)

        return Modifier(
            damageModifier,
            explosionModifier,
            infoModifier,
            meleeModifier,
            projectileModifier,
            reloadModifier,
            scopeModifier,
            shootModifier
        )
    }
}