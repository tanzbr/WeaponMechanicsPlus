package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.SerializerException;
import me.deecaad.core.file.serializers.ItemSerializer;
import me.deecaad.core.mechanics.Mechanics;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ModifierBase;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.Whitelist;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Attachment extends ModifierBase implements Comparable<Attachment> {

    private int maximumStackAmount;
    private ItemStack item;
    private Whitelist<String> attachmentWhitelist;
    private Whitelist<String> weaponWhitelist;
    private Mechanics equipMechanics;
    private Mechanics dequipMechanics;

    private Unlockable unlockable;

    /**
     * Default constructor for serializer
     */
    public Attachment() {
    }

    public Attachment(int maximumStackAmount, ItemStack item, Whitelist<String> attachmentWhitelist,
                      Whitelist<String> weaponWhitelist, Unlockable unlockable, Mechanics equipMechanics, Mechanics dequipMechanics) {
        this.maximumStackAmount = maximumStackAmount;
        this.item = item;
        this.attachmentWhitelist = attachmentWhitelist;
        this.weaponWhitelist = weaponWhitelist;
        this.unlockable = unlockable;
        this.equipMechanics = equipMechanics;
        this.dequipMechanics = dequipMechanics;
    }

    @Override
    public int compareTo(@NotNull Attachment o) {
        return Integer.compare(priority, o.priority);
    }

    @NotNull
    @Override
    public Attachment serialize(SerializeData data) throws SerializerException {

        int maximumStackAmount = data.of("Maximum_Stack_Amount").assertRange(1, 200).getInt(1);
        ItemStack item = data.of("Item").assertExists().serialize(new ItemSerializer());

        boolean isAttachmentWhitelist = data.of("Conditions.Attachment_Whitelist").getBool(false);
        List<String> attachments = data.ofList("Conditions.Attachments").
                addArgument(String.class, true).assertExists().assertList().get()
                .stream().map(arr -> arr[0]).toList();
        Whitelist<String> attachmentWhitelist = new Whitelist<>(isAttachmentWhitelist, attachments);

        boolean isWeaponWhitelist = data.of("Conditions.Weapon_Whitelist").getBool(false);
        List<String> weapons = data.ofList("Conditions.Weapons").
                addArgument(String.class, true).assertExists().assertList().get()
                .stream().map(arr -> arr[0]).toList();
        Whitelist<String> weaponWhitelist = new Whitelist<>(isWeaponWhitelist, weapons);

        Unlockable unlockable = data.of("Unlockable").serialize(Unlockable.class);

        Mechanics equipMechanics = data.of("Equip_Mechanics").serialize(Mechanics.class);
        Mechanics dequipMechanics = data.of("Dequip_Mechanics").serialize(Mechanics.class);

        ModifierBase base = super.serialize(data);
        Attachment returnValue = new Attachment(maximumStackAmount, item, attachmentWhitelist, weaponWhitelist, unlockable, equipMechanics, dequipMechanics);
        returnValue.priority = base.getPriority();
        returnValue.modifier = base.getModifier();
        returnValue.perWeaponModifiers = base.getPerWeaponModifiers();
        return returnValue;
    }
}