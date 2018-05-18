function $util_getMsg(xhr, callback) {
	var msg = "";

	if(typeof(xhr.responseText) != "undefined" && xhr.responseText != "undefined" && xhr.responseText.toLowerCase().indexOf("missing request header 'token' for method parameter of type string") != -1 || xhr.responseText.indexOf('身份认证错误') != -1) {
		this.$util_alertStr("token失效，请重新登录！");
	}

	if(xhr.status == 0) {
		msg = '系统异常，请稍后再试！';
	} else if(xhr.status >= 400 && (typeof(xhr.responseText) == "undefined" || xhr.responseText == "undefined" || xhr.responseText == "")) {
		if(xhr.status == 404) {
			msg = '链接失效，请稍后再试！';
		} else {
			msg = '系统异常，请稍后再试！';
		}
	} else {
		if(typeof(xhr.responseText) != "undefined" && xhr.responseText != "undefined") {
			msg = eval('('+xhr.responseText+')').data;
			if(msg == '' || typeof(msg) == "undefined" || msg == "undefined") {
				msg = '成功！';
			}
		}
	}

	if(typeof(callback) != "undefined") {
		callback();
	}

	return msg;
}

function $util_alertMsg(xhr,msg,callback) {
	var className = 'alert-success';
	if(xhr.status >= 400) {
		className = 'alert-danger';
	}

	if(typeof(msg) == "undefined" || msg == "undefined" || msg == "") {
		msg = this.$util_getMsg(xhr);
	}

	var msgId = "closeMsg" + Math.random();
	var msgDiv = document.createElement('span');
	msgDiv.className = 'msg alert '+className;
	msgDiv.innerHTML = msg+'<a href="#" class="close" id="'+msgId+'" data-dismiss="alert">&times;</a>';
	document.body.appendChild(msgDiv);
	window.setTimeout("document.getElementById('"+msgId+"').click();",2000);
	if(msg.indexOf('token') != -1 || msg.indexOf('身份认证错误') != -1) {
		window.setTimeout("location.href='/src/page/login.html';",1000);
	}

	if(typeof(callback) != "undefined") {
		callback();
	}
}

function $util_alertSuccess(msg) {
	$util_alertStr(msg, "alert-success");
}

function $util_alertError(msg) {
	$util_alertStr(msg, "alert-danger");
}

function $util_alertStr(msg, className) {
	if(typeof(msg) == "undefined" || msg == "undefined" || msg == "") {
		return;
	}

	if(typeof(className) == "undefined" || className == "undefined" || className == "") {
		className = "alert-success";
	}

	var msgId = "closeMsg" + Math.random();
	var msgDiv = document.createElement('span');
	msgDiv.className = 'msg alert ' + className;
	msgDiv.innerHTML = msg+'<a href="#" class="close" id="'+msgId+'" data-dismiss="alert">&times;</a>';
	document.body.appendChild(msgDiv);
	window.setTimeout("document.getElementById('"+msgId+"').click();",2000);
	if(msg.indexOf('token') != -1 || msg.indexOf('身份认证错误') != -1) {
		window.setTimeout("location.href='/src/page/login.html';",1000);
	}
}

//邮箱格式验证
function $util_checkMail(mail) {
	if(mail == ""){
		return false;
	}

	var reg = new RegExp("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$");
	if(!reg.test(mail)){
		return false;
	}
	return true;
}

//手机格式验证
function $util_checkPhone(phone) {
	if(phone == ""){
		return false;
	}

	var reg = new RegExp("^1[0-9]{10}$");
	if(!reg.test(phone)){
		return false;
	}
	return true;
}

//手机或邮箱格式
function $util_checkMailOrPhone(text) {
	if(!this.$util_checkMail(text) && !this.$util_checkPhone(text)) {
		return false;
	}
	return true;
}

//身份证格式验证
function $util_checkIdCard(idcard) {
	if(idcard == ""){
		return false;
	}

	var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
	if(!reg.test(idcard)){
		return false;
	}
	return true;
}

