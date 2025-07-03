package red.man10.questplugin.commands.subcommands.party

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.party.PartyManager

class QuestPartyJoinCommand : CommandExecutor {

    private val prefix = "§a[§6§lQuestPartyPlugin§a]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("$prefix §cプレイヤーのみ実行できます")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("$prefix §c参加するパーティーのリーダー名を指定してください。")
            return true
        }

        val player = sender
        val leaderName = args[0]
        val leaderPlayer = Bukkit.getPlayerExact(leaderName)

        if (leaderPlayer == null) {
            player.sendMessage("$prefix §c指定されたリーダーが見つかりません。")
            return true
        }

        // ① 招待されたか確認
        val invitedBy = PartyManager.invited[player.uniqueId]
        if (invitedBy != leaderPlayer.uniqueId) {
            player.sendMessage("$prefix §cあなたはこのプレイヤーに招待されていません。")
            return true
        }

        // ② すでに所属しているか確認
        if (PartyManager.isInParty(player)) {
            player.sendMessage("$prefix §c既にパーティーに所属しています。")
            return true
        }

        // ③ 参加処理
        val joined = PartyManager.joinParty(player, leaderPlayer)
        if (!joined) {
            player.sendMessage("$prefix §c参加に失敗しました。")
            return true
        }

        player.sendMessage("$prefix §aパーティーに参加しました。")
        leaderPlayer.sendMessage("$prefix §e${player.name} がパーティーに参加しました。")

        return true
    }
}
