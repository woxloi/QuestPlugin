package red.man10.questplugin

import com.shojabon.mcutils.Utils.SScoreboard
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent.suggestCommand
import red.man10.questplugin.utils.STimer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import red.man10.questplugin.QuestPlugin.Companion.plugin
import red.man10.questplugin.party.PartyManager
import java.io.File
import java.util.*

object ActiveQuestManager {
    private val scoreboard = SScoreboard("TEST")
    private const val prefix = "§a[§6§lQuestPlugin§a]"

    private val activeQuests = mutableMapOf<UUID, PlayerQuestData>()

    data class PlayerQuestData(
        val quest: QuestData,
        val startTime: Long,
        var progress: Int = 0,
        val bossBar: BossBar,
        val timer: STimer,
        val questScoreboard: QuestScoreboard,
        var deathCount: Int = 0,
        var originalLocation: Location
    )
    data class QuestHistoryEntry(
        val questId: String,
        val questName: String,
        val completedAt: Long,
        val success: Boolean,
        val progress: Int,
        val deathCount: Int
    )

    // 追加: プレイヤーごとのクエスト履歴保持マップ
    private val questHistories = mutableMapOf<UUID, MutableList<QuestHistoryEntry>>()

    // 履歴保存用ファイル & YamlConfiguration
    private val historyFile = File(plugin.dataFolder, "quest_histories.yml")
    private val historyConfig = if (historyFile.exists()) YamlConfiguration.loadConfiguration(historyFile) else YamlConfiguration()

    object PlayerQuestUsageManager {
        private val usageMap = mutableMapOf<UUID, MutableMap<String, UsageData>>()

        data class UsageData(
            var lastUsedTime: Long = 0,
            var usedCount: Int = 0,
            var lastRecoveryTime: Long = System.currentTimeMillis()
        )
        // 死亡を1回カウント（ライフ減少）
        fun addDeath(player: Player) {
            val data = activeQuests[player.uniqueId] ?: return
            data.deathCount++
            val quest = data.quest

            // 最大ライフを取得（設定されていなければ無制限扱い）
            val maxLives = quest.maxLives ?: return

            val partyMembers = if (quest.partyEnabled) {
                PartyManager.getPartyMembers(player).distinctBy { it.uniqueId }
            } else {
                listOf(player)
            }

            // 全員の最大ライフ合計
            val totalMaxLives = maxLives * partyMembers.size

            // 全員の合計死亡数
            val totalDeaths = partyMembers.sumOf { member ->
                activeQuests[member.uniqueId]?.deathCount ?: 0
            }

            val remainingLives = (totalMaxLives - totalDeaths).coerceAtLeast(0)

            // ライフ0でクエスト失敗扱いにするならここでキャンセル
            if (remainingLives <= 0) {
                partyMembers.forEach {
                    cancelQuest(it)
                    it.sendMessage("$prefix §c§lライフが尽きたためクエスト失敗です。")
                }
            } else {
                // ライフ減少の通知など必要ならここに
            }
        }

