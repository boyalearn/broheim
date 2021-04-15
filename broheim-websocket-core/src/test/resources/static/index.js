var url = "ws://localhost:8080/ws";
var client = new WebSocketClient();
client.init(url);
client.connect();

function sendMessage() {
    if (client.enable) {
        client.send("{\"command\":\"ping\"}")
    } else {
        alert("未连接不能发送")
    }
}