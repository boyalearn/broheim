function WebSocketClient() {
    this.enable = false;
    this.ws = null;
    this.url = null;
    this.connectionTimer = null;
    this.heartInterval = 2000;
    this.reconnectInterval = 2000;
    this.connectListener = function () {
        console.log("connection start work")
    }

    this.closeListener = function () {
        console.log("date of connection stop work")
    }

    this.heartFunction = function () {
        var _this = this;
        _this.connectionTimer = setInterval(function () {
            _this.send("{\"command\":\"ping\"}")
        }, _this.heartInterval)
    }

    this.init = function (url) {
        this.url = url;
    }
    this.connect = function () {
        var _this = this;
        if (!"WebSocket" in window) {
            console.log("您的浏览器不支持WebSocket!");
        }

        this.ws = new WebSocket(_this.url);

        this.ws.onopen = function () {
            _this.enable = true;
            console.log("connect success");
            _this.connectListener();
            _this.heartFunction()
        };

        this.ws.onmessage = function (evt) {
            var message = eval('(' + evt.data + ')');
            console.log(message)
            if ("ping" == message.command) {
                _this.send("{\"command\":\"pong\"}")
            }
        };

        this.ws.onerror = function (evt) {
            console.log(evt)
        }

        this.ws.onclose = function () {
            if (_this.enable) {
                _this.enable = false;
                console.log("onclose")
                clearInterval(_this.connectionTimer)
                _this.closeListener();
            }
            _this.reconnect(_this);
        };
    }
    this.send = function (msg) {
        this.ws.send(msg);
    }
    this.reconnect = function (client) {
        console.log("re connect...")
        setTimeout(function () {
            client.connect();
        }, client.reconnectInterval)
    }
}