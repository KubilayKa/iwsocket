{
function screenResize() {
   var height= $(document).height();
   var width= $(document).width();
    console.log("size " + height + " : "+width);
    $('#pictureHolder').css( {"width":width-20,"height":height-50})
    $('#player1').css( {"width":((width-50)/2),"height":( height-50)  })
    $('#player2').css( {"width":((width-50)/2),"height":( height-50) })
    $('#vs').css( {"width":( width/6),"height":( height/3),
        "left": (width/2) -(width/10),"top":(height/10)})  ;
    $('#table').css( {"width":( width/4),"height":( height/3),
        "left": (width/2) -(width/7),"top":(height/2)})  ;
    $('#vsImage').css( {"width":"100%","height":"100%"})
    $('.playerImg').css({"width":(width-50)/4,"height":(height/2),"marginLeft":(width-50)/9})
    var counter= $('.counter').FlipClock(200,{clockFace:'MinuteCounter',countdown:"true"});

}
function sendMessage(){
    var txt2snd=$("#messageText").val();
    var ws = new WebSocket("ws://localhost:8080/target/inbound");
    ws.onopen = function()
    {
        // Web Socket is connected, send data using send()
        ws.send(txt2snd);
    };
    ws.onmessage = function (evt)
    {
        var received_msg = evt.data;
        $("#messageLog").append(received_msg + "\n");
    };
    ws.onclose = function()
    {
        // websocket is closed.
    };


}}