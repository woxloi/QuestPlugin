package red.man10.questplugin.commands.subcommands.party

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.party.PartyManager

class QuestPartyDisbandCommand : CommandExecutor {

    private val prefix = "§a[§6§lQuestPartyPlugin§a]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(Component.text("$prefix §cこのコマンドはプレイヤーのみ実行可能です。"))
            return true
        }

        val player = sender
        val leader = PartyManager.getLeader(player)

        if (leader == null || leader.uniqueId != player.uniqueId) {
            player.sendMessage(Component.text("$prefix §cあなたはパーティーのリーダーではありません。"))
            return true
        }

        PartyManager.disbandParty(leader.uniqueId)
        player.sendMessage(Component.text("$prefix §aパーティーを解散しました。"))

        return true
    }
}
