package red.man10.questplugin.commands.subcommands.party

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.party.PartyManager

class QuestPartyKickCommand : CommandExecutor {

    private val prefix = "§a[§6§lQuestPartyPlugin§a]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("$prefix §cプレイヤーのみ実行できます。")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("$prefix §c追放するプレイヤー名を指定してください。")
            return true
        }

        val player = sender
        val targetName = args[0]
        val targetPlayer = Bukkit.getPlayerExact(targetName)

        if (targetPlayer == null) {
            player.sendMessage("$prefix §cプレイヤーが見つかりません。")
            return true
        }

        if (!PartyManager.isInParty(player)) {
            player.sendMessage("$prefix §cあなたはパーティーに所属していません。")
            return true
        }

        if (!PartyManager.isLeader(player)) {
            player.sendMessage("$prefix §cリーダーのみプレイヤーを追放できます。")
            return true
        }

        if (!PartyManager.isInParty(targetPlayer)) {
            player.sendMessage("$prefix §cそのプレイヤーはパーティーにいません。")
            return true
        }

        val members = PartyManager.getPartyMembers(player)

        if (!members.contains(targetPlayer)) {
            player.sendMessage("$prefix §c指定したプレイヤーはあなたのパーティーメンバーではありません。")
            return true
        }

        if (targetPlayer == player) {
            player.sendMessage("$prefix §c自分自身を追放できません。")
            return true
        }

        val success = PartyManager.kickMember(player, targetPlayer)
        if (!success) {
            player.sendMessage("$prefix §cプレイヤーの追放に失敗しました。")
            return true
        }

        player.sendMessage("$prefix §a${targetPlayer.name} をパーティーから追放しました。")
        targetPlayer.sendMessage("$prefix §cあなたはパーティーから追放されました。")

        return true
    }
}
