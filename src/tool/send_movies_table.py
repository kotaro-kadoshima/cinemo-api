import psycopg2
import csv
import os
from datetime import datetime

# PostgreSQL接続情報
DB_URL = "jdbc:postgresql://ep-mute-truth-adb5eovo-pooler.c-2.us-east-1.aws.neon.tech/neondb?"
DB_HOST = "ep-mute-truth-adb5eovo-pooler.c-2.us-east-1.aws.neon.tech"
DB_PORT = 5432
DB_NAME = "neondb"
DB_USER = "neondb_owner"
DB_PASSWORD = "npg_zD6ypOk8UXFm"

# CSVファイルパス（tmdb_api.pyで出力されたものを想定）
CSV_PATH = os.path.join(os.path.dirname(__file__), "output", "movies_selected.csv")

def insert_movies():
    # PostgreSQLへ接続
    conn = psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        dbname=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        sslmode="require"
    )
    cur = conn.cursor()

    # CSV読み込み（movie_id重複除外）
    unique_rows = {}
    with open(CSV_PATH, newline='', encoding='utf-8-sig') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            unique_rows[row["movie_id"]] = row  # movie_idごとに上書き（最後の1件が残る）

    for row in unique_rows.values():
        now = datetime.now()
        cur.execute("""
            INSERT INTO movies (
                movie_id, title, original_title, duration, release_date,
                poster_url, rating, overview, adult, original_language,
                emotion_status, created_at, updated_at
            ) VALUES (
                %(movie_id)s, %(title)s, %(original_title)s, %(duration)s, %(release_date)s,
                %(poster_url)s, %(rating)s, %(overview)s, %(adult)s, %(original_language)s,
                %(emotion_status)s, %(created_at)s, %(updated_at)s
            )
        """, {
            "movie_id": row["movie_id"],
            "title": row["title"],
            "original_title": row["original_title"],
            "duration": row["duration"],
            "release_date": row["release_date"],
            "poster_url": row["poster_url"],
            "rating": row["rating"],
            "overview": row["overview"],
            "adult": row["adult"],
            "original_language": row["original_language"],
            "emotion_status": "処理前",
            "created_at": now,
            "updated_at": now
        })
    conn.commit()
    cur.close()
    conn.close()
    print("moviesテーブルへの登録が完了しました。")

if __name__ == "__main__":
    insert_movies()