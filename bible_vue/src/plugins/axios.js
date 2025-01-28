import axios from "axios";

const instance = axios.create({
  baseURL: "http://localhost:8080", // API 서버 주소
  withCredentials: true, // 쿠키 허용
});

// 요청 인터셉터 (필요하면 추가)
instance.interceptors.request.use(
  (config) => {
    console.log("요청 전 처리:", config);
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터 (필요하면 추가)
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("응답 에러:", error.response || error.message);
    return Promise.reject(error);
  }
);

export default instance;
