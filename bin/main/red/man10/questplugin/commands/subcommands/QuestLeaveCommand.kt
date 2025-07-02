package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.ActiveQuestManager

class QuestLeaveCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("§cこのコマンドはプレイヤーのみ実行可能です。"))
            return true
        }
        val player = sender

        if (!ActiveQuestManager.isQuesting(player)) {
            player.sendMessage(Component.text("§c現在進行中のクエストはありません。"))
            return true
        }

        ActiveQuestManager.cancelQuest(player)
        player.sendMessage(Component.text("§aクエストを中断しました。"))

        return true
    }
}
