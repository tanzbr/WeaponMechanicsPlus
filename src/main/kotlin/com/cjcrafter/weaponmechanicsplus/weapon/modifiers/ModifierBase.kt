package com.cjcrafter.weaponmechanicsplus.weapon.modifiers

import com.cjcrafter.armormechanics.ArmorMechanics
import me.deecaad.core.file.*
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

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
        val priority = data.of("Priority").getInt(0) // can be negative

        val weaponModifier = data.of("Modifiers").assertExists().serialize(WeaponModifier::class.java)
        val perWeaponModifiers = HashMap<String, WeaponModifier>()
        if (data.has("Per_Weapon_Modifiers")) {
            val config = data.of("Per_Weapon_Modifiers").assertExists().assertType(ConfigurationSection::class.java).get<ConfigurationSection>()

            val options = WeaponMechanics.getWeaponHandler().infoHandler.sortedWeaponList
            for (key in config.getKeys(false)) {
                //if (!options.contains(key))
                //    throw SerializerOptionsException(data.serializer, "Weapon", options, key, data.of(key).location)

                val temp = data.of("Per_Weapon_Modifiers.$key").assertExists().serialize(WeaponModifier::class.java)!!
                perWeaponModifiers[key] = temp
            }
        }

        val armorModifier: ArmorModifier? = data.of("Armor_Modifiers").serialize(ArmorModifier::class.java)
        val perArmorModifiers = HashMap<String, ArmorModifier>()

        // If they use ArmorModifiers, but ArmorMechanics is not installed...
        val hasArmorMechanics = Bukkit.getPluginManager().getPlugin("ArmorMechanics") != null
        if ((armorModifier != null || data.has("Per_Armor_Modifiers")) && !hasArmorMechanics) {
            throw data.exception(null, "To use Armor_Modifiers, you must have ArmorMechanics installed")
        }

        if (data.has("Per_Armor_Modifiers")) {
            val config = data.of("Per_Armor_Modifiers").assertExists().assertType(ConfigurationSection::class.java).get<ConfigurationSection>()

            val options = ArmorMechanics.INSTANCE.armors.keys
            for (key in config.getKeys(false)) {
                //if (!options.contains(key))
                //    throw SerializerOptionsException(data.serializer, "Armor", options, key, data.of(key).location)

                val temp = data.of("Per_Armor_Modifiers.$key").assertExists().serialize(ArmorModifier::class.java)!!
                perArmorModifiers[key] = temp
            }
        }

        return object : ModifierBase(priority, weaponModifier, perWeaponModifiers, armorModifier, perArmorModifiers) {}
    }
}