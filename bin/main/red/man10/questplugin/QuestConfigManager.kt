package red.man10.questplugin

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object QuestConfigManager {

    private val questFile = File(QuestPlugin.plugin.dataFolder, "quests.yml")
    private lateinit var config: YamlConfiguration

    val quests = mutableMapOf<String, QuestData>()

    fun loadAllQuests() {
        if (!questFile.exists()) {
            questFile.parentFile.mkdirs()
            questFile.createNewFile()
        }

        config = YamlConfiguration.loadConfiguration(questFile)
        quests.clear()

        val section = config.getConfigurationSection("quests") ?: return

        for (id in section.getKeys(false)) {
            val q = section.getConfigurationSection(id) ?: continue

            val name = q.getString("name") ?: "名前未設定"
            val type = QuestType.fromString(q.getString("type") ?: "") ?: QuestType.KILL
            val target = q.getString("target") ?: ""
            val amount = q.getInt("amount")
            val timeLimit = q.getLong("timeLimit", -1)
            val rewards = q.getStringList("rewards").toMutableList()

            quests[id] = QuestData(
                id = id,
                name = name,
                type = type,
                target = target,
                amount = amount,
                timeLimitSeconds = if (timeLimit >= 0) timeLimit else null,
                rewards = rewards
            )
        }
    }
    fun reloadQuests() {
        loadAllQuests()
    }

    fun saveAllQuests() {
        val root = YamlConfiguration()

        for ((id, data) in quests) {
            val path = "quests.$id"
            root.set("$path.name", data.name)
            root.set("$path.type", data.type.name)
            root.set("$path.target", data.target)
            root.set("$path.amount", data.amount)
            root.set("$path.timeLimit", data.timeLimitSeconds ?: -1)
            root.set("$path.rewards", data.rewards)
        }

        root.save(questFile)
    }

    fun getQuest(id: String): QuestData? {
        return quests[id]
    }

    fun createQuest(id: String): QuestData {
        val data = QuestData(id = id)
        quests[id] = data
        return data
    }

    fun deleteQuest(id: String) {
        quests.remove(id)
    }

    fun exists(id: String): Boolean {
        return quests.containsKey(id)
    }

    fun getAllQuests(): Collection<QuestData> {
        return QuestConfigManager.quests.values
    }
}
