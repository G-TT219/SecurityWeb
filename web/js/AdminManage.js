$(document).ready(function () {
    checkLoginStatus();
});

$('#logout').click(function (){
    deleteCookie('user_name');
    checkLoginStatus();
    $('#user_name').html('游客');
    $('#user_score').html(0);
    $('#user_money').html(0);
    $('#my-order-tbody').html('');
})
$('#search_btn').click(function (){
    refreshTable();
});
let index=0;
let jsonStr='{"index":'+index.toString()+',"function":"select"}';

function updateJsonStr(){
    jsonStr='{"index":'+index.toString()+',"function":"select"}';
}
let jsons;
let length;
function refreshTable(){
    let start_station=$("#search_input_start_station").val(),end_station=$("#search_input_end_station").val();
    if(start_station==''&&end_station==''){
        $("#tips").html("请输入关键词进行搜索");
        $("#tips").css("display","");
        $("#tips").css("color","red");
    }else if ($("#search_input_date").val()==''){
        $("#tips").html("请输入日期");
        $("#tips").css("display","");
        $("#tips").css("color","red");
    }else if(!SQLInjectionCheck(start_station)){
        AntiSqlValid($('#search_input_start_station'));
    }else if(!SQLInjectionCheck(end_station)){
        AntiSqlValid($('#search_input_end_station'));
    }
    else {
        $("#tips").css("display","none");
        let date = $("#search_form").serializeArray();
        $.get('Search', date, function (json) {
            jsons=json;
            length = 0;
            for (const temp in jsons) {
                length++;
            }
            console.log(length);
            let tbody = '';
            for (let i = 0; i < length; i++) {
                tbody += '<tr>';
                tbody += '<td>' + jsons[i]['train_name'] + '</td>';
                tbody += '<td>' + jsons[i]['start_station'] + '</td>';
                tbody += '<td>' + jsons[i]['end_station'] + '</td>';
                tbody += '<td>' + jsons[i]['time'] + '</td>';
                tbody += '<td>' + jsons[i]['seats'] + '</td>';
                tbody += '<td>' + '<b class="edit" style="cursor: pointer;color: currentcolor;">购买</b>' + '</td>';
                tbody += '</tr>';
            }

            $('#tbody').html(tbody);
            let firstNum = 1 + index * 15;
            let lastNum = 15 + index * 15;
            if (length < lastNum) {
                lastNum = length;
            }
            $('#page').html('(' + firstNum + '-' + lastNum + ') ' + 'in ' + length);
        }, 'json')
    }
}
$("#last").click(function (){
    if(index>0) {index--;
        updateJsonStr();
    }else{
        $('body').append('<div class="msg" style="animation: 2s linear 0s 1 normal none running show_and_disappear;">已经是第一页了:D</div>')
        setTimeout(function () {
            $('body').children().last().remove();
        },1000);
    }
    refreshTable();
});
$("#next").click(function (){
    if(15+index*15<length) {index++;
        updateJsonStr();
    }else{
        $('body').append('<div class="msg" style="animation: 2s linear 0s 1 normal none running show_and_disappear;">已经是最后一页了:D</div>')
        setTimeout(function () {
            $('body').children().last().remove();
        },1000);
    }
    refreshTable();
});

