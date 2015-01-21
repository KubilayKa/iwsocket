{  var height= $(document).height();
    var width= $(document).width();
    var counter;
    var counterL;
    var counterR;
    var ws = new WebSocket("ws://localhost:8080/iwsocket/inbound");
    ws.onopen = function()
    {
        // Web Socket is connected, send data using send()
        ws.send("Browser says halooo");
    };
    ws.onmessage = function (evt)
    {


        var received_msg = evt.data;
        if("inc" == received_msg) {
            counterL.increment();
            console.log(received_msg);
        }else if("dec" == received_msg) {
            console.log("Dec");
            counterL.decrement();
            console.log(received_msg);
        }else if("stats" == received_msg){
            if($('#leftDrawer').width() > 10){

                $('#leftDrawer').css({"width":((width-50)/2),"height":( height-50)  }).animate({width:'0px',visibility:"hidden" })
                console.log("visible")
            }else {

                $('#leftDrawer').css({"width":((width-50)/2),"height":( height-50),"visibility":"visible"  }).animate({width:width/2})
                console.log("not visible")
            }
            console.log(received_msg);
        }
        $("#messageLog").append(received_msg + "\n");
    };
    ws.onclose = function()
    {
        // websocket is closed.
    };

    function screenResize() {

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
    ws.send(txt2snd);


}}