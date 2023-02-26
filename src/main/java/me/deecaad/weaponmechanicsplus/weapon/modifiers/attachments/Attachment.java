package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.SerializerException;
import me.deecaad.core.file.serializers.ItemSerializer;
import me.deecaad.core.mechanics.Mechanics;
import me.deecaad.weaponmechanics.utils.CustomTag;
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ModifierBase;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.Whitelist;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Attachment extends ModifierBase implements Comparable<Attachment> {

    private String attachmentTitle;
    private int maximumStackAmount;
    private ItemStack item;
    private Set<String> attachmentRequireList;
    private Set<String> attachmentDenyList;
    private Whitelist<String> weaponWhitelist;
    private Mechanics equipMechanics;
    private Mechanics dequipMechanics;

    private Unlockable unlockable;

    /**
     * Default constructor for serializer
     */
    public Attachment() {
    }

    public Attachment(String attachmentTitle, int maximumStackAmount, ItemStack item, Set<String> attachmentRequireList, Set<String> attachmentDenyList,
                      Whitelist<String> weaponWhitelist, Unlockable unlockable, Mechanics equipMechanics, Mechanics dequipMechanics) {
        this.attachmentTitle = attachmentTitle;
        this.maximumStackAmount = maximumStackAmount;
        this.item = item;
        this.attachmentRequireList = attachmentRequireList;
        this.attachmentDenyList = attachmentDenyList;
        this.weaponWhitelist = weaponWhitelist;
        this.unlockable = unlockable;
        this.equipMechanics = equipMechanics;
        this.dequipMechanics = dequipMechanics;
    }

    public String getAttachmentTitle() {
        return attachmentTitle;
    }

    public int getMaximumStackAmount() {
        return maximumStackAmount;
    }

    public ItemStack getItem() {
        return item;
    }

    public Set<String> getAttachmentRequireList() {
        return attachmentRequireList;
    }

    public Set<String> getAttachmentDenyList() {
        return attachmentDenyList;
    }

    public Whitelist<String> getWeaponWhitelist() {
        return weaponWhitelist;
    }

    public Mechanics getEquipMechanics() {
        return equipMechanics;
    }

    public Mechanics getDequipMechanics() {
        return dequipMechanics;
    }

    public Unlockable getUnlockable() {
        return unlockable;
    }

    /**
     * Returns <code>true</code> if at least 1 more of this attachment can be
     * added to the given weapon.
     *
     * @param weapon The non-null weapon to check.
     * @return true if the attachment can be added.
     */
    public boolean canAttach(ItemStack weapon) {
        String weaponTitle = CustomTag.WEAPON_TITLE.getString(weapon);
        if (weaponTitle == null)
            throw new IllegalArgumentException();

        if (!weaponWhitelist.isWhitelisted(weaponTitle))
            return false;

        // If there are no attachments currently on the weapon, then we can attach
        Attachment[] attached = WeaponMechanicsPlusAPI.getAttachments(weapon);
        if (attached == null)
            return true;

        int duplicateCount = 0;
        for (Attachment attachment : attached) {
            if (attachment == this) {
                duplicateCount++;
                continue;
            }

            // Some attachments are not compatible with each other
            if (!attachmentRequireList.isEmpty() && !attachmentRequireList.contains(attachment.attachmentTitle))
                return false;
            if (attachmentDenyList.contains(attachment.attachmentTitle))
                return false;
        }

        // Cannot attach the same attachment multiple times
        return duplicateCount >= maximumStackAmount;
    }

    public void attach(ItemStack weapon) {
        int[] array = CustomTag.ATTACHMENTS.getArray(weapon);
        if (array == null)
            array = new int[0];

        List<Attachment> attachments = new ArrayList<>(array.length + 1);
        for (int id : array) {
            attachments.add(AttachmentRegistry.INSTANCE.get(id));
        }

        // Add the new attachment and sort
        attachments.add(this);
        attachments.sort(null);

        int[] newArray = new int[array.length + 1];
        for (int i = 0; i < attachments.size(); i++) {
            newArray[i] = AttachmentRegistry.INSTANCE.getId(attachments.get(i));
        }

        CustomTag.ATTACHMENTS.setArray(weapon, newArray);
    }

    @Override
    public int compareTo(@NotNull Attachment o) {
        return Integer.compare(priority, o.priority);
    }

    @NotNull
    @Override
    public Attachment serialize(SerializeData data) throws SerializerException {
        String attachmentTitle = data.key;

        int maximumStackAmount = data.of("Maximum_Stack_Amount").assertRange(1, 200).getInt(1);
        ItemStack item = data.of("Item").assertExists().serialize(new ItemSerializer());

        boolean isWeaponWhitelist = data.of("Denying.Weapon_Whitelist").getBool(false);
        List<String> weapons = data.ofList("Denying.Weapons").
                addArgument(String.class, true).assertExists().assertList().get()
                .stream().map(arr -> arr[0]).toList();
        Whitelist<String> weaponWhitelist = new Whitelist<>(isWeaponWhitelist, weapons);

        Set<String> attachmentRequireList = new HashSet<>();
        Set<String> attachmentDenyList = new HashSet<>();
        for (String[] split : data.ofList("Denying.Attachments")
                .addArgument(State.class, true)
                .addArgument(String.class, true)
                .assertList().get()) {

            if (split[0].equalsIgnoreCase("deny"))
                attachmentDenyList.add(split[1]);
            else
                attachmentRequireList.add(split[1]);
        }

        Unlockable unlockable = data.of("Unlockable").serialize(Unlockable.class);

        Mechanics equipMechanics = data.of("Equip_Mechanics").serialize(Mechanics.class);
        Mechanics dequipMechanics = data.of("Dequip_Mechanics").serialize(Mechanics.class);

        ModifierBase base = super.serialize(data);
        Attachment returnValue = new Attachment(attachmentTitle, maximumStackAmount, item, attachmentRequireList, attachmentDenyList, weaponWhitelist, unlockable, equipMechanics, dequipMechanics);
        returnValue.priority = base.getPriority();
        returnValue.modifier = base.getModifier();
        returnValue.perWeaponModifiers = base.getPerWeaponModifiers();
        return returnValue;
    }

    private enum State {
        DENY, REQUIRE
    }
}