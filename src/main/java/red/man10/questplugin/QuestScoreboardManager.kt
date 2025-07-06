package red.man10.questplugin

import com.shojabon.mcutils.Utils.SScoreboard
import org.bukkit.entity.Player

class QuestScoreboard(private val player: Player, private val quest: QuestData) {

    private val scoreboard = SScoreboard("TEST")

    private var currentAmount: Int = 0
    private var remainingTimeSeconds: Long? = null

    fun show() {
        scoreboard.setTitle("§4§lQuestPlugin")
        update()
        scoreboard.addPlayer(player)
    }

    fun updateProgress(newAmount: Int) {
        currentAmount = newAmount.coerceAtMost(quest.amount)
        update()
    }

    fun updateRemainingTime(seconds: Long?) {
        remainingTimeSeconds = seconds
        update()
    }

    fun update() {
        var index = 0
        scoreboard.setText(index++, "§a§lクエスト名: ${quest.name}")
        scoreboard.setText(index++, "§c§lクエスト進行中")
        scoreboard.setText(index++, "§e§l目標: §f§l${quest.target} x${quest.amount}")
        scoreboard.setText(index++, "§a§l進行状況: §e$currentAmount / ${quest.amount}")
        // ライフ残り 合計
        if (quest.maxLives != null) {
            // パーティーメンバー全員(自分含む)
            val partyMembers = red.man10.questplugin.party.PartyManager.getPartyMembers(player).toMutableList()
            if (!partyMembers.contains(player)) partyMembers.add(player)

            val totalMaxLives = quest.maxLives!! * partyMembers.distinctBy { it.uniqueId }.size
            val totalDeaths = partyMembers.distinctBy { it.uniqueId }.sumOf { member ->
                ActiveQuestManager.getPlayerData(member.uniqueId)?.deathCount ?: 0
            }
            val remainingLives = (totalMaxLives - totalDeaths).coerceAtLeast(0)

            scoreboard.setText(index++, "§d§lライフ残り: §f$remainingLives")
        }
        // パーティーメンバーの表示
        val partyMembers = red.man10.questplugin.party.PartyManager.getPartyMembers(player)
        if (partyMembers.isNotEmpty()) {
            scoreboard.setText(index++, "§b§lパーティーメンバー:")
            for (member in partyMembers) {
                val currentHP = member.health.toInt()
                val maxHP = member.maxHealth.toInt()
                scoreboard.setText(index++, "§f${member.name} §d♥§7$currentHP")
            }
        }

        // 制限時間の表示（後ろに持ってくる）
        if (remainingTimeSeconds != null) {
            val min = remainingTimeSeconds!! / 60
            val sec = remainingTimeSeconds!! % 60
            scoreboard.setText(index++, "§c§l制限時間: §f%02d:%02d".format(min, sec))
        }

        scoreboard.renderText()
    }

    fun hide() {
        scoreboard.remove()
    }
}
