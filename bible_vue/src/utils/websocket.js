class WebSocketService {
  constructor() {
    this.socket = null;
  }

  connect(url) {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      this.socket = new WebSocket(url);

      this.socket.onopen = () => {
        console.log("WebSocket 연결 성공!");
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
      console.log("send message");
      this.socket.send(JSON.stringify(message));
    } else {
      console.error("WebSocket이 열려 있지 않습니다.");
    }
  }

  disconnect() {
    if (this.socket) {
      this.socket.close();
    }
  }
}
  
const webSocketService = new WebSocketService();
export default webSocketService;
  