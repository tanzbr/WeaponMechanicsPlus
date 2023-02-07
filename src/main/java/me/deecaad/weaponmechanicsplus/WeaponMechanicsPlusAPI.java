package me.deecaad.weaponmechanicsplus;

import kotlin.Triple;
import me.deecaad.weaponmechanics.utils.CustomTag;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.Modifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ModifierBase;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ammotype.AmmoTypeModifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments.Attachment;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This utility class contains static methods to facilitate getting a weapon's
 * modifiers. These methods are designed to be <i>as fast as possible</i> for
 * repeated usage.
 */
public final class WeaponMechanicsPlusAPI {

    /**
     * "What problem does this solve?" you may ask. Well, we want the result
     * of {@link #getModifiers(ItemStack)} to be sorted by
     * {@link ModifierBase#getPriority()}. Sorting methods are slow, so instead
     * of sorting a list multiple times every tick, we store the result. This
     * way, the only expense is in the triplet instantiation instead of sorting.
     */
    private static final Map<Triple<int[], String, String>, List<Modifier>> CACHE = new HashMap<>();

    /**
     * Don't let anyone instantiate this class
     */
    private WeaponMechanicsPlusAPI() {
    }

    /**
     * Returns an array of all attachments currently attached to the gun. Note
     * that if you are looking to use the {@link Modifier} from the attachment,
     * you should use {@link #getModifiers(ItemStack)} instead. You should
     * check {@link ItemStack#hasItemMeta()} before calling this method.
     *
     * <p>The order of the array is in increasing attachment priority.
     *
     * @param weapon The non-null weapon to get attachments from.
     * @return The array of attachments, or null.
     */
    public static Attachment[] getAttachments(ItemStack weapon) {

        int[] attachmentIds = CustomTag.ATTACHMENTS.getArray(weapon);
        if (attachmentIds.length == 0)
            return null;

        // Get the attachment config information from each attachment id
        int size = attachmentIds.length;
        Attachment[] attachments = new Attachment[size];
        for (int i = 0; i < size; i++)
            attachments[i] = AttachmentRegistry.INSTANCE.get(attachmentIds[i]);

        return attachments;
    }

    /**
     * Returns an immutable list of all modifiers currently attached to the gun.
     * This includes modifiers from any {@link Attachment} and any
     * {@link AmmoTypeModifier}.
     *
     * @param weapon The non-null weapon to get modifiers from.
     * @return The array of modifiers, or null.
     */
    public static List<Modifier> getModifiers(ItemStack weapon) {
        String weaponTitle = CustomTag.WEAPON_TITLE.getString(weapon);
        int[] attachmentIds = CustomTag.ATTACHMENTS.getArray(weapon);

        String ammo = CustomTag.AMMO_NAME.getString(weapon);
        // todo account for ammo modifiers

        // Try to get the cached sorted modifiers list. Otherwise, make it.
        Triple<int[], String, String> triple = new Triple<>(attachmentIds, ammo, weaponTitle);
        List<Modifier> modifiers = CACHE.get(triple);
        if (modifiers == null) {

            int size = attachmentIds.length;
            Modifier[] temp = new Modifier[size];
            for (int i = 0; i < size; i++)
                temp[i] = AttachmentRegistry.INSTANCE.get(attachmentIds[i]).getModifier(weaponTitle);

            CACHE.put(triple, modifiers = Arrays.asList(temp));
        }

        return modifiers;
    }


}
