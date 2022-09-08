package me.deecaad.weaponmechanicsplus.weapon.attachments;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class Attachment implements Serializer<Attachment> {

    private int priority;
    private int maximumStackAmount;

    private ItemStack attachmentStack;

    private boolean whitelist;
    private Set<String> attachmentsWhichDeny;

    private Unlockable unlockable;

    private AttachmentModifier attachmentModifier;
    private Map<String, AttachmentModifier> perWeaponModifiers;

    @Override
    public String getKeyword() {
        return "Attachment";
    }

    @NotNull
    @Override
    public Attachment serialize(SerializeData data) throws SerializerException {
        return null;
    }
}