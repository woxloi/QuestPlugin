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
                .argument("name")
                .argument("value")
                .permission("quest.set")
                .explanation("クエストの名前を設定する")
                .executor(QuestSetCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("set")
                .argument("クエストID")
                .argument("type")
                .argument("value")
                .permission("quest.set")
                .explanation("クエストタイプを設定する")
                .executor(QuestSetCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("set")
                .argument("クエストID")
                .argument("target")
                .argument("value")
                .permission("quest.set")
                .explanation("クエストのターゲットを設定する")
                .executor(QuestSetCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("set")
                .argument("クエストID")
                .argument("amount")
                .argument("value")
                .permission("quest.set")
                .explanation("クエストの個数を設定する")
                .executor(QuestSetCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("config")
                .argument("set")
                .argument("クエストID")
                .argument("timelimit")
                .argument("value")
                .permission("quest.set")
                .explanation("クエストの制限時間を設定する")
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
                .prefix("info")
                .argument("クエスト名") // ここだけ補完は普通の文字列でOK
                .permission("quest.info")
                .explanation("クエストの詳細を見ます")
                .executor(QuestInfoCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .prefix("leave")
                .permission("quest.leave")
                .explanation("クエストを中断する")
                .executor(QuestLeaveCommand())
        )
// party create
        addCommand(
            SCommandObject()
                .prefix("party")
                .argument("create")
                .permission("quest.party")
                .explanation("パーティーを作成する")
                .executor(QuestPartyCommand())
        )

// party invite <player>
        addCommand(
            SCommandObject()
                .prefix("party")
                .argument("invite")
                .argument("player", SCommandArgumentType.ONLINE_PLAYER)
                .permission("quest.party")
                .explanation("プレイヤーをパーティーに招待する")
                .executor(QuestPartyCommand())
        )

// party join <player>
        addCommand(
            SCommandObject()
                .prefix("party")
                .argument("join")
                .argument("player", SCommandArgumentType.ONLINE_PLAYER)
                .permission("quest.party")
                .explanation("指定プレイヤーのパーティーに参加する")
                .executor(QuestPartyCommand())
        )

// party leave
        addCommand(
            SCommandObject()
                .prefix("party")
                .argument("leave")
                .permission("quest.party")
                .explanation("パーティーを離脱する")
                .executor(QuestPartyCommand())
        )

// party disband
        addCommand(
            SCommandObject()
                .prefix("party")
                .argument("disband")
                .permission("quest.party")
                .explanation("パーティーを解散する（リーダーのみ）")
                .executor(QuestPartyCommand())
        )

// party kick <player>
        addCommand(
            SCommandObject()
                .prefix("party")
                .argument("kick")
                .argument("player", SCommandArgumentType.ONLINE_PLAYER)
                .permission("quest.party")
                .explanation("パーティーからプレイヤーを追放する")
                .executor(QuestPartyCommand())
        )

// ✅ 最後に base: /quest party（info表示）
        addCommand(
            SCommandObject()
                .prefix("party")
                .permission("quest.party")
                .explanation("パーティー情報を表示する")
                .executor(QuestPartyCommand())
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
