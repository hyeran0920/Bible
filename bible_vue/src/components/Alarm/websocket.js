import Cookies from "js-cookie"; // js-cookie 라이브러리 필요

class WebSocketService {
  constructor() {
    this.socket = null;
    this.clearCallback=null;
  }

  setClearCallback(callback){
    this.clearCallback=callback;
  }

  connect(url) {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      // 쿠키에서 authorization 토큰 가져오기 (없어도 진행)
      const memId = Cookies.get("memId");

      // WebSocket URL에 토큰 추가 (없을 경우 토큰 제외)
      const wsUrl = memId ? `${url}?memId=${encodeURIComponent(memId)}` : url;

      console.log("WebSocket 연결 URL:", wsUrl);
      this.socket = new WebSocket(wsUrl);

      this.socket.onopen = () => {
        console.log("✅ WebSocket 연결 성공!");
      };

      this.socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log("수신된 메시지:", data);
      };

      this.socket.onerror = (error) => {
        console.error("WebSocket 에러:", error);
      };

      this.socket.onclose = () => {
        console.log("WebSocket 연결 종료");
      };
    }
  }

  send(message) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      console.log("메시지 전송:", message);
      this.socket.send(JSON.stringify(message));
    } else {
      console.error("WebSocket이 열려 있지 않습니다.");
    }
  }

  disconnect() {
    if (this.socket) {
        if (this.socket.readyState === WebSocket.OPEN) {
            console.log("WebSocket 연결 종료 요청...");
            this.socket.close();
        } else {
            console.log("WebSocket이 이미 닫혀 있습니다.");
        }

        if(this.clearCallback){
          this.clearCallback();
        }
        this.socket = null; // WebSocket 객체 초기화
    }
  }

}

const webSocketService = new WebSocketService();
export default webSocketService;
