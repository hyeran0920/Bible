import pickle
import os
#import requests
#import google.auth

from flask import Flask, jsonify, request
from pyngrok import ngrok
from flask_cors import CORS

import pandas as pd

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})

#저장 모드 load
PATH = "C:/dev/metanet/workspace/meta3/Bible/bible_flask/" #"/app"
MODEL_PATH = os.path.join(PATH, "lightfm_model_bible.pkl")
DATASET_PATH = os.path.join(PATH, "dataset_bible.pkl")
BOOKS_CSV_PATH = os.path.join(PATH, "books.csv")
try:
    with open(MODEL_PATH, "rb") as f:
        model = pickle.load(f)

    with open(DATASET_PATH, "rb") as f:
        dataset = pickle.load(f)

    print("모델과 데이터셋 로드 성공!")
except Exception as e:
    print(f"모델 로드 실패: {e}")
    model = None
    dataset = None
books_df = pd.read_csv(BOOKS_CSV_PATH, dtype=str)

@app.route("/")
def home():
    return "Flask API is running!"
def recommend_books(model, dataset, mem_id , n=5):
    print(f"recommend_books() 실행: mem_id={mem_id}, n={n}")

    mem_index = dataset.mapping()[0].get(mem_id , None)
    if mem_index is None:
        print(f"⚠️ 사용자 {mem_id}를 찾을 수 없습니다.")
        return [{"error": f"사용자 {mem_id}를 찾을 수 없습니다."}]

    all_items = list(dataset.mapping()[2].keys())
    isbn_to_book_id = dataset.mapping()[2]
    scores = model.predict(mem_index, list(range(len(all_items))))
    top_books = sorted(zip(all_items, scores), key=lambda x: x[1], reverse=True)[:n]

    recommendations = []
    for isbn, score in top_books:
        book_entry = books_df[books_df["ISBN"] == isbn]

        if not book_entry.empty:
            book_title = book_entry.iloc[0]["Book-Title"]
            image_url = book_entry.iloc[0]["Image-URL-L"]
        else:
            book_title = "Unknown Title"
            image_url = ""

        recommendations.append({
            "book_id": isbn_to_book_id.get(isbn, "Unknown ID"),  # KeyError 방지
            "isbn": isbn,
            "title": book_title,
            "image_url": image_url,
            "score": float(score)  # JSON 직렬화 오류 방지
        })

    return recommendations


@app.route("/recommend", methods=["GET"])
def recommend_get():
    mem_id  = request.args.get("mem_id", type=int)
    n = request.args.get("n", default=5, type=int)
    print(f"📢 recommend_books() 실행: mem_id={mem_id}, n={n}")  
    if not mem_id:
        return jsonify({"error": "mem_id 필요합니다."}), 400

    recommendations = recommend_books(model, dataset, mem_id, n)
    return jsonify({"mem_id": mem_id, "recommendations": recommendations})

# 추천 도서 업데이트 (PUT)
@app.route("/recommend/update", methods=['PUT'])
def recommend_update():
    mem_id = request.args.get("mem_id", type=int)
    n = request.args.get("n", default=5, type=int)
    print(f"📢 recommend_books_update() 실행: mem_id={mem_id}, n={n}")
    if not mem_id:
        return jsonify({"error": "mem_id가 필요합니다."}), 400

    # 예: 1~20권 중 11~20만 반환
    recommendations_all = recommend_books(model, dataset, mem_id, n)
    offset = 10
    new_list = recommendations_all[offset:]  # 11~20
    return jsonify({"recommendations": new_list})

# 추천 도서 삭제 (DELETE)
@app.route('/recommend', methods=['DELETE'])
def recommend_delete():
    mem_id = request.args.get("mem_id")

    if not mem_id:
        return jsonify({"error": "mem_id가 필요합니다."}), 400

    return jsonify({"message": f"회원 {mem_id}의 추천 도서 삭제 완료"}), 200

# Flask 서버 실행
if __name__ == "__main__":

    app.run(host="0.0.0.0", port=5000)
    #app.run()