//数据校验
//fatherId 父控件id，校验该控件下的数据
//data-errMsgId：显示错误信息的div id
//data-disabled：是否校验 true校验
//data-empty：true/false 是否非空
//data-emptyText：如果为空提示文本
//data-min：最小长度
//data-minText：最小长度提示文本
//data-max：最大长度
//data-maxText：最大长度提示文本
//data-length：非空长度长度
//data-lengthText：非空长度长度提示文本
//data-include：非空包含
//data-includeText：非空包含提示文本
//data-same：与id的value是否相同
//data-sameText：与id的value是不同提示文本
//data-num：数字格式
//data-numText：数字格式不正确提示文本
//data-mail：邮箱格式
//data-mailText：邮箱格式不正确提示文本
//data-phone：手机号格式
//data-phoneText：手机号格式不正确提示文本
//data-mailOrPhone：邮箱或手机格式
//data-mailOrPhoneText：邮箱或手机格式不正确提示文本
//data-idcard：身份证号格式
//data-idcardText：身份证号格式不正确提示文本
function $util_validateValue(fatherId) {
	var fatherDOM = document.getElementById(fatherId);
	var inputs = fatherDOM.getElementsByTagName("input");
	if(this.$util_validateDOMs(inputs) == false) {
		return false;
	}
	var textareas = fatherDOM.getElementsByTagName("textarea");
	if(this.$util_validateDOMs(textareas) == false) {
		return false;
	}
	return true;
}

