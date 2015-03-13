{
    var height = $(document).height();
    var width = $(document).width();
    var counter;
    var counterL;
    var counterR;
    var statF;
    var statS;
    var isStarted=false;
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

        var received_msg = evt.data;

        var jsonson= undefined;
         if( received_msg.indexOf("b64") > -1) {
             jsonson =JSON.parse(received_msg);
             console.log("jsonson", jsonson);
         }

        if (  undefined != jsonson && undefined != jsonson.userName ) {
            var picfbo={userName:"",b64:"",player:"",pos:"",stat:""}

            picfbo.userName = jsonson.userName;
            picfbo.b64 = jsonson.b64;
            picfbo.pos= jsonson.pos;
            if(picfbo.pos === "f") {
                $("#fPlayerName").text(picfbo.userName);
                document.getElementById("player1Img").src = picfbo.b64;
                statF = JSON.parse(jsonson.stat);
                console.log("jsonson:",statF)
            }
            else {
                $("#sPlayerName").text(picfbo.userName);
                document.getElementById("player2Img").src = picfbo.b64;
                statS = JSON.parse(jsonson.stat);
                console.log("jsonson:",statF)

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
                        $('.drawerNameL').css({"visibility": "hidden"});
                        $('.statL').css({"visibility": "hidden"});
                    } else {
                        $('#leftDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50), "visibility": "visible"  }).animate({width: width / 3}, "slow")
                        $('.drawerNameL').css({"visibility": "visible", "color": "white", "marginLeft": (width - 150) / 7});
                        $('.statL').css({"visibility": "visible", "color": "white", "marginLeft": 50 });
                        $('#lWon').text(statF.win);
                        $('#lScore').text(statF.score);
                        $('#lLost').text(statF.lost);
                        $('#lDraw').text(statF.draw);
                    }
                }
                else {
                    if ($('#rightDrawer').width() > 50) {
                        $('#rightDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50)  }).animate({width: '0px', visibility: "hidden" }, "slow");
                        $('.drawerNameR').css({"visibility": "hidden"});
                        $('.statR').css({"visibility": "hidden"});
                    } else {
                        $('#rightDrawer').css({"width": ((width - 150) / 3), "height": ( height - 50), "visibility": "visible"  }).animate({width: width / 3}, "slow")
                        $('.drawerNameR').css({"visibility": "visible", "color": "white", "marginLeft": (width - 150) / 7});
                        $('.statR').css({"visibility": "visible", "color": "white","marginLeft": 50  });
                        $('#rWon').text(statS.win);
                        $('#rScore').text(statS.score);
                        $('#rLost').text(statS.lost);
                        $('#rDraw').text(statS.draw);
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

                counter.stop();

            }  else if (received_msg.indexOf("maintain") > -1) {
                document.getElementById("player1Img").src =" img/user-ic2.png";
                document.getElementById("player2Img").src = " img/user-ic.png";
                $("#fPlayerName").text("Player 1");
                $("#sPlayerName").text("Player 2");
                counterL.reset();
                counterR.reset();
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

        counterL = $('.counterL').FlipClock(0,{clockFace: 'Counter' });
        counterR = $('.counterR').FlipClock(0, {clockFace: 'Counter' });
        counter = $('.counter').FlipClock(0, {clockFace: 'MinuteCounter', countdown: true, hideLabels: "true",autoStart:false,
            callbacks:{stop:function(){
                if(isStarted){
                    var lResult=counterL.getTime().time;
                    var rResult=counterR.getTime().time;
                    ws.send("results:"+$("#fPlayerName").text()+":"+lResult+":"+$("#sPlayerName").text()+":"+rResult);
                    ws.send("gameover");
                    isStarted=false;
                }

            },start:function(){isStarted=true;}}});
        var fCSize = $('.flip-clock-wrapper').width();
        var pad = ((width - 50) / 2) / 4;
        var centerPad = ((width - fCSize) / 2) + 30;
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