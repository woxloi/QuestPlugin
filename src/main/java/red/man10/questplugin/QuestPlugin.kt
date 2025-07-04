package red.man10.questplugin

import org.bukkit.plugin.java.JavaPlugin
import red.man10.questplugin.commands.QuestCommand
import red.man10.questplugin.listeners.QuestProgressListener
import red.man10.questplugin.listeners.SmeltTracker

val prefix = "§a[§6§lQuestPlugin§a]"
    val version = "2025/7/2"
class QuestPlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: QuestPlugin
    }

    override fun onEnable() {
        plugin = this

        saveDefaultConfig()
        QuestConfigManager.loadAllQuests()
        ActiveQuestManager.init()

        // コマンド登録
        QuestCommand(this)

        // イベント登録
        server.pluginManager.registerEvents(QuestProgressListener(), this)
        server.pluginManager.registerEvents(SmeltTracker, this)

        logger.info("QuestPlugin has been enabled.")
    }

    override fun onDisable() {
        ActiveQuestManager.shutdown()
        logger.info("QuestPlugin has been disabled.")
    }
}
