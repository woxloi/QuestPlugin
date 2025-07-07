package red.man10.questplugin.listeners

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.ActiveQuestManager
import red.man10.questplugin.party.PartyManager

class QuestDeathListener(private val plugin: JavaPlugin) : Listener {
    private val prefix = "§a[§6§lQuestPlugin§a]"

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val uuid = player.uniqueId

        val data = ActiveQuestManager.getPlayerData(uuid) ?: return
        val quest = data.quest

        val maxLives = quest.maxLives ?: return
        data.deathCount++

        val remainingLives = maxLives - data.deathCount

        if (remainingLives > 0) {
            player.sendMessage("$prefix §c§l残りライフ: §f$remainingLives")
        } else {
            player.sendMessage("$prefix §c§lあなたは死んだ")

            Bukkit.getScheduler().runTask(plugin, Runnable {
                player.gameMode = GameMode.SPECTATOR
            })

            if (quest.partyEnabled) {
                val partyMembers = PartyManager.getPartyMembers(player).filter { it.uniqueId != uuid }
                partyMembers.forEach {
                    it.sendMessage("$prefix §e${player.name}さんがライフ切れで脱落しました。")
                }
            }
        }

        // スコアボード更新
        data.questScoreboard.update()

        // パーティー全員死亡チェック
        if (quest.partyEnabled) {
            val partyMembers = PartyManager.getPartyMembers(player)

            val allDead = partyMembers.all { member ->
                val memberData = ActiveQuestManager.getPlayerData(member.uniqueId)
                memberData != null && (quest.maxLives ?: 0) <= memberData.deathCount
            }

            if (allDead) {
                partyMembers.forEach {
                    ActiveQuestManager.cancelQuest(it)
                    it.sendMessage("$prefix §c§lパーティーメンバー全員が死亡したため、クエストを終了します。")
                }
            }
        } else {
            // ソロプレイヤーが死亡しきった場合
            if ((quest.maxLives ?: 0) <= data.deathCount) {
                ActiveQuestManager.cancelQuest(player)
                player.sendMessage("$prefix §c§lライフ切れのためクエストを終了しました。")
            }
        }

    }
}
