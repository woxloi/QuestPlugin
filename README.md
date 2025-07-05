# QuestPlugin

Minecraftサーバー用のクエスト管理プラグインです。  
多彩なクエストタイプに対応し、パーティー機能や進行共有などの拡張も可能です。

---

## 機能

- 討伐、収集、探索、採掘、設置など多種多様なクエストタイプ対応
- 時間制限付きクエストの実装
- クエスト進行状況のBossBar表示
- クエストクリアでコマンド実行による報酬付与
- コマンドベースでのクエスト作成・編集・開始・一覧表示
- パーティー機能によるクエスト進行の共有や共同プレイ対応

---

## インストール

1. プラグインのjarファイルを `plugins` フォルダに入れる
2. サーバーを起動し、`plugins/QuestPlugin` フォルダが生成されるのを確認
3. `quests.yml` でクエスト設定を管理

---

## コマンド一覧

| コマンド                                       | 説明                                | 権限                       |
|--------------------------------------------|-----------------------------------|--------------------------|
| `/quest config create <ID>`                | 新しいクエストを作成する                      | `quest.config.create`    |
| `/quest config set <ID> type <value>`      | クエストのタイプを設定する                     | `quest.config.set`       |
| `/quest config set <ID> name <value>`      | クエストの名前を変更する                      | `quest.config.name`      |
| `/quest config set <ID> target <value>`    | クエストのターゲットを変更する                   | `quest.config.target`    |
| `/quest config set <ID> amount <value>`    | クエストの個数を変更する                      | `quest.config.amount`    |
| `/quest config set <ID> timelimit <value>` | クエストの制限時間を変更する                    | `quest.config.timelimit` |
| `/quest config addreward <ID> <コマンド>`      | クエスト報酬コマンドを追加(コマンド内置換 `%player%`) | `quest.config.addreward` |
| `/quest config save`                       | すべてのクエスト設定を保存する                   | `quest.config.save`      |
| `/quest leave`                             | クエストから離脱する                        | `quest.leave`            |
| `/quest info クエスト名`                        | クエストの詳細を見る                        | `quest.info`             |
| `/quest start <ID>`                        | クエストを開始する                         | `quest.start`            |
| `/quest list`                              | 利用可能なクエストの一覧を表示                   | `quest.use`              |
| `/quest reload`                            | プラグイン設定をリロード                      | `quest.reload`           |
| `/quest party`                             | 自分のパーティー情報を表示                     | `quest.party`            |
| `/quest party create`                      | パーティーを作成する                        | `quest.party`            |
| `/quest party invite <player>`             | プレイヤーをパーティーに招待                    | `quest.party`            |
| `/quest party join <player>`               | 指定プレイヤーのパーティーに参加                  | `quest.party`            |
| `/quest party leave`                       | パーティーを離脱する                        | `quest.party`            |
| `/quest party disband`                     | パーティーを解散する（リーダーのみ）                | `quest.party`            |
| `/quest party kick <player>`               | パーティーからプレイヤーを追放                   | `quest.party`            |

---

## クエスト設定項目

| key             | 説明                       | 例                                 |
|-----------------|--------------------------|-----------------------------------|
| `name`          | クエストの名前                  | `ドラゴン討伐`                          |
| `type`          | クエストのタイプ                 | `KILL`, `COLLECT` など（QuestType参照） |
| `target`        | 対象のMob名・アイテム名など          | `ZOMBIE`、`DIAMOND`                |
| `amount`        | 必要な数                     | `10`                              |
| `timelimit`     | 制限時間（秒、0または未設定で無制限）      | `300`（5分）                         |
| `cooldownSeconds`     | クールダウン時間（秒）              | `600`（10分）                        |
| `maxUseCount`   | 最大挑戦回数（0または未設定で無制限）      | `1`（1回）                           |
| `rewards`       | クリア時に実行するコマンド(手動で追加推奨)   | `give %player% diamond 5`         |
| `startCommands`       | スタート時に実行するコマンド           | `give %player% stone_sword`         |
| `partyEnabled`  | パーティーでの共有有効化（true/false） | `true`                            |
| `partyMaxMembers`  | クエストに挑める最大パーティー人数        | `3`                               |
| `shareProgress` | パーティー内で進行状況を共有するか        | `true`                            |
| `shareCompletion`| パーティー内でクリア状態を共有するか       | `true`                            |
| `teleportWorld`| プレイヤーを指定したワールドに飛ばす       | `world`                           |
| `teleportX`| 指定したX座標に飛ばす              | `0`                               |
| `teleportY`| 指定したY座標に飛ばす              | `64`                              |
| `teleportZ`| 指定したZ座標に飛ばす              | `0`                               |
```yaml
quests:
    name: "ドラゴン討伐"
    type: "KILL"                 # クエストのタイプ
    target: "ENDER_DRAGON"       # 対象のMob名やアイテム名
    amount: 1                    # クエストをクリアするために必要な数
    timelimit: 1800              # クエストの制限時間(1800秒)
    cooldownSeconds: 3600        # 例3600秒経つごとに使えるようにする
    rewards:                     # クリア時に実行するコマンド一覧
      - "give %player% diamond 10" #プレイヤーにダイアモンドを10個渡す
      - "say %player% がドラゴン討伐クエストをクリアしました！" #プレイヤー全員にクエストクリアを告知する
      - "eco give %player% 1000" #プレイヤーにお金を1000円渡す
```
---

## 対応クエストタイプ一覧

- KILL (Mob討伐)
- COLLECT (アイテム収集)
- TRAVEL (場所訪問)
- MINE (ブロック採掘)
- PLACE (ブロック設置)
- BREAK (ブロック破壊)
- CRAFT (クラフト)
- SMELT (精錬)
- FISH (釣り)
- SLEEP (寝る)
- CHAT (チャット送信)
- COMMAND (コマンド実行)
- INTERACT (対話)
- DAMAGE_TAKEN (ダメージを受ける)
- DAMAGE_GIVEN (ダメージを与える)
- SHOOT (弓で攻撃)
- LEVEL (レベル到達)
- EXP_GAINED (経験値獲得)
- TIME_PLAYED (プレイ時間)
- TAME (動物を手懐ける)
- BREED (繁殖)
- TRADE (村人と取引)
- RIDE (乗り物に乗る)

---

## 開発・拡張

- 今後パーティー機能・クエストタイプ増加のさらなる拡充予定あり
- 要望やバグ報告はIssueにてお知らせください

---

## ライセンス

MIT License

---

## 補足

- `%player%` は報酬コマンド内でプレイヤー名に置換されます
- `quests.yml` は手動編集も可能です（フォーマットに注意してください）

---
