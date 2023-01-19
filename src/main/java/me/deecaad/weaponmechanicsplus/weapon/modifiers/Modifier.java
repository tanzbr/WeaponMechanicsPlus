package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import org.jetbrains.annotations.NotNull;

public class Modifier implements Serializer<Modifier> {

    private DamageModifier damageModifier;
    private ExplosionModifier explosionModifier;
    private InfoModifier infoModifier;
    private MeleeModifier meleeModifier;
    private ReloadModifier reloadModifier;
    private ScopeModifier scopeModifier;
    private ShootModifier shootModifier;

    /**
     * Default constructor for serializer.
     */
    public Modifier() {
    }

    public Modifier(DamageModifier damageModifier, ExplosionModifier explosionModifier, InfoModifier infoModifier,
                    MeleeModifier meleeModifier, ReloadModifier reloadModifier, ScopeModifier scopeModifier, ShootModifier shootModifier) {
        this.damageModifier = damageModifier;
        this.explosionModifier = explosionModifier;
        this.infoModifier = infoModifier;
        this.meleeModifier = meleeModifier;
        this.reloadModifier = reloadModifier;
        this.scopeModifier = scopeModifier;
        this.shootModifier = shootModifier;
    }

    public DamageModifier getDamageModifier() {
        return damageModifier;
    }

    public ExplosionModifier getExplosionModifier() {
        return explosionModifier;
    }

    public InfoModifier getInfoModifier() {
        return infoModifier;
    }

    public MeleeModifier getMeleeModifier() {
        return meleeModifier;
    }

    public ReloadModifier getReloadModifier() {
        return reloadModifier;
    }

    public ScopeModifier getScopeModifier() {
        return scopeModifier;
    }

    public ShootModifier getShootModifier() {
        return shootModifier;
    }

    @NotNull
    @Override
    public Modifier serialize(SerializeData data) throws SerializerException {
        DamageModifier damageModifier = data.of("Damage_Modifier").serialize(DamageModifier.class);
        ExplosionModifier explosionModifier = data.of("Explosion_Modifier").serialize(ExplosionModifier.class);
        InfoModifier infoModifier = data.of("Info_Modifier").serialize(InfoModifier.class);
        MeleeModifier meleeModifier = data.of("Melee_Modifier").serialize(MeleeModifier.class);
        ReloadModifier reloadModifier = data.of("Reload_Modifier").serialize(ReloadModifier.class);
        ScopeModifier scopeModifier = data.of("Scope_Modifier").serialize(ScopeModifier.class);
        ShootModifier shootModifier = data.of("Shoot_Modifier").serialize(ShootModifier.class);

        return new Modifier(damageModifier, explosionModifier, infoModifier, meleeModifier, reloadModifier, scopeModifier, shootModifier);
    }
}