let thisTrain;
let soldSeats;
$(document).on('mousedown','.edit',function (){
    if(checkCookie('user_name')) {
        let train_name = $(this).parent().parent().children().first().html();
        for (let i = 0; i < length; i++) {
            console.log(train_name);
            if (jsons[i]["train_name"] == train_name) {
                thisTrain = jsons[i];
                break;
            }
        }
        $('.box').css("display", "block");
        $("#train_name").html(thisTrain['train_name']);
        updateTicketPage();
    }else{
        alert("购票请先登录");
    }
})
let selectBtn;
let position={
    row:null,
    col:null
}
$(document).on('mousedown','.seat',function () {
    if($(this).attr('class')=='seat sold'){
    }else {
        let selectNow = this;
        if (Object.is(selectNow, selectBtn)) {
            $(this).removeClass('selected');
            selectBtn = null;
            $("#seat-position").html('');
        } else {
            $(selectBtn).removeClass('selected');
            $(this).addClass('selected');
            selectBtn = this;
            position.col = LetterTo123($(this).html());
            position.row = $(this).parent().attr("value");
            $("#seat-position").html(position.row + '排' + NumberToLetter(position.col) + '号');
        }
    }
});
$("#cancel-btn").click(function (){
    cancelTicketPage();
})
$("#buy-btn").click(function () {
    $.get("Buy?train_name="+thisTrain['train_name']+"&seat_row="+position.row+"&seat_col="+position.col,function (text) {
        if(text=='座位已售'){
            alert("座位已售，请重新选择");
            updateTicketPage();
        }else if(text=="余额不足"){
            alert("余额不足");
            cancelTicketPage();
        }
        else{
            alert("购买成功");
            cancelTicketPage();
        }
    },'text');
})

$('#nav_item_user_info').click(function () {
    $('#nav_item_ticket>a').removeClass('active');
    $('#nav_item_entertainment>a').removeClass('active');
    $('#nav_item_user_info>a').addClass('active');
    $('#main_user_info').css('display','');
    $('#main_ticket').css('display','none');
    $('#main_entertainment').css('display','none');
    getUserInfo();
})


$('#nav_item_ticket').click(function () {
    $('#nav_item_ticket>a').addClass('active');
    $('#nav_item_user_info>a').removeClass('active');
    $('#nav_item_entertainment>a').removeClass('active');
    $('#main_user_info').css('display','none');
    $('#main_entertainment').css('display','none');
    $('#main_ticket').css('display','')
})
$('#nav_item_entertainment').click(function (){
    $('#nav_item_ticket>a').removeClass('active');
    $('#nav_item_user_info>a').removeClass('active');
    $('#nav_item_entertainment>a').addClass('active');
    $('#main_user_info').css('display','none');
    $('#main_entertainment').css('display','');
    $('#main_ticket').css('display','none');
    getEntertainmentInfo();
})

function getUserInfo() {
    if (checkCookie('user_name')) {
        $.get('GetUserInfo?', function (jsons) {
            console.log(jsons);
            let size=0;
            for (const temp in jsons){
                size++;
            }
            let tbody='';
            for(let i=0;i<size;i++)
            {
                if(i==0){
                    $('#user_name').html(getCookie('user_name'));
                    $('#user_score').html(jsons[i]['user_score']);
                    $('#user_money').html(jsons[i]['user_money']);
                }else{
                    tbody += '<tr>';
                    tbody += '<td>' + jsons[i]['train_name'] + '</td>';
                    tbody += '<td>' + jsons[i]['start_station'] + '</td>';
                    tbody += '<td>' + jsons[i]['end_station'] + '</td>';
                    tbody += '<td>' + jsons[i]['time'] + '</td>';
                    tbody += '<td>' + jsons[i]['seat_row'] + '</td>';
                    tbody += '<td>' + NumberToLetter(parseInt(jsons[i]['seat_col'])) + '</td>';
                    tbody += '<td>' +  '<b class="refund" style="cursor: pointer;color: currentcolor;">退票</b>'  + '</td>';
                    tbody += '</tr>';
                }
            }
            $("#my-order-tbody").html(tbody);
        }, 'json');
    }
}
$(document).on("mousedown",'.refund',function (){
    let sure= confirm('你真的要退票么？');
    if(sure){
        let train_name=$(this).parent().parent().children().eq(0).html();
        let start_station=$(this).parent().parent().children().eq(1).html();
        let end_station=$(this).parent().parent().children().eq(2).html();
        let seat_row=$(this).parent().parent().children().eq(4).html();
        let seat_col=LetterTo123($(this).parent().parent().children().eq(5).html());
        $.get('Refund?train_name='+train_name+'&start_station='+start_station+'&end_station='+end_station+'&seat_row='+seat_row+'&seat_col='+seat_col,function (text){
            if(text=='success'){
                alert('退票成功');
                getUserInfo();
            }else {
                alert('退票失败');
            }
        },'text')
    }
})

