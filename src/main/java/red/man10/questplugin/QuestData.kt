package red.man10.questplugin

data class QuestData(
    val id: String,
    var name: String = "名前未設定",
    var type: QuestType = QuestType.KILL,
    var target: String = "",
    var amount: Int = 1,
    var timeLimitSeconds: Long? = null,
    var rewards: MutableList<String> = mutableListOf(),

    // 追加項目
    var cooldownSeconds: Long? = null,   // クールダウン時間(秒)、nullなら無し
    var maxUseCount: Int? = null          // 1プレイヤーあたりの最大利用回数、nullなら無制限
)
