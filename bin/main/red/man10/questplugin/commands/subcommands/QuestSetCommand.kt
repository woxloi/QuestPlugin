package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.QuestConfigManager
import red.man10.questplugin.prefix

class QuestSetCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // args[0] = "config", args[1] = "set", args[2] = "<クエストID>", args[3] = "<key>", args[4] = "<value>"
        val id = args[2]
        val key = args[3].lowercase()
        val value = args[4]

        val quest = QuestConfigManager.getQuest(id)
        if (quest == null) {
            sender.sendMessage(Component.text("$prefix §c§lクエスト[$id]は存在しません。"))
            return true
        }

        when (key) {
            "name" -> quest.name = value
            "type" -> {
                val type = red.man10.questplugin.QuestType.fromString(value)
                if (type == null) {
                    sender.sendMessage(Component.text("$prefix §c§l不正なタイプです。"))
                    return true
                }
                quest.type = type
            }
            "target" -> quest.target = value
            "amount" -> {
                val num = value.toIntOrNull()
                if (num == null || num <= 0) {
                    sender.sendMessage(Component.text("$prefix §c§lamountは正の整数でなければなりません。"))
                    return true
                }
                quest.amount = num
            }
            "timelimit" -> {
                val num = value.toLongOrNull()
                if (num == null || num < 0) {
                    sender.sendMessage(Component.text("$prefix §c§ltimelimitは0以上の数字でなければなりません。"))
                    return true
                }
                quest.timeLimitSeconds = if (num == 0L) null else num
            }
            else -> {
                sender.sendMessage(Component.text("$prefix §c§l設定できないキーです。"))
                return true
            }
        }

        sender.sendMessage(Component.text("$prefix §a§lクエスト[$id]の$key を $value に設定しました。"))
        return true
    }
}
