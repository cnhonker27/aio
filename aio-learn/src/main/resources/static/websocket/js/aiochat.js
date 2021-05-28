let layer;
$(function(){
    connect();
    layui.use('layer',function(){
        layer=layui.layer;
    })
});

function connect() {
    if (curUser) {
        alert("当前已登录,请先退出登录!");
        return;
    }
    let ip = "127.0.0.1";
    //let ip = "randspace.cn";
    let port = "6951";
    username = "username";
    let password = "password";
    logDiv = document.getElementById('logs');
    socket = new WebSocket("ws:" + ip + ":" + port + "?username=" + username + "&password=" + password);
    socket.onopen = function (e) {
        var json = "{\"targetId\":\"目标id\",\"senderId\":\"发送者id\",\"context\":\"内容\",\"contextType\":\"text\",\"msgType\":\"groupChat\",\"wsChatMsgCommand\":\"login\"}";
        socket.send(json)
    };
}