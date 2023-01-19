package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier;
import org.jetbrains.annotations.NotNull;

public class ReloadModifier implements Serializer<ReloadModifier> {

    private IntegerModifier magazineSize;
    private IntegerModifier ammoPerReload;
    private IntegerModifier reloadDuration;
    private IntegerModifier shootDelayAfterReload;

    /**
     * Default constructor for serializer
     */
    public ReloadModifier() {
    }

    public ReloadModifier(IntegerModifier magazineSize, IntegerModifier ammoPerReload, IntegerModifier reloadDuration, IntegerModifier shootDelayAfterReload) {
        this.magazineSize = magazineSize;
        this.ammoPerReload = ammoPerReload;
        this.reloadDuration = reloadDuration;
        this.shootDelayAfterReload = shootDelayAfterReload;
    }

    @NotNull
    @Override
    public ReloadModifier serialize(SerializeData data) throws SerializerException {
        IntegerModifier magazineSize = data.of("Magazine_Size").serialize(IntegerModifier.class);
        IntegerModifier ammoPerReload = data.of("Ammo_Per_Reload").serialize(IntegerModifier.class);
        IntegerModifier reloadDuration = data.of("Reload_Duration").serialize(IntegerModifier.class);
        IntegerModifier shootDelayAfterReload = data.of("Shoot_Delay_After_Reload").serialize(IntegerModifier.class);

        return new ReloadModifier(magazineSize, ammoPerReload, reloadDuration, shootDelayAfterReload);
    }
}
