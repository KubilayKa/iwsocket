{
    var height = $(document).height();
    var width = $(document).width();
    var counter;
    var counterL;
    var counterR;

    var url = location.href;
    var paramRaw = url.split(":)");
    if ("undefined" === paramRaw || paramRaw.length == 1) {
        paramRaw = ["", "main&main"]
        //paramRaw=["","userAgent=browserClient&first=main&second=main"];
    }
    //  var roomName=paramRaw[1].replace("userAgent=","").replace("first=","").replace("second=","");
    var paramsAndVal = paramRaw[1].split("&");
    var firstPlayer = paramsAndVal[0];
    var secondPlayer = paramsAndVal[1];

    var ws = new WebSocket("ws://localhost:8080/iwsocket/inbound/browserClient/" + firstPlayer + "/" + secondPlayer);
    ws.onopen = function () {
        // Web Socket is connected, send data using send()
        var url = location.href;
        ws.send("bc:" + url.split("?")[1]);
    };
    ws.onmessage = function (evt) {
        var received_msg = evt.data;
        console.log(received_msg + " recieved...")
        if ("inc" == received_msg) {
            counterL.increment();
            console.log(received_msg);
        } else if ("dec" == received_msg) {
            console.log("Dec");
            counterL.decrement();
            console.log(received_msg);
        } else if ("stats" == received_msg) {
            if ($('#leftDrawer').width() > 50) {

                $('#leftDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50)  }).animate({width: '0px', visibility: "hidden" }, "slow");
                $('.drawerName').css({"visibility": "hidden"});
                console.log("visible")
            } else {

                $('#leftDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50), "visibility": "visible"  }).animate({width: width / 3}, "slow")
                $('.drawerName').css({"visibility": "visible", "color": "white", "marginLeft": (width - 150) / 7});
                console.log("not visible")
            }
            console.log(received_msg);
        }else if(received_msg.indexOf("clist") > -1) {
            $('#clientsView').append(received_msg);
        }else{
            $("#messageLog").append(received_msg + "\n");
        }
    };
    ws.onclose = function () {
        // websocket is closed.
        console.log("før loc replace")
        window.location.replace("");
        console.log("after loc replace")
    };

    function screenResize() {

        $('#pictureHolder').css({"width": width - 20, "height": height - 50})
        $('#player1').css({"width": ((width - 50) / 2), "height": ( height - 50)  })
        $('#player2').css({"width": ((width - 50) / 2), "height": ( height - 50) })
        $('#vs').css({"width": ( width / 6), "height": ( height / 3),
            "left": (width / 2) - (width / 10), "top": (height / 10)});
        $('#table').css({"width": ( width ), "height": ( height / 3) });
        $('#vsImage').css({"width": "100%", "height": "100%"})
        $('.playerImg').css({"width": (width - 50) / 4, "height": (height / 2), "marginLeft": (width - 50) / 9})


        counter = $('.counter').FlipClock(000, {clockFace: 'MinuteCounter', countdown: "true", hideLabels: "true" });
        counterL = $('.counterL').FlipClock({clockFace: 'Counter' });
        counterR = $('.counterR').FlipClock(000, {clockFace: 'Counter' });
        var fCSize = $('.flip-clock-wrapper').width();
        var pad = ((width - 50) / 2) / 4;
        var centerPad = ((width - fCSize) / 2) + 20;

        console.log(pad)
        $('.counterL').css({"left": pad});
        $('.counterR').css({"left": pad});
        $('.counter').css({"left": centerPad})

        $('#counterTdC').css({"top": height - 200})
    }

    function getClientList() {
        ws.send("clients");
    }

    function clearClients() {
        ws.send("clear")
    }

    function sendMessage() {
        var txt2snd = $("#messageText").val();
        ws.send(txt2snd);
    }
}