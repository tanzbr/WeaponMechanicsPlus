package me.deecaad.weaponmechanicsplus.weapon.modifiers

import me.deecaad.core.file.*
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.IntegerModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.MechanicsModifier.Companion.serializeMechanicsModifier

class ReloadModifier : Serializer<ReloadModifier> {

    var magazineSize: IntegerModifier? = null
    var ammoPerReload: IntegerModifier? = null
    var reloadDuration: IntegerModifier? = null
    var shootDelayAfterReload: IntegerModifier? = null
    var mechanicsModifier: MechanicsModifier? = null

    /**
     * Default constructor for serializer
     */
    constructor()

    constructor(magazineSize: IntegerModifier?, ammoPerReload: IntegerModifier?, reloadDuration: IntegerModifier?,
                shootDelayAfterReload: IntegerModifier?, mechanicsModifier: MechanicsModifier?) {
        this.magazineSize = magazineSize
        this.ammoPerReload = ammoPerReload
        this.reloadDuration = reloadDuration
        this.shootDelayAfterReload = shootDelayAfterReload
        this.mechanicsModifier = mechanicsModifier
    }

    @Throws(SerializerException::class)
    override fun serialize(data: SerializeData): ReloadModifier {
        val magazineSize = data.of("Magazine_Size").serialize(IntegerModifier::class.java)
        val ammoPerReload = data.of("Ammo_Per_Reload").serialize(IntegerModifier::class.java)
        val reloadDuration = data.of("Reload_Duration").serialize(IntegerModifier::class.java)
        val shootDelayAfterReload = data.of("Shoot_Delay_After_Reload").serialize(IntegerModifier::class.java)
        val mechanicsModifier = data.serializeMechanicsModifier()

        return ReloadModifier(magazineSize, ammoPerReload, reloadDuration, shootDelayAfterReload, mechanicsModifier)
    }
}