package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.ActiveQuestManager
import red.man10.questplugin.QuestConfigManager
import red.man10.questplugin.prefix

class QuestStartCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("$prefix §cプレイヤーのみ実行可能です。"))
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage(Component.text("$prefix §cクエストIDを指定してください。"))
            return true
        }
        val id = args[1]
        val quest = QuestConfigManager.getQuest(id)
        if (quest == null) {
            sender.sendMessage(Component.text("$prefix §cクエスト[$id]は存在しません。"))
            return true
        }
        val success = ActiveQuestManager.startQuest(sender, quest)
        if (!success) {
            sender.sendMessage(Component.text("$prefix §c既にクエスト中です。"))
            return true
        }
        sender.sendMessage(Component.text("$prefix §aクエスト「${quest.name}」を開始しました！"))
        return true
    }
}
