package red.man10.questplugin.commands.subcommands


import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent.suggestCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.QuestConfigManager
import red.man10.questplugin.prefix

class QuestListCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val quests = QuestConfigManager.getAllQuests()
        if (quests.isEmpty()) {
            sender.sendMessage(Component.text("$prefix §c§l登録されたクエストはありません。"))
            return true
        }
        sender.sendMessage(Component.text("$prefix §a§l====== クエスト一覧 ======"))
        for (quest in quests) {
            sender.sendMessage(Component.text("$prefix §e§l${quest.id} : ${quest.name}").clickEvent(suggestCommand("/quest info ${quest.id}")));
        }
        sender.sendMessage(Component.text("$prefix §a§l===================="))
        return true
    }
}
