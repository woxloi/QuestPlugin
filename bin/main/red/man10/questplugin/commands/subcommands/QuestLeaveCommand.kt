package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.ActiveQuestManager
import red.man10.questplugin.QuestPlugin
import red.man10.questplugin.prefix

class QuestLeaveCommand(plugin: QuestPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("$prefix §c§lこのコマンドはプレイヤーのみ実行可能です。"))
            return true
        }
        val player = sender

        if (!ActiveQuestManager.isQuesting(player)) {
            player.sendMessage(Component.text("$prefix §c§l現在進行中のクエストはありません。"))
            return true
        }

        ActiveQuestManager.cancelQuest(player)
        player.sendMessage(Component.text("$prefix §a§lクエストを中断しました。"))

        return true
    }
}
