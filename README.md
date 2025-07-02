# QuestPlugin

Minecraftサーバー用のクエスト管理プラグインです。  
プレイヤーは討伐、収集、設置など多様なタイプのクエストを受けられます。

---

## 機能

- 討伐、収集、探索、採掘、設置など多種多様なクエストタイプ対応
- 時間制限付きクエストの実装
- クエスト進行状況のBossBar表示
- クエストクリアでコマンド実行による報酬付与
- コマンドベースでのクエスト作成・編集・開始・一覧表示

---

## インストール

1. このプラグインのjarファイルを`plugins`フォルダに入れる
2. サーバーを起動し、`plugins/QuestPlugin`フォルダが生成されるのを確認
3. `quests.yml`でクエスト設定を管理

---

## コマンド一覧
コマンドは新しく追加される場合があります

| コマンド                                 | 説明              | 権限                |
|--------------------------------------|-----------------|-------------------|
| `/quest config create <ID>`          | 新しいクエストを作成する    | `quest.create`    |
| `/quest config set <ID> <key> <value>` | クエストの設定項目を変更する  | `quest.set`       |
| `/quest config addreward <ID> <コマンド>` | クエスト報酬コマンドを追加(コマンド使用不可)   | `quest.addreward` |
| `/quest config save`                 | すべてのクエスト設定を保存する | `quest.save`      |
| `/quest leave`                       | クエストから離脱する      | `quest.leave`     |
| `/quest start <ID>`                  | クエストを開始する       | `quest.start`     |
| `/quest list`                        | 利用可能なクエストの一覧を表示 | `quest.use`       |
| `/quest reload`                      | プラグイン設定をリロード    | `quest.reload`    |

---

## クエスト設定項目

| key          | 説明                        | 例                       |
|--------------|-----------------------------|--------------------------|
| `name`       | クエストの名前               | `ドラゴン討伐`            |
| `type`       | クエストのタイプ             | `KILL`, `COLLECT`など（詳細はQuestType参照）|
| `target`     | 対象のMob名・アイテム名など | `ZOMBIE`、`DIAMOND`       |
| `amount`     | 必要な数                   | `10`                     |
| `timelimit`  | 制限時間（秒、0で無制限）    | `300`（5分）              |
| `cooldown`   | クールダウン    | `600`（10分）              |
| `maxAttempts`   | 最大挑戦回数 0または指定なしで無制限）   | `1`（1回）              |
| `rewards`    | クリア時に実行するコマンド   | `give %player% diamond 5` |

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

- 今後パーティ機能などの拡張予定あり
- ご要望やバグ報告はIssueにて

---

## ライセンス

MIT License

---

### 補足

- `%player%`は報酬コマンド内でプレイヤー名に置き換わります
- `quests.yml`は手動で編集も可能です

---