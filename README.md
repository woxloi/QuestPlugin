QuestPlugin
Minecraftサーバー用のクエスト管理プラグインです。
プレイヤーは討伐、収集、設置など多様なタイプのクエストを受けられます。

✅ 主な機能
Mob討伐・アイテム収集・ブロック設置・探索など多様なクエストタイプに対応

時間制限付きクエスト

クエスト進行状況を BossBar で表示

クリア時にコマンドによる報酬実行

クールダウン／使用回数制限対応

コマンドベースでのクエスト作成・編集・開始・一覧表示

🧩 インストール方法
QuestPlugin.jar を plugins フォルダに入れる

サーバーを起動して plugins/QuestPlugin/ フォルダが生成されるのを確認

quests.yml にクエスト設定を記述するか、コマンドで作成

🧪 コマンド一覧
コマンド	説明	権限
/quest config create <ID>	新しいクエストを作成	quest.create
/quest config set <ID> <key> <value>	クエストの設定を変更	quest.set
/quest config addreward <ID> <コマンド>	クエストクリア時に実行する報酬コマンドを追加	quest.addreward
/quest config save	設定中のすべてのクエストを保存	quest.save
/quest reload	クエスト設定をファイルから再読み込み	quest.reload
/quest start <ID>	クエストを開始	quest.start
/quest leave	現在のクエストをキャンセル	quest.leave
/quest list	使用可能なクエスト一覧を表示	quest.use

⚙️ クエスト設定項目（quests.yml）
yaml
コピーする
編集する
quests:
  sample_quest:
    name: ダイヤ収集
    type: COLLECT
    target: DIAMOND
    amount: 10
    timeLimit: 300
    cooldown: 600        # クールダウン（秒）※任意
    maxAttempts: 3       # 最大挑戦回数（プレイヤーごと）※任意
    rewards:
      - give %player% diamond 3
      - say %player% がクエストを達成しました！
key	説明	例
name	クエストの表示名	ドラゴン討伐
type	クエストタイプ（下記参照）	KILL, COLLECT など
target	Mob名・ブロック名・アイテム名など	ZOMBIE, DIAMOND
amount	目標数値（討伐数、収集数など）	10
timeLimit	時間制限（秒）※0または指定なしで無制限	300（5分）
cooldown	クエスト完了後のクールダウン（秒）	600（10分）
maxAttempts	クエストの最大挑戦回数（プレイヤーごと）※0で無制限	3
rewards	クエストクリア時に実行されるコマンド（複数可）	give %player% diamond 3

📚 クエストタイプ一覧（type）
KILL: Mob討伐（例：ZOMBIE）

COLLECT: アイテム収集（例：DIAMOND）

MINE: ブロック採掘（例：STONE）

PLACE: ブロック設置

BREAK: ブロック破壊

TRAVEL: 特定座標に到達

CRAFT: アイテムクラフト

SMELT: アイテム精錬

FISH: 釣り成功

SLEEP: ベッドで寝る

CHAT: メッセージを送信

COMMAND: コマンドを実行する

INTERACT: 対象とインタラクト

DAMAGE_TAKEN: ダメージを受ける

DAMAGE_GIVEN: ダメージを与える

SHOOT: 弓やクロスボウで攻撃

LEVEL: 指定レベルに到達

EXP_GAINED: 経験値獲得

TIME_PLAYED: 一定時間プレイ

TAME: 動物を手懐ける

BREED: 繁殖

TRADE: 村人と取引

RIDE: 動物や乗り物に乗る

💡 補足情報
%player% は報酬コマンド内で実行者の名前に置き換わります

quests.yml を手動で編集後は /quest reload で読み込み直してください

クールダウン・回数制限はプレイヤーごとに記録されます（永続化対応推奨）

👥 開発・拡張予定
パーティ用共有クエスト

GUIでのクエスト選択・進行

外部データベース保存対応（MySQL等）

🪪 ライセンス
MIT License

必要に応じてマルチワールド対応やイベント条件追加などの拡張も可能です。
何か問題・要望があれば GitHub の Issue へお願いします。
