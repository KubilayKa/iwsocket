{
function sendMessage(){
    console.log("*********** sending message");
    var ws = new WebSocket("ws://localhost:8080/inbound");
    ws.onopen = function()
    {
        // Web Socket is connected, send data using send()
        ws.send("Client says hei");
        alert("Message is sent...");
    };
    ws.onmessage = function (evt)
    {
        var received_msg = evt.data;
        alert("Message is received..."+ received_msg);
    };
    ws.onclose = function()
    {
        // websocket is closed.
        alert("Connection is closed...");
    };


}}