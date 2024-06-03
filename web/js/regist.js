
const password = document.getElementById('password')
const background = document.getElementById('background')

password.addEventListener('input', (e) => {
  const input = e.target.value
  const length = input.length
  const blurValue = 20 - length * 4
  background.style.filter = `blur(${blurValue}px)`
})
$("#btn").click(function (){
    console.log(1);
    $('#error-username-have').css('display','none');
    $("#error-username").css('display','none');
    $("#error-password").css('display','none');
    let flag1=true,flag2=true;
    if(!($("#password").val().length>=6&&$("#user_name").val().length<=20)){
        $("#error-password").css('display','inline');
        flag1=false;
    }
    if(!($("#user_name").val().length>=6&&$("#password").val().length<=20)){
        $("#error-username").css('display','inline');
        flag2=false;
    }
    if(flag1&&flag2){
        let sm3Password=sm3Digest($("#password").val());
        console.log(sm3Password);
        // let data=$("#formData").serializeArray();
        let data='Regist?user_name='+$("#user_name").val()+'&password='+sm3Password+'&email='+$("#email").val()+'&check='+$("#mailcheck").val();
        $.get(data,function (msg){
          if(msg=='用户名已存在'){
              $('#error-username-have').css('display','inline');
          }
          if(msg=="success"){
            window.location.href="index.html";
          }
          if(msg=='验证码错误'){
              alert("验证码错误");
          }
      },'text');
    }
})
$("#sendMail").click(function (){
    let email=$("#email").val();
    let userName=$("#user_name").val();
    $.get('SendMail?email='+email+'&user_name='+userName,function (msg) {
        if(msg=='0')    alert("已发送")
        else alert("对不起请稍后再试")
    })
})