        // ライフ残りを取得（合計）
        fun getRemainingLives(player: Player): Int {
            val data = activeQuests[player.uniqueId] ?: return 0
            val quest = data.quest
            val maxLives = quest.maxLives ?: return Int.MAX_VALUE

            val partyMembers = if (quest.partyEnabled) {
                PartyManager.getPartyMembers(player).distinctBy { it.uniqueId }
            } else {
                listOf(player)
            }

            val totalMaxLives = maxLives * partyMembers.size

            val totalDeaths = partyMembers.sumOf { member ->
                activeQuests[member.uniqueId]?.deathCount ?: 0
            }

            return (totalMaxLives - totalDeaths).coerceAtLeast(0)
        }
        fun canUseQuest(playerUUID: UUID, quest: QuestData): Boolean {
            val now = System.currentTimeMillis()
            val playerUsage = usageMap.getOrPut(playerUUID) { mutableMapOf() }
            val usage = playerUsage.getOrPut(quest.id) { UsageData() }

            val cooldown = quest.cooldownSeconds
            val maxUses = quest.maxUseCount

            if (cooldown != null && maxUses != null) {
                val elapsed = (now - usage.lastRecoveryTime) / 1000
                val recoveries = (elapsed / cooldown).toInt()

                if (recoveries > 0) {
                    usage.usedCount = (usage.usedCount - recoveries).coerceAtLeast(0)
                    usage.lastRecoveryTime += recoveries * cooldown * 1000
                }

                if (usage.usedCount >= maxUses) return false
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
    // クエスト履歴を追加する関数
    private fun addQuestHistory(uuid: UUID, data: PlayerQuestData, success: Boolean) {
        val list = questHistories.getOrPut(uuid) { mutableListOf() }
        list.add(
            QuestHistoryEntry(
                questId = data.quest.id,
                questName = data.quest.name,
                completedAt = System.currentTimeMillis(),
                success = success,
                progress = data.progress,
                deathCount = data.deathCount
            )
        )
    }

    // 履歴の保存
    fun saveQuestHistories() {
        val mapToSave = questHistories.mapKeys { it.key.toString() }  // ここでUUIDを文字列に変換
            .mapValues { entry ->
                entry.value.map {
                    mapOf(
                        "questId" to it.questId,
                        "questName" to it.questName,
                        "completedAt" to it.completedAt,
                        "success" to it.success,
                        "progress" to it.progress,
                        "deathCount" to it.deathCount
                    )
                }
            }
        historyConfig.set("histories", mapToSave)
        historyConfig.save(historyFile)
    }


    // 履歴の読み込み
    fun loadQuestHistories() {
        if (!historyFile.exists()) return

        val section = historyConfig.getConfigurationSection("histories") ?: return
        for (uuidStr in section.getKeys(false)) {
            val list = section.getMapList(uuidStr)
            val historyList = mutableListOf<QuestHistoryEntry>()
            for (map in list) {
                val questId = map["questId"] as? String ?: continue
                val questName = map["questName"] as? String ?: ""
                val completedAt = (map["completedAt"] as? Number)?.toLong() ?: 0L
                val success = map["success"] as? Boolean ?: false
                val progress = (map["progress"] as? Number)?.toInt() ?: 0
                val deathCount = (map["deathCount"] as? Number)?.toInt() ?: 0

                historyList.add(QuestHistoryEntry(questId, questName, completedAt, success, progress, deathCount))
            }
            questHistories[UUID.fromString(uuidStr)] = historyList
        }
    }
    fun init() {
        // スポーン位置の定期更新（10秒ごと）
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            for ((uuid, data) in activeQuests) {
                val player = Bukkit.getPlayer(uuid) ?: continue
                if (player.isOnline && !player.isDead) {
                    // onGround 判定を入れたければ以下に条件追加
                    data.originalLocation = player.location.clone()
                }
            }
        }, 20L * 10, 20L * 10) // 最初の遅延: 10秒、間隔: 10秒
        loadQuestHistories()
    }


    fun shutdown() {
        activeQuests.values.forEach { data -> data.timer.stop() }
        activeQuests.clear()
        saveQuestHistories()
    }

