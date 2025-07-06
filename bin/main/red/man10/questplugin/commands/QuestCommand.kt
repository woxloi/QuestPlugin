package red.man10.questplugin.commands

import com.shojabon.mcutils.Utils.SCommandRouter.SCommandArgument
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandArgumentType
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandObject
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandRouter
import red.man10.questplugin.QuestPlugin
import red.man10.questplugin.QuestPlugin.Companion.plugin
import red.man10.questplugin.commands.subcommands.*

class QuestCommand(plugin: QuestPlugin) : SCommandRouter() {
    // plugin変数の再定義を削除し、コンストラクタで受け取ったpluginをそのまま使用
    init {
        registerCommands()
        registerEvents()
        pluginPrefix = "§a[§6§lQuestPlugin§a]"
    }

    fun registerEvents() {
        setNoPermissionEvent { e -> e.sender.sendMessage("${pluginPrefix}§c§lあなたは権限がありません") }
        setOnNoCommandFoundEvent { e -> e.sender.sendMessage("${pluginPrefix}§c§lコマンドが存在しません") }
    }

    fun registerCommands() {

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("create"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addRequiredPermission("quest.config.create")
                .addExplanation("クエストを作成する")
                .setExecutor(QuestCreateCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("set"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addArgument(SCommandArgument().addAllowedString("name"))
                .addArgument(SCommandArgument().addAllowedString("内部名"))
                .addRequiredPermission("quest.config.name")
                .addExplanation("クエストの名前を設定する")
                .setExecutor(QuestSetCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("set"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addArgument(SCommandArgument().addAllowedString("type"))
                .addArgument(SCommandArgument().addAllowedString("タイプ名"))
                .addRequiredPermission("quest.config.set")
                .addExplanation("クエストタイプを設定する")
                .setExecutor(QuestSetCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("set"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addArgument(SCommandArgument().addAllowedString("target"))
                .addArgument(SCommandArgument().addAllowedString("ターゲット名"))
                .addRequiredPermission("quest.config.target")
                .addExplanation("クエストのターゲットを設定する")
                .setExecutor(QuestSetCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("set"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addArgument(SCommandArgument().addAllowedString("amount"))
                .addArgument(SCommandArgument().addAllowedString("個数"))
                .addRequiredPermission("quest.config.amount")
                .addExplanation("クエストクリア時に必要な個数を設定する")
                .setExecutor(QuestSetCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("set"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addArgument(SCommandArgument().addAllowedString("timelimit"))
                .addArgument(SCommandArgument().addAllowedString("時間"))
                .addRequiredPermission("quest.config.timelimit")
                .addExplanation("クエストの制限時間を設定する")
                .setExecutor(QuestSetCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("addreward"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addArgument(SCommandArgument().addAllowedString("コマンド"))
                .addRequiredPermission("quest.config.addreward")
                .addExplanation("クエストに報酬コマンドを追加する")
                .setExecutor(QuestAddRewardCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("config"))
                .addArgument(SCommandArgument().addAllowedString("save"))
                .addRequiredPermission("quest.config.save")
                .addExplanation("クエスト設定を保存する")
                .setExecutor(QuestSaveConfigCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("info"))
                .addArgument(SCommandArgument().addAllowedString("クエスト名"))
                .addRequiredPermission("quest.info")
                .addExplanation("クエストの詳細を見る")
                .setExecutor(QuestInfoCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("leave"))
                .addRequiredPermission("quest.leave")
                .addExplanation("クエストを中断する")
                .setExecutor(QuestLeaveCommand(plugin))
        )
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("party"))
                .addArgument(SCommandArgument().addAllowedString("create"))
                .addRequiredPermission("quest.party")
                .addExplanation("パーティーを作成する")
                .setExecutor(QuestPartyCommand())
        )
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("party"))
                .addArgument(SCommandArgument().addAllowedString("invite"))
                .addArgument(SCommandArgument().addAllowedString("プレイヤー名").addAllowedType(SCommandArgumentType.ONLINE_PLAYER))
                .addRequiredPermission("quest.party")
                .addExplanation("プレイヤーをパーティーに招待する")
                .setExecutor(QuestPartyCommand())
        )
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("party"))
                .addArgument(SCommandArgument().addAllowedString("join"))
                .addArgument(SCommandArgument().addAllowedString("プレイヤー名").addAllowedType(SCommandArgumentType.ONLINE_PLAYER))
                .addRequiredPermission("quest.party")
                .addExplanation("指定プレイヤーのパーティーに参加する")
                .setExecutor(QuestPartyCommand())
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("party"))
                .addArgument(SCommandArgument().addAllowedString("leave"))
                .addRequiredPermission("quest.party")
                .addExplanation("パーティーを離脱する")
                .setExecutor(QuestPartyCommand())
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("party"))
                .addArgument(SCommandArgument().addAllowedString("disband"))
                .addRequiredPermission("quest.party")
                .addExplanation("パーティーを解散する（リーダーのみ）")
                .setExecutor(QuestPartyCommand())
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("party"))
                .addArgument(SCommandArgument().addAllowedString("kick"))
                .addArgument(SCommandArgument().addAllowedString("プレイヤー名").addAllowedType(SCommandArgumentType.ONLINE_PLAYER))
                .addRequiredPermission("quest.party")
                .addExplanation("パーティーからプレイヤーを追放する")
                .setExecutor(QuestPartyCommand())
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("party"))
                .addRequiredPermission("quest.party")
                .addExplanation("パーティー情報を表示する")
                .setExecutor(QuestPartyCommand())
        )

        // その他
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("reload"))
                .addRequiredPermission("quest.reload")
                .addExplanation("クエスト設定を再読み込みする")
                .setExecutor(QuestReloadCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("start"))
                .addArgument(SCommandArgument().addAllowedString("クエストID"))
                .addRequiredPermission("quest.start")
                .addExplanation("クエストを開始する")
                .setExecutor(QuestStartCommand(plugin))
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("list"))
                .addRequiredPermission("quest.use")
                .addExplanation("現在使用可能なクエストを見る")
                .setExecutor(QuestListCommand(plugin))
        )
    }
    }


