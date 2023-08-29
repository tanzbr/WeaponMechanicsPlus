package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*

class Modifier : Serializer<Modifier> {

    var armor: ArmorModifier? = null
    var damage: DamageModifier? = null
    var explosion: ExplosionModifier? = null
    var info: InfoModifier? = null
    var melee: MeleeModifier? = null
    var projectile: ProjectileModifier? = null
    var reload: ReloadModifier? = null
    var scope: ScopeModifier? = null
    var shoot: ShootModifier? = null

    /**
     * Default constructor for serializer.
     */
    constructor()

    constructor(
        armorModifier: ArmorModifier?,
        damageModifier: DamageModifier?,
        explosionModifier: ExplosionModifier?,
        infoModifier: InfoModifier?,
        meleeModifier: MeleeModifier?,
        projectileModifier: ProjectileModifier?,
        reloadModifier: ReloadModifier?,
        scopeModifier: ScopeModifier?,
        shootModifier: ShootModifier?
    ) {
        this.armor = armorModifier
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
        val armorModifier = data.of("Armor_Modifier").serialize(ArmorModifier::class.java)
        val damageModifier = data.of("Damage_Modifier").serialize(DamageModifier::class.java)
        val explosionModifier = data.of("Explosion_Modifier").serialize(ExplosionModifier::class.java)
        val infoModifier = data.of("Info_Modifier").serialize(InfoModifier::class.java)
        val meleeModifier = data.of("Melee_Modifier").serialize(MeleeModifier::class.java)
        val projectileModifier = data.of("Projectile_Modifier").serialize(ProjectileModifier::class.java)
        val reloadModifier = data.of("Reload_Modifier").serialize(ReloadModifier::class.java)
        val scopeModifier = data.of("Scope_Modifier").serialize(ScopeModifier::class.java)
        val shootModifier = data.of("Shoot_Modifier").serialize(ShootModifier::class.java)

        return Modifier(
            armorModifier,
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