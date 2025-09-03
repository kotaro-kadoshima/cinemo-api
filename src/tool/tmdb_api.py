import requests
import csv
import os
import datetime

"""
TMDB APIを利用した映画情報取得ツール
"""

print(f"処理を開始：{datetime.datetime.now()}")

# APIキーマスキング中（実際に実行する際はTMDBより取得）
API_KEY = "0595c8cc1f021a2dfb763994043d9dcd"
BASE_URL = "https://api.themoviedb.org/3"
IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

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
    return data["results"]

# def get_genre_master():
#     url = f"{BASE_URL}/genre/movie/list"
#     params = {
#         "api_key": API_KEY,
#         "language": "ja-JP"
#     }
#     response = requests.get(url, params=params)
#     genres = response.json()["genres"]
#     # {id: {"name": name, "name_ja": name_ja}} の辞書を作成
#     return {g["id"]: {"name": g["name"], "name_ja": g["name"]} for g in genres}

def get_movie_runtime(movie_id):
    url = f"{BASE_URL}/movie/{movie_id}"
    params = {
        "api_key": API_KEY,
        "language": "ja-JP"
    }
    response = requests.get(url, params=params)
    data = response.json()
    return data.get("runtime")  # 分単位

def get_watch_providers(movie_id):
    url = f"{BASE_URL}/movie/{movie_id}/watch/providers"
    params = {
        "api_key": API_KEY
    }
    response = requests.get(url, params=params)
    data = response.json()
    jp = data.get("results", {}).get("JP", {})
    providers = jp.get("flatrate", [])
    return [p["provider_name"] for p in providers]

if __name__ == "__main__":
    movies = []
    for page in range(1, 101):
        page_movies = get_popular_movies(page)
        # overviewが空でないものだけ追加
        movies.extend([m for m in page_movies if m.get("overview")])

        # # overviewが空でない & movie_idが1484454以上のみ追加
        # movies.extend([m for m in page_movies if m.get("overview") and m.get("id", 0) >= 1484454])

        print(f"ページ {page} の処理が完了しました。")

    # genre_master = get_genre_master()
    movies_sorted = sorted(movies, key=lambda m: m["id"])

    output_dir = os.path.join(os.path.dirname(__file__), "output")
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, "movies.csv")

    with open(output_path, "w", newline="", encoding="utf-8-sig") as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow([
            "movie_id",      # 映画ID
            "title",         # 日本語タイトル
            "original_title",# 原題
            "duration",      # 上映時間（分）
            "release_date",  # 公開日
            "poster_url",    # ポスター画像URL
            "rating",        # 評価
            "overview",      # 概要
            "adult",         # R指定フラグ（boolean値）
            "original_language" # 原語
        ])
        for movie in movies_sorted[:1000]:
            poster_url = ""
            if movie.get("poster_path"):
                poster_url = f"{IMAGE_BASE_URL}{movie['poster_path']}"
            duration = get_movie_runtime(movie["id"])
            rating = movie.get("vote_average")
            overview = movie.get("overview")
            release_date = movie.get("release_date")
            adult_flag = bool(movie.get("adult", False))
            original_language = movie.get("original_language")
            writer.writerow([
                movie.get("id"),
                movie.get("title"),
                movie.get("original_title"),
                duration,
                release_date,
                poster_url,
                rating,
                overview,
                adult_flag,
                original_language
            ])

print(f"処理を終了：{datetime.datetime.now()}")