function $util_validateDOMs(doms) {
	for(var i=0; i<doms.length; i++) {
		var errMsgId = doms[i].getAttribute("data-errMsgId");
		var disabled = doms[i].getAttribute("data-disabled");

		if(errMsgId != null && errMsgId != "" && disabled!="true") {
			var errMsgDom = document.getElementById(errMsgId);

			var tmpId = errMsgId+"Tmp";
			var errMsgDomTmp = document.getElementById(tmpId);
			if(!errMsgDomTmp) {
				errMsgDomTmp = errMsgDom.cloneNode();
				errMsgDomTmp.id = tmpId;
				errMsgDom.parentNode.insertBefore(errMsgDomTmp,errMsgDom);
			}

			//非空校验
			var empty = doms[i].getAttribute("data-empty");
			if(empty != null && empty == "true") {
				var emptyText = doms[i].getAttribute("data-emptyText");
				if(emptyText == null || emptyText == "") {
					emptyText = "不能为空";
				}
				errMsgDomTmp.innerHTML = emptyText;

				if(doms[i].value == "") {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//最小长度校验
			var min = doms[i].getAttribute("data-min");
			if(min != null) {
				var minText = doms[i].getAttribute("data-minText");
				if(minText == null || minText == "") {
					minText = "最小长度为" + min + "个字符";
				}
				errMsgDomTmp.innerHTML = minText;

				if(doms[i].value.length < Number(min)) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//最大长度校验
			var max = doms[i].getAttribute("data-max");
			if(max != null) {
				var maxText = doms[i].getAttribute("data-maxText");
				if(maxText == null || maxText == "") {
					maxText = "最大长度为" + max + "个字符";
				}
				errMsgDomTmp.innerHTML = maxText;

				if(doms[i].value.length > Number(max)) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//非空长度校验
			var length = doms[i].getAttribute("data-length");
			if(length != null) {
				var lengthText = doms[i].getAttribute("data-lengthText");
				if(lengthText == null || lengthText == "") {
					lengthText = "长度必须为" + max + "个字符";
				}
				errMsgDomTmp.innerHTML = lengthText;

				if(doms[i].value != "" && doms[i].value.length != Number(length)) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//非空包含
			var include = doms[i].getAttribute("data-include");
			if(include != null) {
				var includeText = doms[i].getAttribute("data-includeTe xt");
				if(includeText == null || includeText == "") {
					includeText = "必须包含'" + include + "'";
				}
				errMsgDomTmp.innerHTML = includeText;

				if(doms[i].value != "" && doms[i].value.indexOf(include) == -1) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//id的value是否相同
			var same = doms[i].getAttribute("data-same");
			if(same != null) {
				var sameText = doms[i].getAttribute("data-sameText");
				if(sameText == null || sameText == "") {
					sameText = "值不同";
				}
				errMsgDomTmp.innerHTML = sameText;

				if(doms[i].value != document.getElementById(same).value) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//数字格式
			var num = doms[i].getAttribute("data-num");
			if(num != null && num == "true") {
				var numText = doms[i].getAttribute("data-numText");
				if(numText == null || numText == "") {
					numText = "只能输入数字";
				}
				errMsgDomTmp.innerHTML = numText;

				if(doms[i].value != "" && doms[i].value != null && isNaN(doms[i].value)) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//邮箱格式
			var mail = doms[i].getAttribute("data-mail");
			if(mail != null && mail == "true") {
				var mailText = doms[i].getAttribute("data-mailText");
				if(mailText == null || mailText == "") {
					mailText = "邮箱格式不正确";
				}
				errMsgDomTmp.innerHTML = mailText;

				if(doms[i].value != "" && this.$util_checkMail(doms[i].value) == false) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//手机格式
			var phone = doms[i].getAttribute("data-phone");
			if(phone != null && phone == "true") {
				var phoneText = doms[i].getAttribute("data-phonelText");
				if(phoneText == null || phoneText == "") {
					phoneText = "手机号码格式不正确";
				}
				errMsgDomTmp.innerHTML = phoneText;

				if(doms[i].value != "" && this.$util_checkPhone(doms[i].value) == false) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//手机或邮箱格式
			var mailOrPhone = doms[i].getAttribute("data-mailOrPhone");
			if(mailOrPhone != null && mailOrPhone == "true") {
				var mailOrPhoneText = doms[i].getAttribute("data-mailOrPhoneText");
				if(mailOrPhoneText == null || mailOrPhoneText == "") {
					mailOrPhoneText = "非手机或邮箱格式";
				}
				errMsgDomTmp.innerHTML = mailOrPhoneText;

				if(doms[i].value != "" && this.$util_checkMail(doms[i].value) == false && this.$util_checkPhone(doms[i].value) == false) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}

			//身份证号格式
			var idcard = doms[i].getAttribute("data-idcard");
			if(idcard != null && idcard == "true") {
				var idcardText = doms[i].getAttribute("data-idcardText");
				if(idcardText == null || idcardText == "") {
					idcardText = "身份证号格式不正确";
				}
				errMsgDomTmp.innerHTML = idcardText;

				if(doms[i].value != "" && this.$util_checkIdCard(doms[i].value) == false) {
					errMsgDom.hidden = "hidden";
					errMsgDomTmp.hidden = "";
					return false;
				} else {
					errMsgDom.hidden = "";
					errMsgDomTmp.hidden = "hidden";
				}
			}
		}
	}
	return true;
}

// 移除cookie
function $util_removeCookie(timeout) {
        $.removeCookie('token');
        if(typeof(timeout) == "undefined") {
        	location.href="/src/page/login.html";
        } else {
        	window.setTimeout("location.href='/src/page/login.html';",timeout);
        }
}

// 时间戳转日期
function $util_timestampToTime(timestamp) {
        var date = new Date(timestamp);
        Y = date.getFullYear() + '-';
        M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
        D = date.getDate() > 9 ? date.getDate(): "0" + date.getDate();
        h = date.getHours() > 9 ? date.getHours(): "0" + date.getHours();
        m = date.getMinutes() > 9 ? date.getMinutes(): "0" + date.getMinutes();
        s = date.getSeconds() > 9 ? date.getSeconds(): "0" + date.getSeconds();

        return Y+M+D+' '+h+':'+m+':'+s;
}