    fun startQuest(player: Player, quest: QuestData): Boolean {
        val uuid = player.uniqueId
        // 同一クエストがすでに他のプレイヤーによって開始されていたら中断
        if (activeQuests.values.any { it.quest.id == quest.id }) {
            player.sendMessage("$prefix §c§lこのクエストは現在別のパーティーが進行中です。")
            return false
        }
        if (activeQuests.containsKey(uuid)) return false

        val partyMembers = if (quest.partyEnabled) {
            val members = PartyManager.getPartyMembers(player)
            if (members.isEmpty()) {
                player.sendMessage("$prefix §c§lこのクエストはパーティー専用です。パーティーを作成してから再度お試しください。")
                player.sendMessage(
                    text("§c§l[ここをクリックでパーティーコマンドを自動入力する]")
                        .clickEvent(suggestCommand("/quest party create"))
                )
                return false
            }

            val maxMembers = quest.partyMaxMembers
            if (maxMembers != null && members.size > maxMembers) {
                player.sendMessage("$prefix §c§lこのクエストのパーティーメンバー上限は $maxMembers 人です。現在の人数は ${members.size} 人です。")
                return false
            }

            val nonEmpty = members.filter { it.inventory.contents.any { item -> item != null } }
            if (nonEmpty.isNotEmpty()) {
                player.sendMessage("$prefix §c§l以下のプレイヤーのインベントリが空ではありません。クエストを開始できません。")
                nonEmpty.forEach { player.sendMessage("§7§l - §c§l${it.name}") }
                return false
            }

            for (member in members) {
                val memberUUID = member.uniqueId
                val usage = PlayerQuestUsageManager.getUsage(memberUUID, quest)
                if (quest.maxUseCount != null && quest.cooldownSeconds != null) {
                    val remaining = quest.cooldownSeconds!! -
                            ((System.currentTimeMillis() - usage.lastRecoveryTime) / 1000 % quest.cooldownSeconds!!)
                    member.sendMessage("$prefix §c§lこのクエストは最大使用回数に達しています。あと §e§l${remaining}秒§c§lで1回分回復します。")
                    return false
                }

                if (!PlayerQuestUsageManager.canUseQuest(memberUUID, quest)) {
                    if (quest.cooldownSeconds != null) {
                        val remaining = quest.cooldownSeconds!! - ((System.currentTimeMillis() - usage.lastUsedTime) / 1000)
                        if (remaining > 0) {
                            member.sendMessage("$prefix §c§lこのクエストは現在クールダウン中です。あと §e§l$remaining 秒§c§lお待ちください。")
                            return false
                        }
                    }
                    if (quest.maxUseCount != null && usage.usedCount >= quest.maxUseCount!!) {
                        member.sendMessage("$prefix §c§lこのクエストは最大使用回数に達しています。")
                        return false
                    }
                }
            }

            // OKな場合に全員分記録
            members.forEach { PlayerQuestUsageManager.recordQuestUse(it.uniqueId, quest) }

            members
        } else {
            val usage = PlayerQuestUsageManager.getUsage(uuid, quest)

            if (!PlayerQuestUsageManager.canUseQuest(uuid, quest)) {
                if (quest.cooldownSeconds != null) {
                    val remaining = quest.cooldownSeconds!! - ((System.currentTimeMillis() - usage.lastUsedTime) / 1000)
                    if (remaining > 0) {
                        player.sendMessage("$prefix §cこのクエストは現在クールダウン中です。あと §e$remaining 秒§cお待ちください。")
                        return false
                    }
                }
                if (quest.maxUseCount != null && usage.usedCount >= quest.maxUseCount!!) {
                    player.sendMessage("$prefix §cこのクエストは最大使用回数に達しています。")
                    return false
                }
            }

            PlayerQuestUsageManager.recordQuestUse(uuid, quest)

            listOf(player)
        }

        // タイマー、ボスバー共通作成（パーティーで共有）
        val bossBar = createBossBar(quest)
        val timer = STimer()
        val startTime = System.currentTimeMillis()

        quest.timeLimitSeconds?.let {
            val seconds = it.toInt()
            timer.setRemainingTime(seconds)
            timer.linkBossBar(bossBar, true)
            timer.addOnEndEvent {
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    partyMembers.forEach {
                        cancelQuest(it)
                        it.sendMessage("$prefix §c§l制限時間内にクエストをクリアすることができませんでした")
                    }
                })
            }
        }

