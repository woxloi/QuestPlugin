package red.man10.questplugin.commands.subcommands.party

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import red.man10.questplugin.party.PartyManager

class QuestPartyCreateCommand : CommandExecutor {

    private val prefix = "§a[§6§lQuestPartyPlugin§a]"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("$prefix §cプレイヤーのみ実行できます")
            return true
        }

        val player = sender

        if (PartyManager.isInParty(player)) {
            player.sendMessage("$prefix §c既にパーティーに所属しています")
            return true
        }

        PartyManager.createParty(player)
        player.sendMessage("$prefix §aパーティーを作成しました！")
        return true
    }
}
