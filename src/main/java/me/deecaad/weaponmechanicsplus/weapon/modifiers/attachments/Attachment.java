package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.SerializerException;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ModifierBase;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Attachment extends ModifierBase {

    private int priority;
    private int maximumStackAmount;

    private ItemStack attachmentStack;

    private boolean whitelist;
    private Set<String> attachmentsWhichDeny;

    private Unlockable unlockable;

    @Override
    public String getKeyword() {
        return "Attachment";
    }

    @NotNull
    @Override
    public Attachment serialize(SerializeData data) throws SerializerException {
        ModifierBase base = super.serialize(data);

        return null;
    }
}