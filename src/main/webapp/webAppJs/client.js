{
    var height = $(document).height();
    var width = $(document).width();
    console.log("w:" + width + "h:" + height)
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
        if (received_msg.indexOf("inc") > -1) {
            if (received_msg.indexOf("l:")) {
                counterL.increment();
            } else {
                counterR.increment();
            }
        } else if (received_msg.indexOf("dec") > -1) {
            if (received_msg.indexOf("l:")) {
                counterL.decrement();
            } else {
                counterR.decrement();
            }
        } else if (received_msg.indexOf("stats") > -1) {
            if (received_msg.indexOf("l:") > -1) {
                if ($('#leftDrawer').width() > 50) {
                    $('#leftDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50)  }).animate({width: '0px', visibility: "hidden" }, "slow");
                    $('.drawerName').css({"visibility": "hidden"});
                } else {
                    $('#leftDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50), "visibility": "visible"  }).animate({width: width / 3}, "slow")
                    $('.drawerName').css({"visibility": "visible", "color": "white", "marginLeft": (width - 150) / 7});
                }
            } else {
                if ($('#rightDrawer').width() > 50) {
                    $('#rightDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50)  }).animate({width: '0px', visibility: "hidden" }, "slow");
                    $('.drawerName').css({"visibility": "hidden"});
                } else {
                    $('#rightDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50), "visibility": "visible"  }).animate({width: width / 3}, "slow")
                    $('.drawerName').css({"visibility": "visible", "color": "white", "marginLeft": (width - 150) / 7});
                }
            }

        } else if (received_msg.indexOf("clist") > -1) {
            $('#clientsView').append(received_msg);
        }
        else if (received_msg.indexOf("time:") > -1) {
              var t=received_msg.split(":");
            counter.setTime(t[2] * 60);
        } else if (received_msg.indexOf("start:") > -1) {
            counter.start();
        } else if (received_msg.indexOf("pause:") > -1) {
            counter.stop();

        }else {

        }
    };
    ws.onclose = function () {
        // websocket is closed.
        console.log("før loc replace")
        console.log("after loc replace")
    };

    function screenResize() {
        $('#pictureHolder').css({"width": width - 20, "height": height - 50})
        $('#player1').css({"width": ((width - 50) / 2), "height": ( height - 50)  })
        $('#player2').css({"width": ((width - 50) / 2), "height": ( height - 50) })
        $('#vs').css({"width": ( width / 6), "height": ( height / 3),
            "left": (width / 2) - (width / 10), "top": (height / 9)});
        $('#table').css({"width": ( width ), "height": ( height / 3) });
        var nameSize= $("#nameOfTheGame").width();

        $('#nameOfTheGame').css({"left": (width-nameSize)/2});
        $('#vsImage').css({"width": "100%", "height": "100%"})
        $('.playerImg').css({"width": (width - 50) / 4, "height": (height / 2), "marginLeft": (width - 50) / 9})
        var counterWidth = ((width - 100) / 4);
        counter = $('.counter').FlipClock(000, {clockFace: 'MinuteCounter', countdown: "true", hideLabels: "true"
        });
        counterL = $('.counterL').FlipClock({clockFace: 'Counter' });
        counterR = $('.counterR').FlipClock(000, {clockFace: 'Counter' });

        var fCSize = $('.flip-clock-wrapper').width();
        var pad = ((width - 50) / 2) / 4;
        var centerPad = ((width - fCSize) / 2) + 20;
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