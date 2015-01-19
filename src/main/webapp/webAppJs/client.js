{
    var counter;
    var counterL;
    var counterR;
function screenResize() {
   var height= $(document).height();
   var width= $(document).width();
    console.log("size " + height + " : "+width);
    $('#pictureHolder').css( {"width":width-20,"height":height-50})
    $('#player1').css( {"width":((width-50)/2),"height":( height-50)  })
    $('#player2').css( {"width":((width-50)/2),"height":( height-50) })
    $('#vs').css( {"width":( width/6),"height":( height/3),
        "left": (width/2) -(width/10),"top":(height/10)})  ;
    $('#table').css( {"width":( width ),"height":( height/3) })  ;
    $('#vsImage').css( {"width":"100%","height":"100%"})
    $('.playerImg').css({"width":(width-50)/4,"height":(height/2),"marginLeft":(width-50)/9})
    $('#counterTdL').css({"width":width/3 });
    $('#counterTdC').css({"width":width/3});
    $('#counterTdR').css({"width":width/3 });
      counter= $('.counter').FlipClock(000,{clockFace:'MinuteCounter',countdown:"true"});

  counterL= $('.counterL').FlipClock({clockFace:'Counter' });
     counterR= $('.counterR').FlipClock(000,{clockFace:'Counter' });


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
        counterL.increment();
        var received_msg = evt.data;
        $("#messageLog").append(received_msg + "\n");
    };
    ws.onclose = function()
    {
        // websocket is closed.
    };


}}