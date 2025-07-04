package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.ActiveQuestManager
import red.man10.questplugin.QuestConfigManager
import red.man10.questplugin.prefix

class QuestReloadCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        QuestConfigManager.loadAllQuests()
        for (player in Bukkit.getOnlinePlayers()) {
            ActiveQuestManager.cancelQuest(player)
            player.sendMessage("§c§lリロードによってクエストがキャンセルされました")
        }
        plugin.reloadConfig()
        sender.sendMessage("$prefix §a§lクエスト設定を再読み込みしました")
        sender.sendMessage("$prefix §a§lすべてのプレイヤーのクエストが停止されました")
        return true
    }
}
