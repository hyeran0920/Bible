from flask import Flask, jsonify, request
from pyngrok import ngrok
from flask_cors import CORS

# !ngrok authtoken '2gAz8iHMKHDtlgbxaand8ce6EAu_2pSR9fiiFYRnhBPUHT1Vz'
# ngrok config add-authtoken 2gAz8iHMKHDtlgbxaand8ce6EAu_2pSR9fiiFYRnhBPUHT1Vz
app = Flask(__name__)

# ngrok 터널 열기
ngrok.set_auth_token("2gAz8iHMKHDtlgbxaand8ce6EAu_2pSR9fiiFYRnhBPUHT1Vz") # ngrok Authtoken
public_url = ngrok.connect(5000)
print(f"🚀 Public URL: {public_url}")

@app.route("/")
def home():
    return "Flask API is running!"

@app.route("/recommend", methods=["POST"])
def recommend_post():
    data = request.get_json()
    user_id = data.get("user_id")
    n = data.get("n", 5)

    recommendations = [f"Book {i+1}" for i in range(n)]

    return jsonify({"user_id": user_id, "recommendations": recommendations})

@app.route("/recommend", methods=["GET"])
def recommend_get():
    user_id = request.args.get("user_id", type=int)
    n = request.args.get("n", default=5, type=int)

    recommendations = [f"Book {i+1}" for i in range(n)]  # 임시 결과
    return jsonify({"user_id": user_id, "recommendations": recommendations})

# Flask 서버 실행
if __name__ == "__main__":
    app.run()