import requests
import csv
import os
import datetime

"""
TMDB APIを利用した映画情報取得ツール
"""

print(f"処理を開始：{datetime.datetime.now()}")

# APIキーマスキング中（実際に実行する際はTMDBより取得）
API_KEY = "xxxxx"
BASE_URL = "https://api.themoviedb.org/3"

def get_popular_movies():
    url = f"{BASE_URL}/movie/popular"
    params = {
        "api_key": API_KEY,
        "language": "ja-JP",
        "page": 1
    }
    response = requests.get(url, params=params)
    data = response.json()
    return data["results"]

if __name__ == "__main__":
    movies = get_popular_movies()

    # スクリプトと同じ場所にある output フォルダを指定
    output_dir = os.path.join(os.path.dirname(__file__), "output")
    # フォルダが無ければ作成
    os.makedirs(output_dir, exist_ok=True)

    output_path = os.path.join(output_dir, "movies.csv")

    # CSVファイルに出力
    with open(output_path, "w", newline="", encoding="utf-8-sig") as csvfile:
        writer = csv.writer(csvfile)
        # ヘッダー行
        writer.writerow(["タイトル", "上映開始日", "評価"])
        
        # データ行
        for movie in movies:
            writer.writerow([movie["title"], movie["release_date"], movie["vote_average"]])

print(f"処理を終了：{datetime.datetime.now()}")
