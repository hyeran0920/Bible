# 1. Python 3.9 기반 이미지 사용
FROM python:3.8.0

# 2. 컨테이너 내 작업 디렉토리 설정
WORKDIR /app

# 3. 필요 파일들을 컨테이너로 복사
COPY requirements.txt requirements.txt
COPY app.py app.py
COPY books.csv books.csv
COPY dataset_bible.pkl dataset_bible.pkl
COPY lightfm_model_bible.pkl lightfm_model_bible.pkl

# 4. 필요한 패키지 설치
RUN pip install --no-cache-dir -r requirements.txt

# 5. Flask 서버 실행
CMD ["python", "app.py"]