function cancelTicketPage(){
    $(".box").css("display","none");
    $("#seat-position").html("");
    if(selectBtn!=null){
        $(selectBtn).removeClass('selected');
    }
    selectBtn=null;
    let i=0;
    for(const temp in soldSeats){
        $('.select-seat').find('div').eq(soldSeats[i]['seat_row']-1).find('button').eq(soldSeats[i]['seat_col']-1).removeClass("sold");
        i++;
    }
}
function updateTicketPage(){
    $.get('SearchRestSeats?train_name='+thisTrain['train_name'],function (data){
        soldSeats=data;
        let i=0;
        for(const temp in data){
            $('.select-seat').find('div').eq(data[i]['seat_row']-1).find('button').eq(data[i]['seat_col']-1).addClass("sold");
            i++;
        }
    },'json')
    $(selectBtn).removeClass('selected');
}
function checkLoginStatus(){
    if(checkCookie('user_name')) {
        $("#user_account").css('display', 'inline-block');
        $("#user_account").find('a').html("欢迎:" + getCookie('user_name'));
        $('#login_btn').css('display', 'none');
        $('#logout').css('display','inline-block');
    }else{
        $("#user_account").css('display', 'none');
        $('#login_btn').css('display', 'inline-block');
        $('#logout').css('display','none');
    }
}
function LetterTo123(a){
    let offset=a.charCodeAt(0)-'A'.charCodeAt(0);
    return 1+offset;
}
function NumberToLetter(a){
    return String.fromCharCode(64+a);
}
function getEntertainmentInfo(){
    $.get('GetEntertainmentInfo?',function (jsons) {
        let musicTableBody='',videoTableBody='';
        let i,count=0;
        for(const temp in jsons){
            count++;
        }
        for(i=0;i<count;i++){
            if(jsons[i]['type']=='music'){
                musicTableBody += '<tr>';
                musicTableBody += '<td>' + jsons[i]['resource_name'] + '</td>';
                musicTableBody += '<td>' + jsons[i]['resource_score'] + '</td>';
                musicTableBody += '<td>' +  '<b class="download" style="cursor: pointer;color: currentcolor;">购买</b>'  + '</td>';
                musicTableBody += '</tr>';
            }
            else if(jsons[i]['type']=='video') {
                videoTableBody += '<tr>';
                videoTableBody += '<td>' + jsons[i]['resource_name'] + '</td>';
                videoTableBody += '<td>' + jsons[i]['resource_score'] + '</td>';
                videoTableBody += '<td>' + '<b class="download" style="cursor: pointer;color: currentcolor;">购买</b>' + '</td>';
                videoTableBody += '</tr>';
            }
        }
        $('#entertainment-music-tbody').html(musicTableBody);
        $('#entertainment-video-tbody').html(videoTableBody);
    },'json');
}
$(document).on('mousedown','.download',function () {
    let resource_name=$(this).parent().parent().children().first().html();
    let score=$(this).parent().parent().children().eq(1).html();
    if(checkCookie('user_name')) {
        let result= confirm("你确定要使用"+score+"分下载当前资源么？");
        if(result==true) {
            $.get('RequestDownload?resource_name='+resource_name+'&resource_score='+score, function (msg) {
                if (msg =='积分不足'){
                    alert('积分不足');
                }else{
                    let url=msg;
                    download1(url);
                }
                }, 'text');
        }
    }else{
        alert("请先登录");
    }
})
function download1(URI){
    $.get(URI,function (){
        const a = document.createElement('a');
        a.href = URI;
        document.documentElement.appendChild(a);
        a.click();
        a.remove();
    });
}
// $("#search_input_start_station").attr("onblur","AntiSqlValid(this)");//防止Sql脚本注入
// $("#search_input_end_station").attr("onblur","AntiSqlValid(this)");
// $("#search_input_date").attr("onblur","AntiSqlValid(this)");