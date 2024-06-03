const password = document.getElementById('password')
const background = document.getElementById('background')

password.addEventListener('input', (e) => {
    const input = e.target.value
    const length = input.length
    const blurValue = 20 - length * 4
    background.style.filter = `blur(${blurValue}px)`
})
$("#submit").click(function (){
    $("#error-password").css('display','none');
    $("#error-username").css('display','none');
    let sm3Password=sm3Digest($("#password").val());
    console.log(sm3Password);
    //let data = $("#form").serializeArray();
    let data='Login?user_name='+$("#user_name").val()+'&password='+sm3Password;
    $.get(data,function (msg) {
        if (msg == '密码错误') {
            console.log("密码错误");
            $("#error-password").css('display','inline');
        }
        if(msg=='不存在用户名'){
            console.log("用户名错误")
            $("#error-username").css('display','inline');
        }
        if(msg=="登陆成功"){
            window.location.href='encrypt.html';
        }
    },'text');
});