import axios from "axios";

const BASE_URL = "http://localhost:8080/flask";

export const getRecommendations = async (memId, n = 5) => {
  try {
    const response = await axios.get(`${BASE_URL}/recommend`, {
      params: { mem_id: memId, n },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching recommendations:", error);
    return { error: "추천을 가져오는 중 오류 발생" };
  }
};
