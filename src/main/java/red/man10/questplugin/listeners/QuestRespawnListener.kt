package red.man10.questplugin.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import red.man10.questplugin.ActiveQuestManager
import red.man10.questplugin.QuestPlugin.Companion.plugin
import red.man10.questplugin.party.PartyManager

class QuestRespawnListener : Listener {
    private val prefix = "§a[§6§lQuestPlugin§a]"

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val data = ActiveQuestManager.getPlayerData(player.uniqueId) ?: return
        val quest = data.quest

        val destination = if (quest.partyEnabled) {
            val aliveMembers = PartyManager.getPartyMembers(player).filter { member ->
                val memberData = ActiveQuestManager.getPlayerData(member.uniqueId)
                memberData != null && (quest.maxLives ?: 0) > memberData.deathCount && member.uniqueId != player.uniqueId
            }

            if (aliveMembers.isNotEmpty()) {
                val target = aliveMembers.random()
                val loc = target.location.clone().add(1.0, 0.0, 1.0)
                loc.y = loc.world.getHighestBlockYAt(loc).toDouble()
                player.sendMessage("$prefix §eパーティーメンバーの近くにリスポーンしました。")
                loc
            } else {
                player.sendMessage("$prefix §e元いた場所にリスポーンしました。")
                data.originalLocation.clone()
            }
        } else {
            player.sendMessage("$prefix §e元いた場所にリスポーンしました。")
            data.originalLocation.clone()
        }


        event.respawnLocation = destination
    }
}
