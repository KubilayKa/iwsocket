var ws = new WebSocket("ws://localhost:8080/iwsocket/minbound");
ws.onopen = function () {
    // Web Socket is connected, send data using send()

};
ws.onmessage = function (evt) {
  var xyz=evt.data.split(":");

    console.log(xyz);
    console.log($("#energyBall").offset());
};
ws.onclose = function () {
    // websocket is closed.

};
