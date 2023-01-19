package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanics.weapon.damage.DamageDropoff;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.ModifierHelper;
import org.jetbrains.annotations.NotNull;

public class DamageModifier implements Serializer<DamageModifier>, ModifierHelper {

    private DoubleModifier damage;
    private DoubleModifier explosionDamage;
    private IntegerModifier fireTicks;
    private IntegerModifier armorDamage;
    private DamageDropoff dropoffOverride;

    public DamageModifier(DoubleModifier damage, DoubleModifier explosionDamage, IntegerModifier fireTicks, IntegerModifier armorDamage, DamageDropoff dropoffOverride) {
        this.damage = orDefault(damage);
        this.explosionDamage = orDefault(explosionDamage);
        this.fireTicks = orDefault(fireTicks);
        this.armorDamage = orDefault(armorDamage);
        this.dropoffOverride = dropoffOverride;
    }

    public DoubleModifier getDamage() {
        return damage;
    }

    public DoubleModifier getExplosionDamage() {
        return explosionDamage;
    }

    public IntegerModifier getFireTicks() {
        return fireTicks;
    }

    public IntegerModifier getArmorDamage() {
        return armorDamage;
    }

    public DamageDropoff getDropoffOverride() {
        return dropoffOverride;
    }

    @NotNull
    @Override
    public DamageModifier serialize(SerializeData data) throws SerializerException {
        DoubleModifier damage = data.of("Base_Damage").serialize(DoubleModifier.class);
        DoubleModifier explosionDamage = data.of("Base_Explosion_Damage").serialize(DoubleModifier.class);
        IntegerModifier fireTicks = data.of("Fire_Ticks").serialize(IntegerModifier.class);
        IntegerModifier armorDamage = data.of("Armor_Damage").serialize(IntegerModifier.class);
        DamageDropoff dropoffOverride = data.of("Damage_Dropoff").serialize(DamageDropoff.class);

        return new DamageModifier(damage, explosionDamage, fireTicks, armorDamage, dropoffOverride);
    }
}
