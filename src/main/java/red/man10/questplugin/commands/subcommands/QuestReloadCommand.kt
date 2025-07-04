package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.QuestConfigManager
import red.man10.questplugin.prefix

class QuestReloadCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        QuestConfigManager.loadAllQuests()
        plugin.reloadConfig()
        sender.sendMessage(Component.text("$prefix §a§lクエスト設定を再読み込みしました。"))
        return true
    }
}
