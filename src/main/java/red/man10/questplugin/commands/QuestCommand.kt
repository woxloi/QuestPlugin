package red.man10.questplugin.commands

import com.shojabon.scommandrouter.SCommandRouter.SCommandArgumentType
import com.shojabon.scommandrouter.SCommandRouter.SCommandObject
import com.shojabon.scommandrouter.SCommandRouter.SCommandRouter
import net.kyori.adventure.text.Component
import red.man10.questplugin.QuestPlugin
import red.man10.questplugin.commands.subcommands.*

class QuestCommand(val plugin: QuestPlugin) : SCommandRouter(plugin, "quest") {

    init {
        registerCommands()
        registerEvents()
        pluginPrefix = "§a[§6§lQuestPlugin§a]"
    }

    private fun registerEvents() {
        setNoPermissionEvent { e ->
            e.sender.sendMessage("$pluginPrefix§c§lあなたは権限がありません")
        }
        setOnNoCommandFoundEvent { e ->
            e.sender.sendMessage("$pluginPrefix§c§lコマンドが存在しません")
        }
    }

    private fun registerCommands() {
// create コマンド
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("create")
                .argument("クエストID") // ここだけ補完は普通の文字列でOK
                .permission("quest.create")
                .explanation("クエストを作成する")
                .executor(QuestCreateCommand(plugin))
        )

// set コマンド
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("set")
                .argument("クエストID")
                .argument("key", "name", "type", "target", "amount", "timelimit") // key の補完はここだけ
                .argument("value")
                .permission("quest.set")
                .explanation("クエストの設定を変更する")
                .executor(QuestSetCommand(plugin))
        )

// addreward コマンド
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("addreward")
                .argument("クエストID")
                .argument("コマンド")
                .permission("quest.addreward")
                .explanation("クエストに報酬コマンドを追加する")
                .executor(QuestAddRewardCommand(plugin))
        )

// save コマンド
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("save")
                .permission("quest.save")
                .explanation("クエスト設定を保存する")
                .executor(QuestSaveConfigCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("leave")
                .permission("quest.leave")
                .explanation("クエストを中断する")
                .executor(QuestLeaveCommand())
        )
        addCommand(
            SCommandObject()
                .prefix("reload")
                .permission("quest.reload")
                .explanation("クエスト設定を再読み込みする")
                .executor(QuestReloadCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("start")
                .argument("クエストID")
                .permission("quest.start")
                .explanation("クエストを開始する")
                .executor(QuestStartCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("list")
                .permission("quest.use")
                .explanation("現在使用可能なクエストを見る")
                .executor(QuestListCommand(plugin))
        )
    }
}
