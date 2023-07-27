package me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments

import me.deecaad.core.file.*
import me.deecaad.core.file.serializers.ItemSerializer
import me.deecaad.core.mechanics.Mechanics
import me.deecaad.weaponmechanics.utils.CustomTag
import me.deecaad.weaponmechanicsplus.WeaponMechanicsPlusAPI
import me.deecaad.weaponmechanicsplus.weapon.modifiers.ModifierBase
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.Whitelist
import org.bukkit.inventory.ItemStack

class Attachment : ModifierBase, Comparable<Attachment> {

    lateinit var attachmentTitle: String
    var maximumStackAmount = 1
    lateinit var item: ItemStack
    var attachmentRequireList: Set<String> = setOf()
    var attachmentDenyList: Set<String> = setOf()
    lateinit var weaponWhitelist: Whitelist<String>
    var denyMechanics: Mechanics? = null
    var equipMechanics: Mechanics? = null
    var dequipMechanics: Mechanics? = null
    var unlockable: Unlockable? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(attachmentTitle: String, maximumStackAmount: Int, item: ItemStack, attachmentRequireList: Set<String>,
                attachmentDenyList: Set<String>, weaponWhitelist: Whitelist<String>, unlockable: Unlockable?,
                denyMechanics: Mechanics?, equipMechanics: Mechanics?, dequipMechanics: Mechanics?) {
        this.attachmentTitle = attachmentTitle
        this.maximumStackAmount = maximumStackAmount
        this.item = item
        this.attachmentRequireList = attachmentRequireList
        this.attachmentDenyList = attachmentDenyList
        this.weaponWhitelist = weaponWhitelist
        this.unlockable = unlockable
        this.denyMechanics = denyMechanics
        this.equipMechanics = equipMechanics
        this.dequipMechanics = dequipMechanics
    }

    /**
     * Returns a copy of the attachment item.
     */
    fun generateItem() = item.clone()

    /**
     * Returns `true` if at least 1 more of this attachment can be
     * added to the given weapon.
     *
     * @param weapon The non-null weapon to check.
     * @return true if the attachment can be added.
     */
    fun canAttach(weapon: ItemStack?): Boolean {
        val weaponTitle = CustomTag.WEAPON_TITLE.getString(weapon) ?: throw IllegalArgumentException()
        if (!weaponWhitelist.isWhitelisted(weaponTitle)) return false

        // If there are no attachments currently on the weapon, then we can attach
        val attached = WeaponMechanicsPlusAPI.getAttachments(weapon) ?: return true
        var duplicateCount = 0
        for (attachment in attached) {
            if (attachment === this) {
                duplicateCount++
                continue
            }

            // Some attachments are not compatible with each other
            if (attachmentRequireList.isNotEmpty() && !attachmentRequireList.contains(attachment.attachmentTitle)) return false
            if (attachmentDenyList.contains(attachment.attachmentTitle)) return false
        }

        // Cannot attach the same attachment multiple times
        return duplicateCount < maximumStackAmount
    }

    fun attach(weapon: ItemStack?) {
        var array = CustomTag.ATTACHMENTS.getStringArray(weapon)
        if (array == null)
            array = emptyArray()

        val attachments: MutableList<Attachment> = ArrayList(array.size + 1)
        for (id in array) {
            val attachment = AttachmentRegistry.INSTANCE[id]

            // attachment is null when the admin deletes an attachment from
            // config after it is attached to a weapon.
            if (attachment != null)
                attachments.add(attachment)
        }

        // Add the new attachment and sort
        attachments.add(this)
        attachments.sort()
        CustomTag.ATTACHMENTS.setStringArray(weapon, attachments.map { it.attachmentTitle }.toTypedArray())
    }

    override fun compareTo(other: Attachment): Int {
        return priority.compareTo(other.priority)
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): Attachment {
        val attachmentTitle = data.key
        val maximumStackAmount = data.of("Maximum_Stack_Amount").assertRange(1, 100).getInt(1)

        // Make sure to use CustomTag during serialization so recipe works
        data.of("Item").assertExists()
        val tags = mapOf(Pair(CustomTag.ATTACHMENT_TITLE.key, attachmentTitle))
        val item = ItemSerializer().serializeWithTags(data.move("Item"), tags)

        val isWeaponWhitelist = data.of("Denying.Weapon_Whitelist").getBool(false)
        val weapons = data.ofList("Denying.Weapons").addArgument(String::class.java, true).assertList().get()
                .stream().map { arr: Array<String> -> arr[0] }.toList()
        val weaponWhitelist = Whitelist(isWeaponWhitelist, weapons)

        val attachmentRequireList: MutableSet<String> = HashSet()
        val attachmentDenyList: MutableSet<String> = HashSet()
        for (split in data.ofList("Denying.Attachments")
            .addArgument(State::class.java, true)
            .addArgument(String::class.java, true)
            .assertList().get()) {

            if (split[0].equals("deny", ignoreCase = true))
                attachmentDenyList.add(split[1])
            else
                attachmentRequireList.add(split[1])
        }

        val unlockable = data.of("Unlockable").serialize(Unlockable::class.java)
        val denyMechanics = data.of("Denying.Mechanics").serialize(Mechanics::class.java)
        val equipMechanics = data.of("Attach_Mechanics").serialize(Mechanics::class.java)
        val dequipMechanics = data.of("Detach_Mechanics").serialize(Mechanics::class.java)

        val returnValue = Attachment(attachmentTitle, maximumStackAmount, item, attachmentRequireList, attachmentDenyList, weaponWhitelist, unlockable, denyMechanics, equipMechanics, dequipMechanics)

        val base = super.serialize(data)
        returnValue.priority = base.priority
        returnValue.modifier = base.modifier
        returnValue.perWeaponModifiers = base.perWeaponModifiers
        return returnValue
    }

    private enum class State {
        DENY, REQUIRE
    }
}