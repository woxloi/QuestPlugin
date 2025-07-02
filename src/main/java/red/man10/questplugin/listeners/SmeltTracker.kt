package red.man10.questplugin.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.Bukkit
import red.man10.questplugin.ActiveQuestManager
import red.man10.questplugin.QuestType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SmeltTracker : Listener {

    private val lastSmelters = ConcurrentHashMap<UUID, Long>()

    /**
     * プレイヤーがかまど系インベントリを操作した時間を記録
     */
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as? Player ?: return
        val invType = e.inventory.type
        if (invType == InventoryType.FURNACE || invType == InventoryType.BLAST_FURNACE || invType == InventoryType.SMOKER) {
            lastSmelters[player.uniqueId] = System.currentTimeMillis()
        }
    }

    /**
     * FurnaceSmeltEventで直近にかまど操作を行ったプレイヤーを返す
     */
    fun getRecentSmelter(timeoutMillis: Long = 5000): Player? {
        val now = System.currentTimeMillis()
        val recent = lastSmelters.entries
            .filter { now - it.value <= timeoutMillis }
            .maxByOrNull { it.value }
            ?: return null
        return Bukkit.getPlayer(recent.key)
    }

    /**
     * FurnaceSmeltEventを受けてクエスト進行処理
     */
    @EventHandler
    fun onSmeltItem(e: FurnaceSmeltEvent) {
        val player = getRecentSmelter() ?: return
        if (!ActiveQuestManager.isQuesting(player)) return

        val quest = ActiveQuestManager.getQuest(player) ?: return
        if (quest.type != QuestType.SMELT) return

        val targetItem = quest.target.lowercase()
        val smeltedItem = e.result.type.name.lowercase()
        if (targetItem != "any" && targetItem != smeltedItem) return

        ActiveQuestManager.addProgress(player, 1)
    }
}
