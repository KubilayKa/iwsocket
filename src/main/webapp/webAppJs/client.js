{

function sendMessage(){
    var txt2snd=$("#messageText").val();
    console.log("*********** sending message "+ txt2snd);
    var ws = new WebSocket("ws://localhost:8080/inbound");
    ws.onopen = function()
    {
        // Web Socket is connected, send data using send()
        ws.send(txt2snd);
        alert("Message is sent..." + txt2snd);
    };
    ws.onmessage = function (evt)
    {
        var received_msg = evt.data;
        alert("Message is received..."+ received_msg);
        $("#messageLog").val(received_msg);
    };
    ws.onclose = function()
    {
        // websocket is closed.
        alert("Connection is closed...");
    };


}}