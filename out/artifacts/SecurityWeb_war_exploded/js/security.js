function AntiSqlValid(oField )
{
        oField.value = '';
        oField.className="errInfo";
        oField.focus();
}
function SQLInjectionCheck(str){
    let reg=/(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)|(\*|;|\+|'|%|"|>|<|=|`)/
    if(reg.test(str)){
        alert("请您不要在参数中输入特殊字符和SQL关键字！");
        return false;
    }else {
        return true;
    }
}

