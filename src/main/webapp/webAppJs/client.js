{
function screenResize() {
   var height= $(document).height();
   var width= $(document).width();
    console.log("size " + height + " : "+width);
    $('#pictureHolder').css( {"width":width-20,"height":height-50})
    $('#player1').css( {"width":((width-50)/2),"height":( height-50)  })
    $('#player2').css( {"width":((width-50)/2),"height":( height-50) })
    $('#vs').css( {"width":( width/6),"height":( height/3),"left": (width/2) -(width/10),"top":50,"backgroundImage":"url(img/vs1.png)","backgroundSize":"100%"})

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