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

| コマンド                                 | 説明                          | 権限                |
|--------------------------------------|-----------------------------|-------------------|
| `/quest config create <ID>`          | 新しいクエストを作成する          | `quest.create`    |
| `/quest config set <ID> <key> <value>` | クエストの設定項目を変更する        | `quest.set`       |
| `/quest config addreward <ID> <コマンド>` | クエスト報酬コマンドを追加(コマンド内置換 `%player%`) | `quest.addreward` |
| `/quest config save`                 | すべてのクエスト設定を保存する       | `quest.save`      |
| `/quest leave`                       | クエストから離脱する                | `quest.leave`     |
| `/quest start <ID>`                  | クエストを開始する                 | `quest.start`     |
| `/quest list`                        | 利用可能なクエストの一覧を表示        | `quest.use`       |
| `/quest reload`                      | プラグイン設定をリロード            | `quest.reload`    |
| `/quest party`                      | 自分のパーティー情報を表示            | `quest.party`     |
| `/quest party create`               | パーティーを作成する                | `quest.party`     |
| `/quest party invite <player>`      | プレイヤーをパーティーに招待          | `quest.party`     |
| `/quest party join <player>`        | 指定プレイヤーのパーティーに参加       | `quest.party`     |
| `/quest party leave`                | パーティーを離脱する                | `quest.party`     |
| `/quest party disband`              | パーティーを解散する（リーダーのみ）     | `quest.party`     |
| `/quest party kick <player>`         | パーティーからプレイヤーを追放         | `quest.party`     |

---

## クエスト設定項目

| key             | 説明                          | 例                           |
|-----------------|-------------------------------|------------------------------|
| `name`          | クエストの名前                 | `ドラゴン討伐`                 |
| `type`          | クエストのタイプ               | `KILL`, `COLLECT` など（QuestType参照）|
| `target`        | 対象のMob名・アイテム名など     | `ZOMBIE`、`DIAMOND`           |
| `amount`        | 必要な数                     | `10`                         |
| `timelimit`     | 制限時間（秒、0または未設定で無制限） | `300`（5分）                  |
| `cooldownSeconds`      | クールダウン時間（秒）           | `600`（10分）                 |
| `maxUseCount`   | 最大挑戦回数（0または未設定で無制限）| `1`（1回）                   |
| `rewards`       | クリア時に実行するコマンド一覧    | `give %player% diamond 5`     |
| `partyEnabled`  | パーティーでの共有有効化（true/false）| `true`                      |
| `shareProgress` | パーティー内で進行状況を共有するか | `true`                      |
| `shareCompletion`| パーティー内でクリア状態を共有するか| `true`                      |

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

- 今後パーティー機能のさらなる拡充予定あり
- 要望やバグ報告はIssueにてお知らせください

---

## ライセンス

MIT License

---

## 補足

- `%player%` は報酬コマンド内でプレイヤー名に置換されます
- `quests.yml` は手動編集も可能です（フォーマットに注意してください）

---
