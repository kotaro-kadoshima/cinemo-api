import requests
import psycopg2
import os
import datetime

API_KEY = "XXX" # TMDBのAPIキー
BASE_URL = "https://api.themoviedb.org/3"
IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

# PostgreSQL接続情報
DB_HOST = "ep-mute-truth-adb5eovo-pooler.c-2.us-east-1.aws.neon.tech"
DB_PORT = 5432
DB_NAME = "neondb"
DB_USER = "neondb_owner"
DB_PASSWORD = "npg_zD6ypOk8UXFm"

# 最終取得ページ番号を保存するファイル
LAST_PAGE_PATH = os.path.join(os.path.dirname(__file__), "output", "last_page.txt")

def get_popular_movies(page):
    url = f"{BASE_URL}/movie/popular"
    params = {
        "api_key": API_KEY,
        "language": "ja-JP",
        "page": page,
        "include_adult": False
    }
    response = requests.get(url, params=params)
    data = response.json()
    return data.get("results", [])

def get_movie_runtime(movie_id):
    url = f"{BASE_URL}/movie/{movie_id}"
    params = {
        "api_key": API_KEY,
        "language": "ja-JP"
    }
    response = requests.get(url, params=params)
    data = response.json()
    return data.get("runtime")  # 分単位

def insert_movie_to_db(cur, movie):
    now = datetime.datetime.now()
    poster_url = ""
    if movie.get("poster_path"):
        poster_url = f"{IMAGE_BASE_URL}{movie['poster_path']}"
    duration = get_movie_runtime(movie["id"])
    release_date = movie.get("release_date")
    if not release_date:
        release_date = None
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
        ON CONFLICT (movie_id) DO NOTHING
    """, {
        "movie_id": movie.get("id"),
        "title": movie.get("title"),
        "original_title": movie.get("original_title"),
        "duration": duration,
        "release_date": release_date,
        "poster_url": poster_url,
        "rating": movie.get("vote_average"),
        "overview": movie.get("overview"),
        "adult": bool(movie.get("adult", False)),
        "original_language": movie.get("original_language"),
        "emotion_status": "処理前",
        "created_at": now,
        "updated_at": now
    })

if __name__ == "__main__":
    print(f"処理を開始：{datetime.datetime.now()}")

    # 前回の最終ページを取得
    if os.path.exists(LAST_PAGE_PATH):
        with open(LAST_PAGE_PATH, "r") as f:
            start_page = int(f.read().strip()) + 1
    else:
        start_page = 1

    conn = psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        dbname=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        sslmode="require"
    )
    cur = conn.cursor()

    inserted = 0
    page = start_page
    max_count = 5000
    while inserted < max_count:
        page_movies = get_popular_movies(page)
        if not page_movies:
            break
        for movie in page_movies:
            # 概要が空、またはadult=Trueは除外
            if not movie.get("overview") or movie.get("adult", False):
                continue
            insert_movie_to_db(cur, movie)
            inserted += 1
            if inserted >= max_count:
                break
        print(f"{inserted}件登録完了（{page}ページ目まで）")
        page += 1

    # 最終ページを保存
    os.makedirs(os.path.dirname(LAST_PAGE_PATH), exist_ok=True)
    with open(LAST_PAGE_PATH, "w") as f:
        f.write(str(page - 1))

    conn.commit()
    cur.close()
    conn.close()
    print(f"処理を終了：{datetime.datetime.now()}")