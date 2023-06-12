@file:Suppress("UNCHECKED_CAST")

package me.deecaad.weaponmechanicsplus

import me.deecaad.core.commands.*
import me.deecaad.core.commands.arguments.EntityListArgumentType
import me.deecaad.core.commands.arguments.IntegerArgumentType
import me.deecaad.core.commands.arguments.StringArgumentType
import me.deecaad.core.utils.StringUtil
import me.deecaad.weaponmechanicsplus.weapon.modifiers.attachments.AttachmentRegistry
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.function.Function


object Command {

    private var ATTACHMENT_SUGGESTIONS =
        Function { _: CommandData ->
            return@Function AttachmentRegistry.INSTANCE.asSequence()
                .map { it.attachmentTitle }
                .map { Tooltip.of(it) }
                .toList().toTypedArray()
        }


    fun register() {
        val cmd = command("wmp") {
            aliases("weaponmechanicsplus")
            permission("weaponmechanicsplus.admin")
            description("WeaponMechanicsPlus main command")

            subcommand("give") {
                permission("weaponmechanicsplus.commands.give")
                description("Give attachments to players")
                argument("target", EntityListArgumentType()) {
                    description = "Who to give the attachments to"
                }
                argument("attachment", StringArgumentType()) {
                    description = "Which attachment to give"
                    append(ATTACHMENT_SUGGESTIONS)
                }
                argument("amount", IntegerArgumentType(1, 64)) {
                    description = "How many of the attachment to give"
                    append(IntegerArgumentType.ITEM_COUNT)
                }
                executeAny { sender: CommandSender, args: Array<Any?> ->
                    give(sender, args[0] as List<Entity>, args[1] as String, args[2] as Int)
                }
            }

            subcommand("get") {
                permission("weaponmechanicsplus.commands.get")
                description("Give attachments to yourself")

                argument("attachment", StringArgumentType()) {
                    description = "Which attachment to give"
                    append(ATTACHMENT_SUGGESTIONS)
                }
                argument("amount", IntegerArgumentType(1, 64)) {
                    description = "How many of the attachment to give"
                    append(IntegerArgumentType.ITEM_COUNT)
                }
                executePlayer { sender: Player, args: Array<Any?> ->
                    give(sender, listOf(sender), args[0] as String, args[1] as Int)
                }
            }

            subcommand("detach") {
                permission("weaponmechanicsplus.commands.detach")
                description("Detach attachments from the weapon")

                argument("target", EntityListArgumentType()) {
                    description = ""
                    default = null
                }

                argument("attachment", StringArgumentType().withLiteral("*")) {
                    description
                    default = "*"
                }

                executeAny { sender: CommandSender, args: Array<Any?> ->
                    detach(sender, (args[0] ?: sender) as LivingEntity, args[1] as String)
                }
            }
        }

        cmd.registerHelp(HelpCommandBuilder.HelpColor.from(ChatColor.GOLD, ChatColor.GRAY, '\u27A2'))
        cmd.register()
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

    fun detach(sender: CommandSender, target: LivingEntity, attachment: String?) {

    }
}