import axios from "axios";
import store from "../store";
import router from "../router";

const instance = axios.create({
  baseURL: "http://localhost:8080/api", // API 서버 주소
  // withCredentials: true, // 쿠키 허용
});

// 요청 인터셉터 (필요하면 추가)
instance.interceptors.request.use(
  (config) => {
    const token = store.state.token;
    if (token) {
      config.headers.Authorization = token;  // 또는 `Bearer ${token}`
    }
    // console.log("요청 전 처리:", config);
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터 (필요하면 추가)
instance.interceptors.response.use(
  async (response) => response,
  async (error) => {
    let token = store.state.token;
    let redirect = false;

    console.log(error.response.data.message);
    if(error.response.status === 401 && error.response.data.message.includes("유효")) {
      redirect = true;
      store.commit('setToken', null);
    }
    if(error.response.status === 401 && error.response.data.message.includes("로그인")) {
      redirect = true;
      store.commit('setToken', null);
    }

    if(redirect) { // access token이 없는 경우
      try {
        await generateByRefreshToken(); // 토큰 재발급
        return instance(error.config); // 원래 요청 재시도
      } catch (refreshError) {
        // alert(refreshError);
        console.log("refreshError: ", refreshError);
        return Promise.reject(refreshError);
      }
    } else {
      const errorMessage = await error.response?.data?.message || "에러 발생";
      console.log("errorMessage: ", errorMessage);
      // alert(errorMessage);
    }

    return Promise.reject(error);
  }
);

// 토큰 재발급
const generateByRefreshToken = async () => {
  console.log('토큰 재발급 동작');
  try {
    const response = await instance.post('/auth/refresh', null, {
      withCredentials: true,
    });
    // store에 접근하기 위해서는 store를 import 해야 합니다
    store.commit('setToken', response.headers['authorization']);
    return response;
  } catch (error) {
    console.error("토큰 재발급 실패:", error);
    // 재발급 실패시 로그아웃 처리
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("isAdmin");
    router.push("/");
    return Promise.reject(error);
  }
}

export default instance;
