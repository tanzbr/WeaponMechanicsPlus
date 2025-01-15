@file:Suppress("UNCHECKED_CAST")

package com.cjcrafter.weaponmechanicsplus

import me.deecaad.core.mechanics.CastData
import me.deecaad.core.utils.StringUtil
import me.deecaad.weaponmechanics.utils.CustomTag
import com.cjcrafter.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyEntities
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object Command {

    private var ATTACHMENT_SUGGESTIONS =
        ArgumentSuggestions.strings { _: SuggestionInfo<CommandSender> ->
            AttachmentRegistry.INSTANCE.asSequence().map { it.attachmentTitle }.toList().toTypedArray()
        }


    fun register() {
        commandAPICommand("wmp") {
            withAliases("weaponmechanicsplus")
            withPermission("weaponmechanicsplus.admin")
            withShortDescription("WeaponMechanicsPlus main command")

            subcommand("give") {
                withPermission("weaponmechanicsplus.commands.give")
                withShortDescription("Give attachments to players")

                entitySelectorArgumentManyEntities("target")
                stringArgument("attachment") {
                    replaceSuggestions(ATTACHMENT_SUGGESTIONS)
                }
                integerArgument("amount", 1, 99, optional = true)

                anyExecutor { sender, args ->
                    val receivers = args[0] as List<Entity>
                    val attachmentStr = args[1] as String
                    val amount = args[2] as Int? ?: 1

                    give(sender, receivers, attachmentStr, amount)
                }
            }

            subcommand("get") {
                withPermission("weaponmechanicsplus.commands.get")
                withShortDescription("Give attachments to yourself")

                stringArgument("attachment") {
                    replaceSuggestions(ATTACHMENT_SUGGESTIONS)
                }
                integerArgument("amount", 1, 99, optional = true)

                playerExecutor { sender, args ->
                    val attachmentStr = args[0] as String
                    val amount = args[1] as Int? ?: 1

                    give(sender, listOf(sender), attachmentStr, amount)
                }
            }

            subcommand("detach") {
                withPermission("weaponmechanicsplus.commands.detach")
                withShortDescription("Detach attachments from the weapon")

                entitySelectorArgumentManyEntities("target", optional = true)
                stringArgument("attachment", optional = true)

                anyExecutor { sender, args ->
                    val targets = (args[0] ?: listOf(sender)) as List<LivingEntity>
                    val removeAttachment = args[1] as String?

                    detach(sender, targets, removeAttachment)
                }
            }

            subcommand("detach") {
                withPermission("weaponmechanicsplus.commands.detach")
                withShortDescription("Detach attachments from the weapon")

                stringArgument("attachment", optional = true)

                playerExecutor { sender, args ->
                    val removeAttachment = args[0] as String?

                    detach(sender, listOf(sender), removeAttachment)
                }
            }
        }
    }

    fun give(sender: CommandSender, receivers: List<Entity>, attachmentStr: String, amount: Int) {

        // Get the attachment and make sure it exists
        val attachment = AttachmentRegistry.INSTANCE[attachmentStr]
        if (attachment == null) {
            val didYouMean = StringUtil.didYouMean(attachmentStr, AttachmentRegistry.INSTANCE.asSequence().map { it.attachmentTitle }.asIterable())
            sender.sendMessage("${ChatColor.RED}Unknown attachment '$attachmentStr'. Did you mean '$didYouMean'")
            return
        }

        // Give it to all targeted players
        var count = 0
        for (entity in receivers) {
            if (entity !is Player)
                continue

            val item = attachment.item.clone()
            item.amount = amount
            entity.inventory.addItem(item)
            count++
        }

        sender.sendMessage("Gave $count players $amount $attachmentStr(s)")
    }

    fun detach(sender: CommandSender, targets: List<LivingEntity>, removeAttachment: String?) {
        var removedFrom = 0

        for (target in targets) {
            if (target !is Player)
                continue

            val weapon = target.equipment?.itemInMainHand ?: target.equipment?.itemInOffHand ?: continue
            val attachments = CustomTag.ATTACHMENTS.getStringArray(weapon).toMutableList()

            // Remove all attachments that match, and generate an item
            val removed = attachments.removeIf { attachment ->
                if (removeAttachment == null || removeAttachment == attachment) {

                    // This may be null if the attachment no longer exists in config
                    val removedAttachment = AttachmentRegistry.INSTANCE[attachment]
                    if (removedAttachment != null) {
                        removedAttachment.dequipMechanics?.use(CastData(target, CustomTag.WEAPON_TITLE.getString(weapon), weapon))
                        val overflow = target.inventory.addItem(removedAttachment.generateItem())
                        overflow.values.forEach { item -> target.world.dropItem(target.eyeLocation, item) }
                    }

                    return@removeIf true
                }

                return@removeIf false
            }


            // Remove the NBT of the removed weapons
            CustomTag.ATTACHMENTS.setStringArray(weapon, attachments.toTypedArray())

            // Increment the counter so the command executor knows who they effected
            if (removed)
                removedFrom++
        }

        sender.sendMessage("${ChatColor.GREEN}Detached attachments from $removedFrom players")
    }
}