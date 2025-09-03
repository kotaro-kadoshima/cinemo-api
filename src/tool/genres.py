import requests
import csv
import os
from datetime import datetime
import psycopg2

API_KEY = "0595c8cc1f021a2dfb763994043d9dcd"
BASE_URL = "https://api.themoviedb.org/3"

# PostgreSQL接続情報
DB_HOST = "ep-mute-truth-adb5eovo-pooler.c-2.us-east-1.aws.neon.tech"
DB_PORT = 5432
DB_NAME = "neondb"
DB_USER = "neondb_owner"
DB_PASSWORD = "npg_zD6ypOk8UXFm"

CSV_PATH = os.path.join(os.path.dirname(__file__), "output", "genres.csv")

def fetch_genres():
    # 日本語ジャンル一覧
    url_ja = f"{BASE_URL}/genre/movie/list"
    params_ja = {
        "api_key": API_KEY,
        "language": "ja-JP"
    }
    response_ja = requests.get(url_ja, params=params_ja)
    genres_ja = response_ja.json()["genres"]

    # 英語ジャンル一覧
    url_en = f"{BASE_URL}/genre/movie/list"
    params_en = {
        "api_key": API_KEY,
        "language": "en-US"
    }
    response_en = requests.get(url_en, params=params_en)
    genres_en = response_en.json()["genres"]
    # idをキーに英語名を紐付け
    en_dict = {g["id"]: g["name"] for g in genres_en}

    # 日本語ジャンルに英語名を追加
    for g in genres_ja:
        g["name_en"] = en_dict.get(g["id"], "")
    return genres_ja

def write_genres_csv(genres):
    now = datetime.now().isoformat()
    with open(CSV_PATH, "w", newline="", encoding="utf-8-sig") as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["genre_id", "name", "name_ja", "created_at"])
        for g in genres:
            writer.writerow([g["id"], g["name_en"], g["name"], now])

def insert_genres_to_db():
    conn = psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        dbname=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        sslmode="require"
    )
    cur = conn.cursor()
    with open(CSV_PATH, newline='', encoding='utf-8-sig') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            cur.execute("""
                INSERT INTO genres (
                    genre_id, name, name_ja, created_at
                ) VALUES (
                    %(genre_id)s, %(name)s, %(name_ja)s, %(created_at)s
                )
                ON CONFLICT (genre_id) DO NOTHING
            """, {
                "genre_id": row["genre_id"],
                "name": row["name"],
                "name_ja": row["name_ja"],
                "created_at": row["created_at"]
            })
    conn.commit()
    cur.close()
    conn.close()
    print("genresテーブルへの登録が完了しました。")

if __name__ == "__main__":
    genres = fetch_genres()
    write_genres_csv(genres)
    insert_genres_to_db()
