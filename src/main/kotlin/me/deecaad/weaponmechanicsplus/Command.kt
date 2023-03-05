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
import org.bukkit.entity.Player
import java.util.function.Function


object Command {

    private var ATTACHMENT_SUGGESTIONS =
        Function { _ignore: CommandData ->
            return@Function AttachmentRegistry.INSTANCE.asSequence()
                .map { it.attachmentTitle }
                .map { Tooltip.of(it) }
                .toList().toTypedArray()
        }


    fun register() {
        val cmd = CommandBuilder("wmp")
            .withAliases("weaponmechanicsplus")
            .withPermission("weaponmechanicsplus.admin")
            .withDescription("WeaponMechanicsPlus main command")

            .withSubcommand(CommandBuilder("give")
                .withPermission("weaponmechanicsplus.commands.give")
                .withDescription("Give attachments to players")
                .withArgument(Argument("target", EntityListArgumentType()).withDesc("Who to give the attachment to"))
                .withArgument(Argument("attachment", StringArgumentType()).withDesc("Which attachment to give").append(ATTACHMENT_SUGGESTIONS))
                .withArgument(Argument("amount", IntegerArgumentType(1, 64), 1).withDesc("How many of the attachment to give").append(IntegerArgumentType.ITEM_COUNT))
                .executes(CommandExecutor.any { sender: CommandSender, args: Array<Any> ->
                    give(sender, args[0] as List<Entity>, args[1] as String, args[2] as Int)
                }))

            .withSubcommand(CommandBuilder("get")
                .withPermission("weaponmechanicsplus.commands.get")
                .withDescription("Give attachments to yourself")
                .withArgument(Argument("attachment", StringArgumentType()).withDesc("Which attachment to give").append(ATTACHMENT_SUGGESTIONS))
                .withArgument(Argument("amount", IntegerArgumentType(1, 64), 1).withDesc("How many of the attachment to give").append(IntegerArgumentType.ITEM_COUNT))
                .executes(CommandExecutor.player { sender: Player, args: Array<Any> ->
                    give(sender, listOf(sender), args[0] as String, args[1] as Int)
                })
            )

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
}