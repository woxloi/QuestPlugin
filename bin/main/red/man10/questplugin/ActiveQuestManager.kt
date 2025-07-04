package red.man10.questplugin

import red.man10.questplugin.utils.STimer
import org.bukkit.Bukkit
import org.bukkit.Location
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

    object PlayerQuestUsageManager {
        private val usageMap = mutableMapOf<UUID, MutableMap<String, UsageData>>()

        data class UsageData(
            var lastUsedTime: Long = 0,
            var usedCount: Int = 0
        )

        fun canUseQuest(playerUUID: UUID, quest: QuestData): Boolean {
            val now = System.currentTimeMillis()
            val playerUsage = usageMap.getOrPut(playerUUID) { mutableMapOf() }
            val usage = playerUsage.getOrPut(quest.id) { UsageData() }

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

        fun getUsage(playerUUID: UUID, quest: QuestData): UsageData {
            val playerUsage = usageMap.getOrPut(playerUUID) { mutableMapOf() }
            return playerUsage.getOrPut(quest.id) { UsageData() }
        }
    }

    fun init() {
        // 必要なら起動時の初期化処理をここに
    }

    fun shutdown() {
        activeQuests.values.forEach { data -> data.timer.stop() }
        activeQuests.clear()
    }

    fun startQuest(player: Player, quest: QuestData): Boolean {
        val uuid = player.uniqueId
        if (activeQuests.containsKey(uuid)) return false

        if (!PlayerQuestUsageManager.canUseQuest(uuid, quest)) {
            val usage = PlayerQuestUsageManager.getUsage(uuid, quest)
            val cooldownRemaining = quest.cooldownSeconds?.let {
                val elapsed = (System.currentTimeMillis() - usage.lastUsedTime) / 1000
                (it - elapsed).coerceAtLeast(0)
            } ?: 0

            val maxUse = quest.maxUseCount
            if (maxUse != null && usage.usedCount >= maxUse) {
                player.sendMessage("$prefix §c§lこのクエストはもう挑戦できません。利用回数の上限（${maxUse}回）に達しています。")
            } else if (quest.cooldownSeconds != null && cooldownRemaining > 0) {
                player.sendMessage("$prefix §c§lこのクエストは現在クールダウン中です。あと ${cooldownRemaining} 秒待ってください。")
            } else {
                player.sendMessage("$prefix §c§lこのクエストは現在利用できません。")
            }
            return false
        }

        val bossBar = createBossBar(quest)
        bossBar.addPlayer(player)

        val timer = STimer()
        quest.timeLimitSeconds?.let {
            val seconds = it.toInt()
            timer.setRemainingTime(seconds)
            timer.linkBossBar(bossBar, true)
            timer.addOnEndEvent {
                Bukkit.getScheduler().runTask(QuestPlugin.plugin as Plugin, Runnable {
                    cancelQuest(player)
                    player.sendMessage("$prefix §c§l制限時間内にクエストをクリアすることができませんでした")
                })
            }
            timer.start()
        }

        val startTime = System.currentTimeMillis()
        activeQuests[uuid] = PlayerQuestData(quest, startTime, 0, bossBar, timer)

        PlayerQuestUsageManager.recordQuestUse(uuid, quest)
        updateBossBar(player)

        // テレポート
        if (quest.teleportWorld != null && quest.teleportX != null && quest.teleportY != null && quest.teleportZ != null) {
            val world = Bukkit.getWorld(quest.teleportWorld!!)
            if (world != null) {
                val location = Location(world, quest.teleportX!!, quest.teleportY!!, quest.teleportZ!!)
                player.teleport(location)
                player.sendMessage("$prefix §a§lクエスト開始に伴い、指定された場所にテレポートしました！")
            } else {
                player.sendMessage("$prefix §c§lテレポート先のワールドが存在しません。")
            }
        }

        // パーティー共有
        if (quest.partyEnabled) {
            val members = red.man10.questplugin.party.PartyManager.getPartyMembers(player)
                .filter { it != player }
            for (member in members) {
                startQuest(member, quest)
            }
        }

        return true
    }

    fun cancelQuest(player: Player) {
        val uuid = player.uniqueId
        val data = activeQuests.remove(uuid) ?: return
        data.bossBar.removePlayer(player)
        data.timer.stop()
    }

    fun completeQuest(player: Player) {
        val uuid = player.uniqueId
        val data = activeQuests.remove(uuid) ?: return
        data.bossBar.removePlayer(player)
        data.timer.stop()
        player.sendMessage("$prefix §a§lクエスト[${data.quest.name}]をクリアしました！")

        for (cmd in data.quest.rewards) {
            val command = cmd.replace("%player%", player.name)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
        }

        if (data.quest.partyEnabled && data.quest.shareCompletion) {
            val members = red.man10.questplugin.party.PartyManager.getPartyMembers(player)
                .filter { it != player && activeQuests.containsKey(it.uniqueId) }
            for (member in members) {
                completeQuest(member)
            }
        }
    }

    fun addProgress(player: Player, amount: Int = 1) {
        val uuid = player.uniqueId
        val data = activeQuests[uuid] ?: return

        if (data.quest.partyEnabled && data.quest.shareProgress) {
            val members = red.man10.questplugin.party.PartyManager.getPartyMembers(player)
            for (member in members) {
                addProgressIndividual(member, amount)
            }
        } else {
            addProgressIndividual(player, amount)
        }
    }

    private fun addProgressIndividual(player: Player, amount: Int) {
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
        return when (type) {
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
