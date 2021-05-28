var layer
$(function(){
    connect();
    layui.use('layer',function(){
        layer=layui.layer;
    })
})
var socket ;
var logDiv;
var username;
var onSelected;
var curUser;
var friends = [];
var groups = [];

function connect(){
    if(curUser){
        alert("当前已登录,请先退出登录!");
        return;
    }
    let ip = "127.0.0.1";
    //let ip = "randspace.cn";
    let port ="6951";
    username = "username";
    let password ="password";
    logDiv = document.getElementById('logs');
    socket = new WebSocket("ws:"+ip+":"+port+"?username="+username+"&password="+password);

    socket.onopen = function(e){
        var json="{\"targetId\":\"目标id\",\"senderId\":\"发送者id\",\"context\":\"内容\",\"contextType\":\"text\",\"msgType\":\"groupChat\",\"wsChatMsgCommand\":\"login\"}";
        socket.send(json)
    };
    socket.onerror = function(e){
        logDiv.innerHTML+="<font color='red' size='1'>异常:"+e+"</font><br>";
        scrollToBottom();
    };
    socket.onclose = function(e){
        curUser = null;
        logDiv.innerHTML+="<font color='green' size='1'>关闭连接...</font><br>";
        document.getElementById("onlinePanel").innerHTML="&nbsp;在线成员(0/0)";
        scrollToBottom();
    };
    socket.onmessage = function(e){
        let data = e.data;
        console.log("data="+data)
        let dataObj = eval("("+data+")");//转换为json对象
        if(dataObj.msgType=='login'&&dataObj.status==2000){
            setUserName(dataObj.data);
            return;
        }
        if(dataObj.msgType=='join'){
            COMMAND_JOIN_GROUP_NOTIFY_RESP(dataObj);
            return;
        }
        if(dataObj.msgType == "talk"){//接收到聊天响应处理;
            COMMAND_CHAT_RESP(dataObj);
            return;
        }
        if(dataObj.msgType == "get"){//获取用户信息响应处理;
            COMMAND_GET_USER_RESP(dataObj);
            return;
        }
        if(dataObj.msgType == "response"){
            layer.msg(dataObj.msg,{times:2000,icon:6});
            return;
        }
        scrollToBottom();
    };
}
//登陆通知处理
function COMMAND_LOGIN_RESP(dataObj,data){
    if(10007 == dataObj.code){//登陆成功;
        logDiv.innerHTML+="<font color='green' size='1'>连接成功...</font><br>";
        var userCmd = "{\"cmd\":17,\"type\":\"0\",\"userId\":\""+username+"\"}";
        var msgCmd = "{\"cmd\":19,\"type\":\"0\",\"userId\":\""+username+"\"}";
        socket.send(userCmd);//获取登录用户信息;
        socket.send(msgCmd);//获取用户离线消息(好友+群组);
        scrollToBottom();
    }else if(10008 == dataObj.code){//登录失败;
        OTHER(data);
    }
}
function COMMAND_EXIT_GROUP_NOTIFY_RESP(data){
    var exitGroupNotify = data.data;
    var onlineUserCmd = "{\"cmd\":17,\"type\":\"0\",\"userId\":\""+curUser.userId+"\"}";
    logDiv.innerHTML+="<font color='#A3A3A3' size='1'>"+exitGroupNotify.user.nick+"("+exitGroupNotify.user.userId+")退出群聊...</font><br>";
    socket.send(onlineUserCmd);//获取在线用户列表;
}
//加入群组的消息通知处理;
function COMMAND_JOIN_GROUP_NOTIFY_RESP(data){
    var user = data.nick;
    logDiv.innerHTML+="<font color='#A3A3A3' size='1'>"+($("#username").val())+"加入群聊...</font><br>";
    //获取在线用户列表;
    getGroupOnline();
}
//加入群组响应状态处理;
function COMMAND_JOIN_GROUP_RESP(data){
    //成功加入群组响应信息;
}
//发送聊天请求发送状态处理;
function COMMAND_CHAT_RESP_SEND_STATUS(data){
    //发送成功后的状态处理...
}
//获取用户信息响应处理;
function COMMAND_GET_USER_RESP(data){
    var user =  data.data;
    curUser = user;
    initOnlineUsers();
}
//接收到聊天响应处理;
var times=0;
function COMMAND_CHAT_RESP(data){
    var chatObj = data.data;
    var createTime = new Date(chatObj.createTime).Format("yyyy/MM/dd HH:mm:ss");
    var from = chatObj.senderId;
    if(from == $("#userid").val())
        return;
    var content = chatObj.context;
    var user = getOnlineUserById(from);
    if(user){
        times=0;
        //logDiv.innerHTML+="<font color='#009ACD' size='1' style='font-weight: bold'>"+user.userName+"("+user.userId+")"+" "+createTime+"</font><br>";
        logDiv.innerHTML+="<font color='#009ACD' size='1' style='font-weight: bold'>"+user.userName+""+" "+createTime+"</font><br>";
    }else{
        if(times==0){
            times++;
            getGroupOnline();
            setTimeout( function(){COMMAND_CHAT_RESP(data)},2000);
        }
        /*logDiv.innerHTML+="<font color='#009ACD' size='1' style='font-weight: bold'>"+from+" "+createTime+"</font><br>";*/
        return;
    }
    times=0;
    //处理数据
    logDiv.innerHTML+="<font color='#FFFFFF' size='1'>&nbsp;"+content+"</font><br>";
}
//处理用户同步+持久化消息
function COMMAND_GET_MESSAGE_RESP(data,msgFlag){
    var msgObj = data.data;
    friendOfflineMessage(msgObj,msgFlag);
    groupOfflineMessage(msgObj,msgFlag);
}
//好友消息
function friendOfflineMessage(msgObj,msgFlag){
    var friends = msgObj.friends;
    for (var key in friends) {
        var chatDatas = friends[key];
        for(var index in chatDatas){
            var user_id = chatDatas[index].from;
            var createTime = new Date(chatDatas[index].createTime).Format("yyyy/MM/dd HH:mm:ss");
            logDiv.innerHTML+="<font color='	#009ACD' size='1' style='font-weight: bold'>"+user_id+"</font><font color='#DC143C' size='1' style='font-weight: bold'>(好友"+msgFlag+")</font>"+"<font color='#009ACD' size='1' style='font-weight: bold'>"+createTime+"</font><br>";
            logDiv.innerHTML+="<font color='#FFFFFF' size='1'>&nbsp;"+chatDatas[index].content+"</font><br>";
        }
    }
}
//群组消息
function groupOfflineMessage(msgObj,msgFlag){
    var groups = msgObj.groups;
    for (var key in groups) {
        var chatDatas = groups[key];
        for(var index in chatDatas){
            var user_id = chatDatas[index].from;
            var createTime = new Date(chatDatas[index].createTime).Format("yyyy/MM/dd HH:mm:ss");
            logDiv.innerHTML+="<font color='	#009ACD' size='1' style='font-weight: bold'>"+user_id+"</font><font color='#DC143C' size='1' style='font-weight: bold'>(群聊["+chatDatas[index].groupId+"]"+msgFlag+")</font>"+"<font color='#009ACD' size='1' style='font-weight: bold'>"+createTime+"</font><br>";
            logDiv.innerHTML+="<font color='#FFFFFF' size='1'>&nbsp;"+chatDatas[index].content+"</font><br>";
        }
    }
}
//其它信息处理;
function OTHER(data){
    //处理数据
    logDiv.innerHTML+="<font color='green' size='1'>"+data+"</font><br>";
}
function getOnlineUserById(id){
    var groups = curUser.groups;
    var onlineUserStr = "";
    for(var g = 0 ; g < groups.length ; g++){
        var group = groups[g];
        var users = group.users;
        for(var u = 0 ; u < users.length ; u++){
            var user = users[u];
            if(user.userId == id){
                return user;
            }
        }
    }
}
function initOnlineUsers(){
    var groups = curUser.groups;
    var onlineUserStr = "";
    for(var g = 0 ; g < groups.length ; g++){
        var group = groups[g];
        var users = group.users;
        onlineUserStr += "&nbsp;"+group.groupName+"在线成员("+users.length+"/"+users.length+")";
        for(var u = 0 ; u < users.length ; u++){
            var user = users[u];
            onlineUserStr +="<div id=\""+user.userId+"\" nick=\""+user.userName+"\" style=\"line-height: 25px;margin: 5px 5px 0 5px;padding-left:15px;cursor:pointer;\" onclick=\"onlineDb(this);\" onmouseover=\"onlineMove(this);\"  onmouseleave=\"onlineLeave(this);\"><img alt=\""+user.userId+"\" src=\""+user.avatar+"\" height=\"25px\" width=\"25px;\" style=\"float:left\">&nbsp;<font size='2'>"+user.userName+"</font></div>";
        }
    }
    if(!onlineUserStr){
        onlineUserStr = "&nbsp;在线成员(0/0)";
    }
    document.getElementById("onlinePanel").innerHTML = onlineUserStr;
}
function disConnect(){
    socket.close();
}

