package red.man10.questplugin.commands.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.QuestConfigManager

class QuestInfoCommand(private val plugin: JavaPlugin) : CommandExecutor {

    private val prefix = "§a[§6§lQuestPlugin§a]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val questId = args[1]
        val quest = QuestConfigManager.getQuest(questId)

        if (quest == null) {
            sender.sendMessage("$prefix §c§lクエスト「$questId」が見つかりません。")
            return true
        }

        sender.sendMessage("§7§l=== クエスト情報 [$questId] ===")
        sender.sendMessage("§e§l名前: §f§l${quest.name}")
        sender.sendMessage("§e§lタイプ: §f§l${quest.type}")
        sender.sendMessage("§e§l目標: §f§l${quest.target} x${quest.amount}")
        sender.sendMessage("§e§l報酬:")
        if (quest.rewards.isEmpty()) {
            sender.sendMessage("  §7§l(報酬なし)")
        } else {
            quest.rewards.forEach { reward ->
                sender.sendMessage("  §f§l- $reward")
            }
        }

        quest.timeLimitSeconds?.let {
            sender.sendMessage("§e§l時間制限: §f§l${it}秒")
        }
        quest.cooldownSeconds?.let {
            sender.sendMessage("§e§lクールダウン: §f§l${it}秒")
        }
        quest.maxUseCount?.let {
            sender.sendMessage("§e§l最大回数: §f§l${it}回")
        }

        if (quest.partyEnabled) {
            sender.sendMessage("§e§lパーティー対応: §a有効")
            sender.sendMessage("  §7§l進捗共有: ${if (quest.shareProgress) "§a§lはい" else "§c§lいいえ"}")
            sender.sendMessage("  §7§l達成共有: ${if (quest.shareCompletion) "§a§lはい" else "§c§lいいえ"}")
            sender.sendMessage("  §7§l人数上限: ${quest.partyMaxMembers ?: "制限なし"}")
        } else {
            sender.sendMessage("§e§lパーティー対応: §c§l無効")
        }

        if (quest.teleportWorld != null) {
            sender.sendMessage("§e§lテレポート先: §f§l${quest.teleportWorld} (${quest.teleportX}, ${quest.teleportY}, ${quest.teleportZ})")
        }

        sender.sendMessage("§7§l==============================")
        return true
    }
}
