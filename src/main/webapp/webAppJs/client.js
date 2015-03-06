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

    }

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
        console.log(evt.data)
        var received_msg = evt.data;
        var jsonson= undefined;
         if(received_msg.length > 20) {
             jsonson =JSON.parse(received_msg);
         }
        if (  undefined != jsonson && undefined != jsonson.userName ) {
            var picfbo={userName:"",b64:"",player:"",pos:""}
            console.log("in jsonson")
            picfbo.userName = jsonson.userName;
            picfbo.b64 = jsonson.b64;
            picfbo.pos= jsonson.pos;
            if(picfbo.pos === "f") {
                $("#fPlayerName").text(picfbo.userName);   console.log("fir")
                document.getElementById("player1Img").src = picfbo.b64;
            }
            else {
                $("#sPlayerName").text(picfbo.userName);console.log("sec")
                document.getElementById("player2Img").src = picfbo.b64;
            }
        } else {
            if (received_msg.indexOf("inc") > -1) {
                if (received_msg.indexOf("l:") > -1) {
                    counterL.increment();
                } else {
                    counterR.increment();
                }
            } else if (received_msg.indexOf("dec") > -1) {
                if (received_msg.indexOf("l:") > -1) {
                    counterL.decrement();
                } else {
                    counterR.decrement();
                }
            } else if (received_msg.indexOf("stats") > -1) {
                if (received_msg.indexOf("l:") > -1) {
                    console.log("er i l:")
                    if ($('#leftDrawer').width() > 50) {
                        $('#leftDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50)  }).animate({width: '0px', visibility: "hidden" }, "slow");
                        $('.drawerName').css({"visibility": "hidden"});

                    } else {
                        $('#leftDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50), "visibility": "visible"  }).animate({width: width / 3}, "slow")
                        $('.drawerName').css({"visibility": "visible", "color": "white", "marginLeft": (width - 150) / 7});
                    }
                }
                else {
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
            } else if (received_msg.indexOf("time:") > -1) {
                var t = received_msg.split(":");
                counter.setTime(t[2] * 60);
            } else if (received_msg.indexOf("restart:") > -1 || received_msg.indexOf("start:") > -1) {
                counter.start();
            } else if (received_msg.indexOf("pause:") > -1) {
                console.log("er i pause:")
                counter.stop();

            }  else if (received_msg.indexOf("db:") > -1) {
                alert("u did it dude" + "\n" + received_msg)
            }
        }

    };
    ws.onclose = function () {
        // websocket is closed.

    };

    function screenResize() {
        $('#pictureHolder').css({"width": width - 20, "height": height - 50})
        $('#player1').css({"width": ((width - 50) / 2), "height": ( height - 50)  })
        $('#player2').css({"width": ((width - 50) / 2), "height": ( height - 50) })
        $('#vs').css({"width": ( width / 6), "height": ( height / 3),
            "left": (width / 2) - (width / 10), "top": (height / 9)});
        $('#table').css({"width": ( width ), "height": ( height / 3) });
        var nameSize = $("#nameOfTheGame").width();

        $('#nameOfTheGame').css({"left": (width - nameSize) / 2});
        $('#vsImage').css({"width": "100%", "height": "100%"})
        $('.playerImg').css({"width": (width - 50) / 4, "height": (height / 2), "marginLeft": (width - 50) / 9})
        var counterWidth = ((width - 100) / 4);

        counterL = $('.counterL').FlipClock(2,{clockFace: 'Counter' });
        counterR = $('.counterR').FlipClock(3 , {clockFace: 'Counter' });
        counter = $('.counter').FlipClock(5, {clockFace: 'MinuteCounter', countdown: true, hideLabels: "true",autoStart:true,
            callbacks:{stop:function(){
                var lResult=counterL.getTime().time;
                var rResult=counterR.getTime().time;

                ws.send("results:"+$("#fPlayerName").text()+":"+lResult+":"+$("#sPlayerName").text()+":"+rResult);
            }}});
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