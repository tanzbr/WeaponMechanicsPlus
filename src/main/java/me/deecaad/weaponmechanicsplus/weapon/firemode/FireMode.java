package me.deecaad.weaponmechanicsplus.weapon.firemode;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.mechanics.Mechanics;
import me.deecaad.weaponmechanics.weapon.trigger.Trigger;
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlus;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class FireMode implements Serializer<FireMode> {

    private Trigger trigger;
    private String nextMode;
    private Mechanics switchMechanics;

    /**
     * Default constructor for serializer
     */
    public FireMode() { }

    public FireMode(Trigger trigger, String nextMode, Mechanics switchMechanics) {
        this.trigger = trigger;
        this.nextMode = nextMode;
        this.switchMechanics = switchMechanics;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public String getNextMode() {
        return nextMode;
    }

    public Mechanics getSwitchMechanics() {
        return switchMechanics;
    }

    @Override
    public String getKeyword() {
        return "Fire_Mode";
    }

    @NotNull
    @Override
    public FireMode serialize(SerializeData data) throws SerializerException {
        Trigger trigger = data.of("Trigger").assertExists().serialize(Trigger.class);
        String nextMode = data.of("Next_Mode").assertExists().assertType(String.class).get();
        Mechanics switchMechanics = data.of("Mechanics").serialize(Mechanics.class);

        // Late check on weapon name
        new BukkitRunnable() {
            public void run() {
                if (!WeaponMechanics.getWeaponHandler().getInfoHandler().hasWeapon(nextMode)) {
                    data.exception("Next_Mode", "Could not find weapon named " + nextMode,
                            SerializerException.didYouMean(nextMode, WeaponMechanics.getWeaponHandler().getInfoHandler().getSortedWeaponList()))
                            .log(WeaponMechanicsPlus.getDebug());
                }
            }
        }.runTask(WeaponMechanicsPlus.getPlugin());

        return new FireMode(trigger, nextMode, switchMechanics);
    }
}
