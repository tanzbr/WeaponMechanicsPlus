/*
 * Copyright (c) 2026. All rights reserved. Distribution of this file, similar
 * files, related files, or related projects is strictly controlled.
 */

package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import com.cjcrafter.armormechanics.ArmorMechanics
import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import kotlin.jvm.optionals.getOrNull

abstract class ModifierBase : Serializer<ModifierBase>, Comparable<ModifierBase> {

    var priority = 0
    var weaponModifier: WeaponModifier? = null
    var perWeaponModifiers: Map<String, WeaponModifier> = mapOf()
    var armorModifier: ArmorModifier? = null
    var perArmorModifiers: Map<String, ArmorModifier> = mapOf()

    /**
     * Default constructor for serializer.
     */
    constructor()
    constructor(
        priority: Int,
        weaponModifier: WeaponModifier?,
        perWeaponModifiers: Map<String, WeaponModifier>,
        armorModifier: ArmorModifier?,
        perArmorModifiers: Map<String, ArmorModifier>
    ) {
        this.priority = priority
        this.weaponModifier = weaponModifier
        this.perWeaponModifiers = perWeaponModifiers
        this.armorModifier = armorModifier
        this.perArmorModifiers = perArmorModifiers
    }

    fun getWeaponModifier(weaponTitle: String) = perWeaponModifiers.getOrDefault(weaponTitle, weaponModifier)

    fun getArmorModifier(armorTitle: String) = perArmorModifiers.getOrDefault(armorTitle, armorModifier)

    override fun compareTo(other: ModifierBase) = priority.compareTo(other.priority)

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ModifierBase {
        val priority = data.of("Priority").getInt().orElse(0) // can be negative

        val weaponModifier = data.of("Modifiers").assertExists().serialize(WeaponModifier::class.java).get()
        val perWeaponModifiers = HashMap<String, WeaponModifier>()
        if (data.has("Per_Weapon_Modifiers")) {
            val config = data.of("Per_Weapon_Modifiers").assertExists().get(ConfigurationSection::class.java).get()

            for (key in config.getKeys(false)) {
                val temp = data.of("Per_Weapon_Modifiers.$key").assertExists().serialize(WeaponModifier::class.java).get()
                perWeaponModifiers[key] = temp
            }
        }

        val armorModifier: ArmorModifier? = data.of("Armor_Modifiers").serialize(ArmorModifier::class.java).getOrNull()
        val perArmorModifiers = HashMap<String, ArmorModifier>()

        // If they use ArmorModifiers, but ArmorMechanics is not installed...
        val hasArmorMechanics = Bukkit.getPluginManager().getPlugin("ArmorMechanics") != null
        if ((armorModifier != null || data.has("Per_Armor_Modifiers")) && !hasArmorMechanics) {
            throw data.exception(null, "To use Armor_Modifiers, you must have ArmorMechanics installed")
        }

        if (data.has("Per_Armor_Modifiers")) {
            val config = data.of("Per_Armor_Modifiers").assertExists().get(ConfigurationSection::class.java).get()
            for (key in config.getKeys(false)) {
                val temp = data.of("Per_Armor_Modifiers.$key").assertExists().serialize(ArmorModifier::class.java).get()
                perArmorModifiers[key] = temp
            }
        }

        return object : ModifierBase(priority, weaponModifier, perWeaponModifiers, armorModifier, perArmorModifiers) {}
    }
}