package red.man10.questplugin.commands.subcommands.party

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.party.PartyManager

class QuestPartyInviteCommand : CommandExecutor {

    private val prefix = "§a[§6§lQuestPartyPlugin§a]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("$prefix §cプレイヤーのみ実行できます")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("$prefix §c招待するプレイヤー名を指定してください。")
            return true
        }

        val player = sender
        val targetName = args[0]
        val targetPlayer = Bukkit.getPlayerExact(targetName)

        if (targetPlayer == null) {
            player.sendMessage("$prefix §cプレイヤーが見つかりません。")
            return true
        }

        if (!PartyManager.isLeader(player)) {
            player.sendMessage("$prefix §cあなたはパーティーのリーダーではありません。")
            return true
        }

        if (PartyManager.isInParty(targetPlayer)) {
            player.sendMessage("$prefix §c対象のプレイヤーはすでにパーティーに所属しています。")
            return true
        }

        val success = PartyManager.invitePlayer(player, targetPlayer)
        if (success) {
            player.sendMessage("$prefix §a${targetPlayer.name} をパーティーに招待しました。")
            targetPlayer.sendMessage("$prefix §e${player.name} からパーティー招待が届いています。 /quest party join ${player.name} で参加可能です。")
        } else {
            player.sendMessage("$prefix §cすでに招待済み、または招待に失敗しました。")
        }

        return true
    }
}
