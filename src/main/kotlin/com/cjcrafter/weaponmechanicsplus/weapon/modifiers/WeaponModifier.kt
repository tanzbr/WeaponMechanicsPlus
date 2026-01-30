/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import kotlin.jvm.optionals.getOrNull

class WeaponModifier : Serializer<WeaponModifier> {

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
    override fun serialize(data: SerializeData): WeaponModifier {
        val damageModifier = data.of("Damage_Modifier").serialize(DamageModifier::class.java).getOrNull()
        val explosionModifier = data.of("Explosion_Modifier").serialize(ExplosionModifier::class.java).getOrNull()
        val infoModifier = data.of("Info_Modifier").serialize(InfoModifier::class.java).getOrNull()
        val meleeModifier = data.of("Melee_Modifier").serialize(MeleeModifier::class.java).getOrNull()
        val projectileModifier = data.of("Projectile_Modifier").serialize(ProjectileModifier::class.java).getOrNull()
        val reloadModifier = data.of("Reload_Modifier").serialize(ReloadModifier::class.java).getOrNull()
        val scopeModifier = data.of("Scope_Modifier").serialize(ScopeModifier::class.java).getOrNull()
        val shootModifier = data.of("Shoot_Modifier").serialize(ShootModifier::class.java).getOrNull()

        return WeaponModifier(
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