function send(){
    var toId = "";
    if(onSelected){
        toId = onSelected.getElementsByTagName("img")[0].alt;
    }
    var createTime = new Date().getTime();
    var content = document.getElementById('content').value;
    if(content == "")
        return ;
    var msg = "{\"from\": \""+username+"\",\"to\": \""+toId+"\",\"cmd\":11,\"createTime\":"+createTime+",\"chatType\":\"2\",\"msgType\": \"0\",\"content\": \""+content+"\"}";
    if(!toId){
        alert("请选择要私聊的人!");
        return;
    }
    if(toId == username){
        alert("无法给自己发送消息!");
        return ;
    }
    socket.send(msg);
    var chatObj = eval("("+msg+")");
    var createTime = new Date(chatObj.createTime).Format("yyyy/MM/dd HH:mm:ss");
    //处理数据
    logDiv.innerHTML+="<font color='#228B22' size='1' style='font-weight: bold'>"+chatObj.from+" "+createTime+"</font><br>";
    //处理数据
    logDiv.innerHTML+="<font color='#FFFFFF' size='1'>&nbsp;"+chatObj.content+"</font><br>";
    document.getElementById('content').value = "";
}
function sendGroup(){
    var createTime = new Date().getTime();
    var content = $('#content').val();
    var userid = $('#userid').val();
    if(content == "")
        return ;
    var msg = "{\"targetId\":\"1000\",\"senderId\":\""+userid+"\",\"context\":\""+content+"\",\"contextType\":\"text\",\"msgType\":\"groupChat\",\"wsChatMsgCommand\":\"send\"}";
    socket.send(msg);
    var chatObj = eval("("+msg+")");
    var createTime = new Date(createTime).Format("yyyy/MM/dd HH:mm:ss");
    //处理数据
    logDiv.innerHTML+="<font color='#228B22' size='1' style='font-weight: bold'>"+($("#username").val())+" "+createTime+"</font><br>";
    //处理数据
    logDiv.innerHTML+="<font color='#FFFFFF' size='1'>&nbsp;"+content+"</font><br>";
    document.getElementById('content').value = "";
}
function scrollToBottom(){
    var logDiv = document.getElementById('logs');
    logDiv.scrollTop = logDiv.scrollHeight;
}
function clearLogs(){
    var logDiv = document.getElementById('logs');
    logDiv.innerHTML="";
}

