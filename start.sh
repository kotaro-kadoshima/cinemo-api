#!/bin/bash
set -e

# Google Cloud認証情報ファイル作成
if [ -n "$GOOGLE_APPLICATION_CREDENTIALS_JSON" ]; then
    echo "Setting up Google Cloud credentials..."
    echo "$GOOGLE_APPLICATION_CREDENTIALS_JSON" > "$GOOGLE_APPLICATION_CREDENTIALS"
    echo "Google Cloud credentials file created at $GOOGLE_APPLICATION_CREDENTIALS"
fi

# アプリケーション起動
echo "Starting application..."
exec java -jar /app/app.jar