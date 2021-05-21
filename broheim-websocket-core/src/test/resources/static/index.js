var url = "ws://localhost:8000/ws";
var client = new WebSocketClient();
client.init(url);
client.connect();

function sendMessage() {
    if (client.enable) {
        client.send("{\"type\":\"ping\",\"body\":\"1212\"}")
    } else {
        alert("未连接不能发送")
    }
}