package red.man10.questplugin.commands.subcommands

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.QuestConfigManager

class QuestAddRewardCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // args[0] = "config", args[1] = "addreward", args[2] = "<クエストID>", args[3..] = "<コマンド>"
        if (args.size < 4) {
            sender.sendMessage(Component.text("§c使い方: /quest config addreward <クエストID> <コマンド>"))
            return true
        }
        val id = args[2]
        val cmd = args.drop(3).joinToString(" ")

        val quest = QuestConfigManager.getQuest(id)
        if (quest == null) {
            sender.sendMessage(Component.text("§cクエスト[$id]は存在しません。"))
            return true
        }

        quest.rewards.add(cmd)
        sender.sendMessage(Component.text("§aクエスト[$id]に報酬コマンドを追加しました。"))
        return true
    }
}
