package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments;

import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Stores all registered attachments by both name and id. Since
 * {@link org.bukkit.persistence.PersistentDataType} does not have a wrapper
 * for <code>String[]</code>, we have to use an <code>int[]</code> to store
 * attachments in items. So instead of storing the "title" of the attachment,
 * we created an arbitrary "id" and store that instead.
 *
 * @see me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI#getAttachments(ItemStack)
 * @see me.deecaad.weaponmechanics.utils.CustomTag#ATTACHMENTS
 */
public class AttachmentRegistry {

    public static final AttachmentRegistry INSTANCE = new AttachmentRegistry();

    private final Map<String, Attachment> byTitle;
    private final List<Attachment> byId;

    private AttachmentRegistry() {
        this.byTitle = new LinkedHashMap<>();
        this.byId = new ArrayList<>();
    }

    public Attachment get(int id) {
        return byId.get(id);
    }
}
