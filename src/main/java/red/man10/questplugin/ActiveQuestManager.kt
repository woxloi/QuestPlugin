package red.man10.questplugin

import red.man10.questplugin.utils.STimer
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

object ActiveQuestManager {

    private val activeQuests = mutableMapOf<UUID, PlayerQuestData>()

    data class PlayerQuestData(
        val quest: QuestData,
        val startTime: Long,
        var progress: Int = 0,
        val bossBar: BossBar,
        val timer: STimer
    )

    // 追加：利用履歴管理
    object PlayerQuestUsageManager {
        private val usageMap = mutableMapOf<UUID, MutableMap<String, UsageData>>()

        data class UsageData(
            var lastUsedTime: Long = 0,
            var usedCount: Int = 0
        )

        fun canUseQuest(playerUUID: UUID, quest: QuestData): Boolean {
            val usage = usageMap[playerUUID]?.get(quest.id) ?: return true

            val now = System.currentTimeMillis()

            quest.cooldownSeconds?.let {
                val diff = (now - usage.lastUsedTime) / 1000
                if (diff < it) return false
            }

            quest.maxUseCount?.let {
                if (usage.usedCount >= it) return false
            }
            return true
        }

        fun recordQuestUse(playerUUID: UUID, quest: QuestData) {
            val playerUsage = usageMap.getOrPut(playerUUID) { mutableMapOf() }
            val usage = playerUsage.getOrPut(quest.id) { UsageData() }
            usage.lastUsedTime = System.currentTimeMillis()
            usage.usedCount++
        }
    }

    fun init() {
        // 必要なら起動時の初期化処理をここに（例：ファイルから利用履歴をロード）
    }

    fun shutdown() {
        activeQuests.values.forEach { data ->
            data.timer.stop()
        }
        activeQuests.clear()
        // 必要なら利用履歴の保存処理も追加
    }

    fun startQuest(player: Player, quest: QuestData): Boolean {
        val uuid = player.uniqueId
        if (activeQuests.containsKey(uuid)) return false // 既にクエスト中

        // 追加: クールダウン・回数制限チェック
        if (!PlayerQuestUsageManager.canUseQuest(uuid, quest)) {
            player.sendMessage("§cこのクエストはまだ利用できません。クールダウン中か利用回数の上限に達しています。")
            return false
        }

        val bossBar = createBossBar(quest)
        bossBar.addPlayer(player)

        val timer = STimer()
        if (quest.timeLimitSeconds != null) {
            val seconds = quest.timeLimitSeconds!!.toInt()
            timer.setRemainingTime(seconds)
            timer.linkBossBar(bossBar, true)
            timer.addOnEndEvent {
                Bukkit.getScheduler().runTask(QuestPlugin.plugin as Plugin, Runnable {
                    cancelQuest(player)
                    player.sendMessage("$prefix §c制限時間内にクエストをクリアすることができませんでした")
                })
            }
            timer.start()
        }

        val startTime = System.currentTimeMillis()
        activeQuests[uuid] = PlayerQuestData(quest, startTime, 0, bossBar, timer)

        // 追加：利用記録を残す
        PlayerQuestUsageManager.recordQuestUse(uuid, quest)

        updateBossBar(player)

        return true
    }

    fun cancelQuest(player: Player) {
        val uuid = player.uniqueId
        val data = activeQuests.remove(uuid) ?: return
        data.bossBar.removePlayer(player)
        data.timer.stop()
        player.sendMessage("$prefix §cクエスト[${data.quest.name}]をキャンセルしました。")
    }

    fun completeQuest(player: Player) {
        val uuid = player.uniqueId
        val data = activeQuests.remove(uuid) ?: return
        data.bossBar.removePlayer(player)
        data.timer.stop()
        player.sendMessage("$prefix §aクエスト[${data.quest.name}]をクリアしました！")

        for (cmd in data.quest.rewards) {
            val command = cmd.replace("%player%", player.name)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
        }
    }

    fun addProgress(player: Player, amount: Int = 1) {
        val uuid = player.uniqueId
        val data = activeQuests[uuid] ?: return
        data.progress += amount
        if (data.progress >= data.quest.amount) {
            completeQuest(player)
        } else {
            updateBossBar(player)
        }
    }

    private fun getActionVerb(type: QuestType): String {
        return when(type) {
            QuestType.KILL -> "倒す"
            QuestType.COLLECT -> "集める"
            QuestType.TRAVEL -> "訪れる"
            QuestType.MINE -> "掘る"
            QuestType.PLACE -> "設置する"
            QuestType.BREAK -> "壊す"
            else -> "達成する"
        }
    }

    private fun createBossBar(quest: QuestData): BossBar {
        val action = getActionVerb(quest.type)
        val title = "§e${quest.name} §7- ${quest.type.displayName} §f- §b${quest.target} を ${quest.amount} 個 $action"
        return Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SOLID)
    }

    private fun updateBossBar(player: Player) {
        val uuid = player.uniqueId
        val data = activeQuests[uuid] ?: return

        val action = getActionVerb(data.quest.type)
        val progressPercent = data.progress.toDouble() / data.quest.amount
        val progress = progressPercent.coerceIn(0.0, 1.0).toFloat()

        data.bossBar.progress = progress.toDouble()

        val barName = "§e${data.quest.name} §7- ${data.quest.type.displayName}\n" +
                "§b${data.quest.target} を ${data.progress} / ${data.quest.amount} $action"
        data.bossBar.setTitle(barName)
    }

    fun isQuesting(player: Player): Boolean {
        return activeQuests.containsKey(player.uniqueId)
    }
    fun getQuest(player: Player): QuestData? {
        return activeQuests[player.uniqueId]?.quest
    }
}
