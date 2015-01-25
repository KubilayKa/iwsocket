
{  var height= $(document).height();
    var width= $(document).width();
    var counter;
    var counterL;
    var counterR;

    var url =location.href;
    var paramRaw=url.split("?");
    if ("undefined"===paramRaw|| paramRaw.length==1){
        paramRaw=["","userAgent=main&room=main"];
    }
    var roomName=paramRaw[1].replace("userAgent=","").replace("room=","");
    var paramsAndVal=roomName.split("&");
    var firstPlayer=paramsAndVal[0];
    var secondPlayer= paramsAndVal[1];

    var ws = new WebSocket("ws://localhost:8080/iwsocket/inbound/{bc}/{ikkebc}");


    ws.onopen = function()
    {
        // Web Socket is connected, send data using send()
        var url=location.href;

        ws.send("bc:"+url.split("?")[1]);

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
            console.log("sibu")
            if($('#leftDrawer').width() > 50){

                $('#leftDrawer').css({"width":((width-150)/3),"height":( height-50)  }).animate({width:'0px',visibility:"hidden" },"slow");
                $('.drawerName').css({"visibility":"hidden"});
                console.log("visible")
            }else {

                $('#leftDrawer').css({"width":((width-150)/3),"height":( height-50),"visibility":"visible"  }).animate({width:width/3},"slow")
                $('.drawerName').css({"visibility":"visible","color":"white","marginLeft":(width-150)/7});
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