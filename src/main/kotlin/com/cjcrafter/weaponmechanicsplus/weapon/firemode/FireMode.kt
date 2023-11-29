package com.cjcrafter.weaponmechanicsplus.weapon.firemode

import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlus
import com.cjcrafter.weaponmechanicsplus.WeaponMechanicsPlusAPI
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import me.deecaad.core.compatibility.CompatibilityAPI
import me.deecaad.core.file.SerializeData
import me.deecaad.core.file.Serializer
import me.deecaad.core.file.SerializerException
import me.deecaad.core.mechanics.Mechanics
import me.deecaad.weaponmechanics.WeaponMechanics
import me.deecaad.weaponmechanics.utils.CustomTag
import me.deecaad.weaponmechanics.weapon.trigger.Trigger
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class FireMode : Serializer<FireMode> {

    /**
     * This solves a niche problem. Imagine you have a m4a1 with a grenade
     * launcher attachment. Of course, you don't want the grenade launcher mode
     * to be active WITHOUT that attachment. And when you do have the attachment,
     * you don't want the grenades and bullets to use the same ammo.
     */
    data class FireModeSelector(
        val weaponTitle: String,
        val requiresAttachments: List<String> = emptyList(),
        val separateAmmo: String = "universal",
    ) {
        fun canUse(weapon: ItemStack): Boolean {
            val attachments = CustomTag.ATTACHMENTS.getStringArray(weapon)
            if (attachments.isNullOrEmpty())
                return requiresAttachments.isEmpty()

            for (requiredAttachment in requiresAttachments) {
                if (!attachments.contains(requiredAttachment))
                    return false
            }

            return true
        }
    }

    lateinit var trigger: Trigger
    lateinit var modes: List<FireModeSelector> // a makeshift circularly linked list
    var switchMechanics: Mechanics? = null

    /**
     * Default constructor for serializer
     */
    constructor()
    constructor(trigger: Trigger, modes: List<FireModeSelector>, switchMechanics: Mechanics?) {
        this.trigger = trigger
        this.modes = modes
        this.switchMechanics = switchMechanics
    }

    /**
     * Attempts to switch firemodes on the given weaponstack.
     *
     * @return The new weapon title, or null
     */
    fun switch(weaponTitle: String, weaponStack: ItemStack): String? {
        val currentIndex = modes.indexOfFirst { it.weaponTitle == weaponTitle }
        if (currentIndex == -1) {
            WeaponMechanicsPlus.getDebug().error("Not sure how this happened...")
            return null
        }

        val next = getNextWeapon(currentIndex, weaponStack) ?: return null
        val current = modes[currentIndex]

        // If the next weapon's ammo is different, we need to switch the ammo.
        val nbt = CompatibilityAPI.getNBTCompatibility();
        if (next.separateAmmo != current.separateAmmo) {
            val currentAmmoLeft = CustomTag.AMMO_LEFT.getInteger(weaponStack)
            val currentAmmoTypeIndex = CustomTag.AMMO_TYPE_INDEX.getInteger(weaponStack)

            val nextAmmoLeft = nbt.getInt(weaponStack, "weaponmechanicsplus", "${next.separateAmmo}_ammo_left")
            val nextAmmoTypeIndex = nbt.getInt(weaponStack, "weaponmechanicsplus", "${next.separateAmmo}_ammo_type_index")
            CustomTag.AMMO_LEFT.setInteger(weaponStack, nextAmmoLeft)
            CustomTag.AMMO_TYPE_INDEX.setInteger(weaponStack, nextAmmoTypeIndex)

            nbt.setInt(weaponStack, "weaponmechanicsplus", "${current.separateAmmo}_ammo_left", currentAmmoLeft)
            nbt.setInt(weaponStack, "weaponmechanicsplus", "${current.separateAmmo}_ammo_type_index", currentAmmoTypeIndex)
        }

        CustomTag.WEAPON_TITLE.setString(weaponStack, next.weaponTitle)
        return next.weaponTitle
    }

    fun getNextWeapon(currentIndex: Int, weaponStack: ItemStack): FireModeSelector? {
        // First, we need to find where we currently are in the list.
        // Then we can loop through until we find a valid next firemode.
        // If we loop all the way back to where we started, next weapon is null.
        for (i in 1 until modes.size) {
            val mode = modes[(currentIndex + i) % modes.size]
            if (mode.canUse(weaponStack))
                return mode
        }

        return null
    }

    override fun getKeyword() = "Fire_Mode"

    override fun getWikiLink() = "https://cjcrafter.gitbook.io/weaponmechanicsplus/firemode"

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): FireMode {
        val config = WeaponMechanics.getConfigurations()

        // This config option was removed in 1.2.1
        if (data.has("Next_Weapon")) {
            throw data.exception("The 'Next_Weapon' option was removed in 1.2.1",
                "Please use the 'Order' option instead")
        }

        val trigger = data.of("Trigger").assertExists().serialize(Trigger::class.java)!!
        val switchMechanics = data.of("Mechanics").serialize(Mechanics::class.java)
        val orderInput = data.ofList("Order").assertExists()
            .addArgument(String::class.java, true)  // weapon
            .addArgument(String::class.java, false) // separate ammo id
            .addArgument(String::class.java, false) // attachment
            .assertList().get()

        // Make sure the user has put in at least 1 firemode. We allow 1 firemode for WIP weapons
        if (orderInput.isEmpty()) {
            throw data.exception("Order", "The 'Order' option should NOT be an empty list! You should have at least 2 firemodes.")
        }

        val order = mutableListOf<FireModeSelector>()
        for ((index, split) in orderInput.withIndex()) {
            val weapon = split[0]
            val ammo = if (split.size > 1) split[1].lowercase() else "universal"
            val attachments = if (split.size > 2) listOf(split[2]) else emptyList()

            // Only 1 weapon can have a firemode
            if (config.containsKey("$weapon.Fire_Mode")) {
                throw data.listException("Order", index,
                    "The weapon '$weapon' already had a 'Fire_Mode' option defined.",
                    "When using firemodes, only 1 of the weapons can have a 'Fire_Mode' defined",
                    "Please delete all other 'Fire_Mode' config sections except 1"
                )
            }

            // Run some checks to make sure the weapon and attachment actually
            // exist. We have to do this 1 tick later due to serialization
            Bukkit.getScheduler().runTask(WeaponMechanicsPlus.getPlugin(), Runnable {
                if (!WeaponMechanics.getWeaponHandler().infoHandler.hasWeapon(weapon)) {
                    data.listException("Order", index,
                        "Could not find any weapon named '$weapon'",
                        SerializerException.didYouMean(weapon, WeaponMechanics.getWeaponHandler().infoHandler.sortedWeaponList)
                    ).log(WeaponMechanicsPlus.getDebug())
                }
                for (attachment in attachments) {
                    if (AttachmentRegistry.INSTANCE[attachment] == null) {
                        data.listException("Order", index,
                            "Could not find any attachment named '$attachment'",
                            SerializerException.didYouMean(attachment, AttachmentRegistry.INSTANCE.map { it.attachmentTitle })
                        ).log(WeaponMechanicsPlus.getDebug())
                    }
                }
            })

            order.add(FireModeSelector(weapon, attachments, ammo))
        }

        // Make sure that the CURRENT WEAPON is first in the order list, and it
        // uses 'universal' ammo and no attachments.
        val currentWeaponTitle = data.key.split(".").first()
        if (order.first().weaponTitle != currentWeaponTitle) {
            throw data.exception("We expected '$currentWeaponTitle' to be the FIRST weapon in the list",
                "Instead, '${order.first().weaponTitle}' was the first element",
                "Make sure the first weapon in the list is '- $currentWeaponTitle universal' or just '- $currentWeaponTitle'")
        }
        if (order.first().separateAmmo != "universal") {
            throw data.exception("The first firemode should always use 'universal' ammo",
                "Instead, '${order.first().separateAmmo}' was used for the first firemode",
                "You should leave the 'ammo' option blank, or use 'universal' as the ammo for the first firemode")
        }
        if (order.first().requiresAttachments.isNotEmpty()) {
            throw data.exception("The first firemode should not require any attachments",
                "Instead, '${order.first().requiresAttachments.first()}' was used for the first firemode",
                "You should leave the 'attachment' option blank for the first firemode")
        }

        // After creating the firemode, we have to go through each weapon
        // (other than the first) and set their firemode config
        val firemode = FireMode(trigger, order, switchMechanics)
        for (i in 1 until order.size) {
            val weapon = order[i].weaponTitle
            config.set("$weapon.Fire_Mode", firemode)
        }

        return firemode
    }
}