        timer.addOnIntervalEvent { remainingSeconds ->
            Bukkit.getScheduler().runTask(plugin, Runnable {
                partyMembers.forEach { member ->
                    val data = activeQuests[member.uniqueId] ?: return@forEach
                    data.questScoreboard.updateProgress(data.progress)
                    data.questScoreboard.updateRemainingTime(remainingSeconds.toLong())
                }
            })
        }

        timer.start()

        // 全員に bossbar、scoreboard、startCommand 実行、テレポート
        for (member in partyMembers) {
            val uuid = member.uniqueId

            val scoreboard = QuestScoreboard(member, quest)
            scoreboard.show()

            activeQuests[uuid] = PlayerQuestData(
                quest,
                startTime,
                0,
                bossBar,
                timer,
                scoreboard,
                0,
                member.location.clone()
            )

            bossBar.addPlayer(member)

            quest.startCommands.forEach { cmd ->
                val command = cmd.replace("%player%", member.name)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            }

            if (quest.teleportWorld != null && quest.teleportX != null && quest.teleportY != null && quest.teleportZ != null) {
                val worldName = quest.teleportWorld!!
                val world = Bukkit.getWorld(worldName)

                if (world != null) {
                    val location = Location(world, quest.teleportX!!, quest.teleportY!!, quest.teleportZ!!)
                    member.teleport(location)
                    member.sendMessage("$prefix §a§lクエスト開始に伴い、指定された場所にテレポートしました！")
                    Bukkit.getLogger().info("[QuestPlugin] ${member.name} を $worldName(${location.blockX}, ${location.blockY}, ${location.blockZ}) にテレポートしました。")
                } else {
                    member.sendMessage("$prefix §c§lテレポート先のワールドが存在しません（$worldName）")
                    Bukkit.getLogger().warning("[QuestPlugin] テレポート失敗: ワールド '$worldName' が存在しません。")

                    // 利用可能なワールド一覧を表示
                    val availableWorlds = Bukkit.getWorlds().joinToString(", ") { it.name }
                    Bukkit.getLogger().info("[QuestPlugin] 使用可能なワールド一覧: $availableWorlds")
                }
            }


            updateBossBar(member)
        }

        return true
    }



    fun cancelQuest(player: Player) {
        val uuid = player.uniqueId
        val data = activeQuests.remove(uuid) ?: return
        data.bossBar.removePlayer(player)
        data.timer.stop()
        data.questScoreboard.hide()
        Bukkit.getScheduler().runTask(plugin, Runnable {
            player.gameMode = GameMode.SURVIVAL
        })

        addQuestHistory(uuid, data, false)
        saveQuestHistories()  // ←追加

        val partyMembers = PartyManager.getPartyMembers(player)

        // パーティーメンバーのインベントリをクリアし、キルコマンドを実行
        for (member in partyMembers) {
            member.inventory.clear()
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill ${member.name}")
        }

        if (PartyManager.disbandParty(player)) {
            player.sendMessage("$prefix §a§lクエストが終了とともにパーティーが解散されました")
        } else {
            player.sendMessage("$prefix §c§lパーティー解散に失敗しました。")
        }
    }

    fun completeQuest(player: Player) {
        val uuid = player.uniqueId
        val data = activeQuests.remove(uuid) ?: return
        data.bossBar.removePlayer(player)
        data.timer.stop()
        Bukkit.getScheduler().runTask(plugin, Runnable {
            player.gameMode = GameMode.SURVIVAL
        })
        player.sendMessage("$prefix §a§lクエスト[${data.quest.name}]をクリアしました！")
        data.questScoreboard.hide()

        addQuestHistory(uuid, data, true)
        saveQuestHistories()  // ←追加

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
        if (PartyManager.disbandParty(player)) {
            player.sendMessage("$prefix §a§lクエストが終了とともにパーティーが解散されました")
        } else {
            player.sendMessage("$prefix §c§lパーティー解散に失敗しました。")
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
    fun getPlayerData(uuid: UUID): PlayerQuestData? {
        return activeQuests[uuid]
    }
}