function onlineDb(obj){
    if(onSelected){
        if(onSelected.id != obj.id){
            onSelected.style.background = "";
        }
    }
    obj.style.background = "#D4D4D4";
    onSelected = obj;
    var sendBtn = document.getElementById("sendBtn");
    sendBtn.style="width: 150px;"
    sendBtn.value="发送给:"+onSelected.id;
}
function onlineMove(obj){
    if("undefined" == typeof(onSelected) || onSelected.id != obj.id){
        obj.style.background = "#F0F0F0";
    }
}
function onlineLeave(obj){
    var onlineDiv = document.getElementById("onlinePanel").getElementsByTagName("div");
    for(var i =0 ; i < onlineDiv.length ; i++){
        if("undefined" == typeof(onSelected) || onSelected.id != onlineDiv[i].id){
            onlineDiv[i].style.background = "";
        }
    }
}
function setUserName(userInfo){
    $("#username").val(userInfo.userName);
    $("#userid").val(userInfo.userId);
}
function keyDown(e) {
    var ev= window.event||e;
    //13是键盘上面固定的回车键
    if (ev.keyCode == 13) {
        sendGroup();
    }
}


function joinGroup(){
    let groupId = $("#join_group").val();
    var userid = $('#userid').val();
    var msg = "{\"targetId\":\""+groupId+"\",\"senderId\":\""+userid+"\",\"contextType\":\"text\",\"msgType\":\"groupChat\",\"wsChatMsgCommand\":\"join\"}";
    socket.send(msg);
}

function getGroupOnline(){
    let groupId = $("#join_group").val();
    var userid = $('#userid').val();
    var msg = "{\"targetId\":\""+groupId+"\",\"senderId\":\""+userid+"\",\"contextType\":\"text\",\"msgType\":\"groupChat\",\"wsChatMsgCommand\":\"get\"}";
    socket.send(msg);
}


