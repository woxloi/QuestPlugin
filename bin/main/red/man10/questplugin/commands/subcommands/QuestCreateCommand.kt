package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.QuestConfigManager
import red.man10.questplugin.prefix

class QuestCreateCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // args[0] = "config", args[1] = "create", args[2] = "テスト"

        val id = args[2]

        if (QuestConfigManager.exists(id)) {
            sender.sendMessage(Component.text("$prefix §c§lそのIDのクエストは既に存在します。"))
            return true
        }

        QuestConfigManager.createQuest(id)
        sender.sendMessage(Component.text("$prefix §a§lクエスト[$id]を作成しました。"))
        return true
    }
}
