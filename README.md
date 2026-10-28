# Rail Architect

Rail Architect は、Java と独自の Canvas 描画処理で開発している、Android 向け鉄道建設ゲームの初期プロトタイプです。

## 現在の実装

- 横画面・全画面でのゲーム表示
- マス目に沿ったタイル配置：消去、直線、縦線、カーブ、分岐器、駅、信号
- シンプルな列車の移動シミュレーション
- 実行、一時停止、リセット、全消去の操作
- GitHub Actions によるデバッグ APK の自動ビルド

現在は初期プロトタイプです。線路の接続判定、分岐器による進路制御、セーブ・ロード、複数列車、時刻表、列車同士の衝突防止などは、まだ実装されていません。

## GitHub Actions で APK をビルドする

次の場合に自動でビルドが実行されます。

- `main` または `master` ブランチへのコードのプッシュ
- プルリクエストの作成・更新
- GitHub Actions からの手動実行

ビルド済み APK は、次の手順でダウンロードできます。

1. リポジトリの **Actions** タブを開きます。
2. **Build Rail Architect APK** を選択します。
3. 最新の実行結果を開きます。
4. **Artifacts** セクションから **RailArchitect-debug-apk** をダウンロードします。
5. ダウンロードした ZIP ファイルを展開すると、`app-debug.apk` が入っています。

デバッグ APK のビルドに署名用の Secrets を設定する必要はありません。Android のデバッグ用キーで署名されます。これは動作確認用であり、Google Play で公開するためのリリース APK ではありません。

## ローカル環境でビルドする

必要な環境：

- JDK 17
- Android SDK Platform 35
- Android SDK Build-Tools 35.0.0
- Gradle 8.9

プロジェクトのルートディレクトリで、次のコマンドを実行します。

```bash
gradle --no-daemon assembleDebug
```

生成される APK の場所：

```text
app/build/outputs/apk/debug/app-debug.apk
```
