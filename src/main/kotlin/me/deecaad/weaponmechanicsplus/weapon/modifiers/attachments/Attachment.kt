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
        private set
    var maximumStackAmount = 1
        private set
    lateinit var item: ItemStack
        private set
    var attachmentRequireList: Set<String> = setOf()
        private set
    var attachmentDenyList: Set<String> = setOf()
        private set
    lateinit var weaponWhitelist: Whitelist<String>
        private set
    var equipMechanics: Mechanics? = null
        private set
    var dequipMechanics: Mechanics? = null
        private set
    var unlockable: Unlockable? = null
        private set

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(attachmentTitle: String, maximumStackAmount: Int, item: ItemStack, attachmentRequireList: Set<String>,
                attachmentDenyList: Set<String>, weaponWhitelist: Whitelist<String>, unlockable: Unlockable?, equipMechanics: Mechanics?, dequipMechanics: Mechanics?) {
        this.attachmentTitle = attachmentTitle
        this.maximumStackAmount = maximumStackAmount
        this.item = item
        this.attachmentRequireList = attachmentRequireList
        this.attachmentDenyList = attachmentDenyList
        this.weaponWhitelist = weaponWhitelist
        this.unlockable = unlockable
        this.equipMechanics = equipMechanics
        this.dequipMechanics = dequipMechanics
    }

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
            if (attachmentDenyList.contains(attachment!!.attachmentTitle)) return false
        }

        // Cannot attach the same attachment multiple times
        return duplicateCount >= maximumStackAmount
    }

    fun attach(weapon: ItemStack?) {
        var array = CustomTag.ATTACHMENTS.getArray(weapon)
        if (array == null) array = IntArray(0)
        val attachments: MutableList<Attachment> = ArrayList(array.size + 1)
        for (id in array) {
            attachments.add(AttachmentRegistry.Companion.INSTANCE.get(id))
        }

        // Add the new attachment and sort
        attachments.add(this)
        attachments.sort()
        val newArray = IntArray(array.size + 1)
        for (i in attachments.indices) {
            newArray[i] = AttachmentRegistry.Companion.INSTANCE.getId(attachments[i])
        }
        CustomTag.ATTACHMENTS.setArray(weapon, newArray)
    }

    override fun compareTo(other: Attachment): Int {
        return priority.compareTo(other.priority)
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): Attachment {
        val attachmentTitle = data.key
        val maximumStackAmount = data.of("Maximum_Stack_Amount").assertRange(1, 200).getInt(1)
        val item = data.of("Item").assertExists().serialize(ItemSerializer())

        val isWeaponWhitelist = data.of("Denying.Weapon_Whitelist").getBool(false)
        val weapons = data.ofList("Denying.Weapons").addArgument(String::class.java, true).assertExists().assertList().get()
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
        val equipMechanics = data.of("Equip_Mechanics").serialize(Mechanics::class.java)
        val dequipMechanics = data.of("Dequip_Mechanics").serialize(Mechanics::class.java)

        val base = super.serialize(data)
        val returnValue = Attachment(attachmentTitle, maximumStackAmount, item, attachmentRequireList, attachmentDenyList, weaponWhitelist, unlockable, equipMechanics, dequipMechanics)

        returnValue.priority = base.priority
        returnValue.modifier = base.modifier
        returnValue.perWeaponModifiers = base.perWeaponModifiers
        return returnValue
    }

    private enum class State {
        DENY, REQUIRE
    }
}