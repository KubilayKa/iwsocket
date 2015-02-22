var ofset = $("#energyBall").offset();
var ws = new WebSocket("ws://localhost:8080/iwsocket/minbound");

ws.onopen = function () {
    // Web Socket is connected, send data using send()

};
ws.onmessage = function (evt) {
  var xyz=evt.data.split(":");

    console.log(xyz);
    var oleft=ofset.left ;
    var otop=ofset.top  ;
    if(xyz[0]<2&&-2>xyz[0]){
        $("#energyBall").animate({left:oleft,top:otop},"fast")
    }else {
        if(xyz[0]>2 || xyz[0] < 400) {
            $("#energyBall").animate({left:oleft-xyz[0] ,top:otop},"fast")
        }else if(xyz[0]< -2 || xyz[0] > -450) {
            $("#energyBall").animate({left:oleft+xyz[0] ,top:otop},"fast")
        }
    }
    console.log($("#energyBall").offset());
};
ws.onclose = function () {
    // websocket is closed.

};
