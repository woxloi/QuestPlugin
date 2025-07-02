package red.man10.questplugin.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import red.man10.questplugin.ActiveQuestManager
import red.man10.questplugin.QuestType

class QuestProgressListener : Listener {

    // モブ討伐クエスト用
    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        val killer = e.entity.killer ?: return
        if (!ActiveQuestManager.isQuesting(killer)) return

        val quest = ActiveQuestManager.getQuest(killer) ?: return
        if (quest.type != QuestType.KILL) return

        // ターゲット指定があればチェック（例: 特定mob名）
        val targetName = quest.target.lowercase()
        val entityName = e.entity.type.name.lowercase()
        if (targetName != "any" && targetName != entityName) return

        ActiveQuestManager.addProgress(killer, 1)
    }

    // アイテム収集クエスト用
    @EventHandler
    fun onPlayerPickupItem(e: PlayerPickupItemEvent) {
        val player = e.player
        if (!ActiveQuestManager.isQuesting(player)) return

        val quest = ActiveQuestManager.getQuest(player) ?: return
        if (quest.type != QuestType.COLLECT) return

        val targetItem = quest.target.lowercase()
        val itemName = e.item.itemStack.type.name.lowercase()
        if (targetItem != "any" && targetItem != itemName) return

        ActiveQuestManager.addProgress(player, e.item.itemStack.amount)
    }

    // ブロック設置クエスト用
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val player = e.player
        if (!ActiveQuestManager.isQuesting(player)) return

        val quest = ActiveQuestManager.getQuest(player) ?: return
        if (quest.type != QuestType.PLACE) return

        val targetBlock = quest.target.lowercase()
        val placedBlock = e.block.type.name.lowercase()
        if (targetBlock != "any" && targetBlock != placedBlock) return

        ActiveQuestManager.addProgress(player, 1)
    }
}
