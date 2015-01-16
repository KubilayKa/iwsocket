{

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