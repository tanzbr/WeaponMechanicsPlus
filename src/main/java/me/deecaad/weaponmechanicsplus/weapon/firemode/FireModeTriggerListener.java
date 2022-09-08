package me.deecaad.weaponmechanicsplus.weapon.firemode;

import me.deecaad.core.file.Configuration;
import me.deecaad.weaponmechanics.mechanics.CastData;
import me.deecaad.weaponmechanics.mechanics.Mechanics;
import me.deecaad.weaponmechanics.utils.CustomTag;
import me.deecaad.weaponmechanics.weapon.info.WeaponInfoDisplay;
import me.deecaad.weaponmechanics.weapon.trigger.TriggerListener;
import me.deecaad.weaponmechanics.weapon.trigger.TriggerType;
import me.deecaad.weaponmechanics.wrappers.EntityWrapper;
import me.deecaad.weaponmechanics.wrappers.PlayerWrapper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static me.deecaad.weaponmechanics.WeaponMechanics.getConfigurations;
import static me.deecaad.weaponmechanics.WeaponMechanics.getWeaponHandler;

public class FireModeTriggerListener implements TriggerListener {

    @Override
    public boolean allowOtherTriggers() {
        return false;
    }

    @Override
    public boolean tryUse(EntityWrapper entityWrapper, String weaponTitle, ItemStack weaponStack, EquipmentSlot slot, TriggerType triggerType, boolean dualWield, @Nullable LivingEntity victim) {

        Configuration config = getConfigurations();
        FireMode fireMode = config.getObject(weaponTitle + ".Info.Fire_Mode", FireMode.class);
        if (fireMode == null || !fireMode.getTrigger().check(triggerType, slot, entityWrapper)) {
            return false;
        }

        entityWrapper.getMainHandData().cancelTasks();
        entityWrapper.getOffHandData().cancelTasks();

        Mechanics switchMechanics = fireMode.getSwitchMechanics();
        if (switchMechanics != null) switchMechanics.use(new CastData(entityWrapper, weaponTitle, weaponStack));

        String nextWeapon = fireMode.getNextMode();

        CustomTag.WEAPON_TITLE.setString(weaponStack, nextWeapon);

        WeaponInfoDisplay weaponInfoDisplay = config.getObject(weaponTitle + ".Info.Weapon_Info_Display", WeaponInfoDisplay.class);
        if (weaponInfoDisplay != null) weaponInfoDisplay.send((PlayerWrapper) entityWrapper, slot);

        getWeaponHandler().getSkinHandler().tryUse(triggerType, entityWrapper, nextWeapon, weaponStack, slot);

        return true;
    }
}