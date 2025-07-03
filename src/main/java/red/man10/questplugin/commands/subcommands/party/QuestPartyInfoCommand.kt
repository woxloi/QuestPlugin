package red.man10.questplugin.commands.subcommands.party

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.party.PartyManager

class QuestPartyInfoCommand : CommandExecutor {

    private val prefix = "§a[§6§lQuestPartyPlugin§a]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("$prefix §cプレイヤーのみ実行できます")
            return true
        }

        val player = sender

        if (!PartyManager.isInParty(player)) {
            player.sendMessage("$prefix §cあなたはパーティーに所属していません。")
            return true
        }

        val leader = PartyManager.getLeader(player)
        val members = PartyManager.getPartyMembers(player)

        val leaderName = leader?.name ?: "不明"
        val memberNames = members.joinToString(", ") { it.name }

        player.sendMessage("$prefix §eパーティー情報")
        player.sendMessage("§bリーダー: §f$leaderName")
        player.sendMessage("§bメンバー: §f$memberNames")

        return true
    }
}
