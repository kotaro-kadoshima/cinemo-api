# Cinemo API

Spring AIを使用したチャットAPIアプリケーション

## セットアップ

### 1. 環境変数の設定

`.env.template`をコピーして`.env`ファイルを作成し、必要な値を設定してください。

```bash
cp .env.template .env
```

.envファイルを編集：

```properties
# Database (Neon PostgreSQL)
DATABASE_URL=jdbc:postgresql://your-neon-host/your-db?sslmode=require&channelBinding=require
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
# Ollama
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=gemma3:4b
OLLAMA_TEMPERATURE=0.7
# Server
SERVER_PORT=8080
CONTEXT_PATH=/cinemo
```

### 2. 前提条件

- Java 21以上
- Maven 3.6+
- **Spring Boot 5.5**
- ~~Docker & Docker Compose~~（未対応 - 今後対応予定）
- Ollama（ローカル実行の場合）
- ~~Gemini~~（未対応 - 今後対応予定）
- PostgreSQL

### 3. Ollamaのセットアップ（ローカル実行）

```bash
# Ollamaをインストール
brew install ollama
# または https://ollama.ai/download

# Ollamaサービス開始
ollama serve

# モデルをダウンロード
ollama pull gemma3:4b

# モデルの実行
ollama run gemma3:4b
```

## ⚠️ セキュリティ注意事項

- `.env`ファイルは絶対にGitにコミットしないでください
- 本番環境では環境変数を直接設定することを推奨します
- データベースパスワードなど機密情報の取り扱いに注意してください
