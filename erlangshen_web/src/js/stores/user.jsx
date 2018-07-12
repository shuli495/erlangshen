var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var UserStore = assign({}, EventEmitter.prototype, {
  data: {
	users: [],
       total: 1,
       pageSize: 10,
       pageNum: 1,
       retrieveId: "",
       info: "",
       codeImage: "",
       idcardImage: ""
  },

  code: function (type) {
    $.ajax({
        type : "GET",
        dataType: "json",
        url: $setting.serverUrl + "code?type="+type,
        headers : {"Token": $setting.token},
        success: function(result) {
          this.data.codeImage = result.data;
          this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
          var errMsg = $util_getMsg(xhr);
          this.data.info = errMsg;
          this.emit('change');
        }.bind(this)
    });
  },

  login: function (userName, pwd, verifyCode) {
      var data = {
          "userName" : userName,
          "pwd" : pwd
      };

    if(typeof(verifyCode) != "undefined" && verifyCode != "undefined" && verifyCode != "") {
      data["verifyCode"] = verifyCode;
    }

    $.ajax({
        data: data,
        type : "POST",
        dataType: "json",
        url: $setting.serverUrl + "token",
        headers : {"Token": $setting.token},
        success: function(result) {
          this.data.codeImage = "";
          var resultToken = result.data;
          var date = new Date();
          date.setTime(resultToken.activeTime);
          $.cookie('token', resultToken.id, { expires: date });
          location.href="/src/page/dashboard.html";
        }.bind(this),
        error: function(xhr, type, exception) {
          var errMsg = $util_getMsg(xhr);

          if(eval('('+xhr.responseText+')').image) {
            this.data.codeImage = eval('('+xhr.responseText+')').image;
          }

          this.data.info = errMsg;
          this.emit('change');
        }.bind(this)
    });
  },

  register: function(userName, pwd, code, verifyCode) {
    var params = {"checkMail" : true};

    if(typeof(userName) != "undefined" && userName != "undefined" && userName != "") {
      params["mail"] = userName;
    }
    if(typeof(pwd) != "undefined" && pwd != "undefined" && pwd != "") {
      params["pwd"] = pwd;
    }
    if(typeof(code) != "undefined" && code != "undefined" && code != "") {
      params["code"] = code;
    }
    if(typeof(verifyCode) != "undefined" && verifyCode != "undefined" && verifyCode != "") {
      params["verifyCode"] = verifyCode;
    }

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
        url: $setting.serverUrl + "user",
        headers : {"Token": $setting.token},
        success: function(result) {
            this.data.codeImage = "";
            $util_alertMsg('', '注册成功！',
              function() {
                window.setTimeout("location.href='/src/page/login.html';",2000);
              }
            );
        }.bind(this),
        error: function(xhr, type, exception) {
          var errMsg = $util_getMsg(xhr);

          if(eval('('+xhr.responseText+')').image) {
            this.data.codeImage = eval('('+xhr.responseText+')').image;
          }

          this.data.info = errMsg;
          this.emit('change');
        }.bind(this)
    });
  },

  retrieveChkUser(type, mail, phone, isCheckUserExist) {
    var text = "手机";
    var url = $setting.serverUrl + 'user?';
    if(typeof(mail) != "undefined" && mail != "undefined" && mail != "") {
      text = "邮箱"
      url += "mail=" + mail;
    } else {
      url += "phone=" + phone;
    }

    $.ajax({
        type : "GET",
        dataType: "json",
        url: url,
        headers : {"Token": $setting.token},
        success: function(result) {
              var users = result.data;

              if(type == "retrieve" && users.length == 0) {
                this.data.retrieveId = "";
                this.data.info = text + "未注册，请注册！";
                this.emit('change');
                return;
              } else if(type == "register" && users.length != 0) {
                this.data.info = "该"+text+"已注册";
                this.emit('change');
                return;
              } else {
                this.data.info = "";
                if(users.length > 0) {
                 this.data.retrieveId = users[0].id;
                }
              }

              if(text == "手机") {
                this.sendPhone(type, phone,"",isCheckUserExist);
              } else {
                this.sendMail(type, mail,"",isCheckUserExist);
              }
        }.bind(this),
        error: function(xhr, type, exception) {
          this.data.info = $util_getMsg(xhr);
          this.emit('change');
        }.bind(this)
    });
  },

  retrieve(id, code, pwd) {
    var params = {"id" : id, "code" : code, "pwd": pwd};

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
        url: $setting.serverUrl + "user/rePwd",
        headers : {"Token": $setting.token},
        success: function(result) {
              $util_alertMsg('', '修改成功，请登录！',
                function() {
                  $util_removeCookie(2000);
                }
              );
        }.bind(this),
        error: function(xhr, type, exception) {
              this.data.info = $util_getMsg(xhr);
              this.emit('change');
        }.bind(this)
    });
  },

  repwd(id, oldPwd, pwd) {
    var params = {"oldPwd" : oldPwd, "pwd": pwd};

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
        url: $setting.serverUrl + "user/" + id + "/rePwd",
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '修改成功，请重新登录！',
                function() {
                  $util_removeCookie(2000);
                }
              );
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 发送验证码
  sendMail(type, mail, userId, isCheckUserExist) {
    var token = $setting.token;
    if(typeof($.cookie('token')) != 'undefined') {
      token = $.cookie('token');
    }

    var url = $setting.serverUrl + 'user/sendMail?isCheckUserExist='+isCheckUserExist+'&type=' + type;
    if(mail && mail != "") {
      url += "&mail=" + mail;
    }
    if(userId && userId != "") {
      url += "&userId=" + userId;
    }

    $.ajax({
        type : "GET",
        dataType: "json",
        url: url,
        headers : {"Token": token},
        success: function(result) {
                this.data.info = "";
                $util_alertStr("发送成功！");
                this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  // 发送验证码
  sendPhone(type, phone, userId, isCheckUserExist) {
    var token = $setting.token;
    if(typeof($.cookie('token')) != 'undefined') {
      token = $.cookie('token');
    }

    var url = $setting.serverUrl + 'user/sendPhone?isCheckUserExist='+isCheckUserExist+'&type=' + type;
    if(phone && phone != "") {
      url += "&phone=" + phone;
    }
    if(userId && userId != "") {
      url += "&userId=" + userId;
    }

    $.ajax({
        type : "GET",
        dataType: "json",
        url: url,
        headers : {"Token": token},
        success: function(result) {
                this.data.info = "";
                $util_alertStr("发送成功！");
                this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  query: function (pageNum,source,username,nickname,mail,mailVerify,phone,phoneVerify,tel,qq,weixin,weibo,name,idcard,schCertification,sex,status,province,city,area,address,clientId) {
    var url = $setting.serverUrl + 'user?';

    if(pageNum != '' && typeof(pageNum) != "undefined" && pageNum != "undefined") {
      url += "&pageSize="+this.data.pageSize+'&pageNum='+pageNum;
    }
    if(typeof(source) != "undefined" && source != "undefined" && source != "") {
      url += "&source="+source;
    }
    if(typeof(username) != "undefined" && username != "undefined" && username != "") {
      url += "&username="+username;
    }
    if(typeof(nickname) != "undefined" && nickname != "undefined" && nickname != "") {
      url += "&nickname="+nickname;
    }
    if(typeof(mail) != "undefined" && mail != "undefined" && mail != "") {
      url += "&mail="+mail;
    }
    if(typeof(mailVerify) != "undefined" && mailVerify != "undefined" && mailVerify != "") {
      url += "&mailVerify="+mailVerify;
    }
    if(typeof(phone) != "undefined" && phone != "undefined" && phone != "") {
      url += "&phone="+phone;
    }
    if(typeof(phoneVerify) != "undefined" && phoneVerify != "undefined" && phoneVerify != "") {
      url += "&phoneVerify="+phoneVerify;
    }
    if(typeof(tel) != "undefined" && tel != "undefined" && tel != "") {
      url += "&tel="+tel;
    }
    if(typeof(qq) != "undefined" && qq != "undefined" && qq != "") {
      url += "&qq="+qq;
    }
    if(typeof(weixin) != "undefined" && weixin != "undefined" && weixin != "") {
      url += "&weixin="+weixin;
    }
    if(typeof(weibo) != "undefined" && weibo != "undefined" && weibo != "") {
      url += "&weibo="+weibo;
    }
    if(typeof(name) != "undefined" && name != "undefined" && name != "") {
      url += "&name="+name;
    }
    if(typeof(idcard) != "undefined" && idcard != "undefined" && idcard != "") {
      url += "&idcard="+idcard;
    }
    if(typeof(schCertification) != "undefined" && schCertification != "undefined" && schCertification != "") {
      url += "&certification="+schCertification;
    }
    if(typeof(sex) != "undefined" && sex != "undefined" && sex != "") {
      url += "&sex="+sex;
    }
    if(typeof(status) != "undefined" && status != "undefined" && status != "") {
      url += "&status="+status;
    }
    if(typeof(province) != "undefined" && province != "undefined" && province != "") {
      url += "&province="+province;
    }
    if(typeof(city) != "undefined" && city != "undefined" && city != "") {
      url += "&city="+city;
    }
    if(typeof(area) != "undefined" && area != "undefined" && area != "") {
      url += "&area="+area;
    }
    if(typeof(address) != "undefined" && address != "undefined" && address != "") {
      url += "&address="+address;
    }
    if(typeof(clientId) != "undefined" && clientId != "undefined" && clientId != "") {
      url += "&clientId="+clientId;
    }

    $.ajax({
        type : "GET",
        dataType: "json",
        url: url,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              if(pageNum != '' && typeof(pageNum) != "undefined" && pageNum != "undefined") {
                this.data.users = result.data.dataList;

                this.data.pageNum = pageNum;
                this.data.total = result.data.page.totalCount;
              } else {
                this.data.users = result.data;
              }
              this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  page: function (pageNum,source,username,nickname,mail,mailVerify,phone,phoneVerify,tel,qq,weixin,weibo,name,idcard,schCertification,sex,status,province,city,area,address,clientId) {
    if(pageNum == '' || typeof(pageNum) == "undefined" || pageNum == "undefined") {
      pageNum = 1;
    }

    this.query(pageNum,source,username,nickname,mail,mailVerify,phone,phoneVerify,tel,qq,weixin,weibo,name,idcard,schCertification,sex,status,province,city,area,address,clientId);
  },

  list: function (source,username,nickname,mail,mailVerify,phone,phoneVerify,tel,qq,weixin,weibo,name,idcard,schCertification,sex,status,province,city,area,address,clientId) {
    this.query("",source,username,nickname,mail,mailVerify,phone,phoneVerify,tel,qq,weixin,weibo,name,idcard,schCertification,sex,status,province,city,area,address,clientId);
  },

  add: function (source,username,pwd,nickname,mail,phone,tel,qq,weixin,weibo,name,idcard,sex,clientId,province,city,area,address,status) {
    var params = {"checkMail" : true};

    if(typeof(source) != "undefined" && source != "undefined" && source != "") {
      params["source"] = source;
    }
    if(typeof(username) != "undefined" && username != "undefined" && username != "") {
      params["username"] = username;
    }
    if(typeof(pwd) != "undefined" && pwd != "undefined" && pwd != "") {
      params["pwd"] = pwd;
    }
    if(typeof(nickname) != "undefined" && nickname != "undefined" && nickname != "") {
      params["nickname"] = nickname;
    }
    if(typeof(mail) != "undefined" && mail != "undefined" && mail != "") {
      params["mail"] = mail;
    }
    if(typeof(phone) != "undefined" && phone != "undefined" && phone != "") {
      params["phone"] = phone;
    }
    if(typeof(tel) != "undefined" && tel != "undefined" && tel != "") {
      params["tel"] = tel;
    }
    if(typeof(qq) != "undefined" && qq != "undefined" && qq != "") {
      params["qq"] = qq;
    }
    if(typeof(weixin) != "undefined" && weixin != "undefined" && weixin != "") {
      params["weixin"] = weixin;
    }
    if(typeof(weibo) != "undefined" && weibo != "undefined" && weibo != "") {
      params["weibo"] = weibo;
    }
    if(typeof(name) != "undefined" && name != "undefined" && name != "") {
      params["name"] = name;
    }
    if(typeof(idcard) != "undefined" && idcard != "undefined" && idcard != "") {
      params["idcard"] = idcard;
    }
    if(typeof(sex) != "undefined" && sex != "undefined" && sex != "") {
      params["sex"] = sex;
    }
    if(typeof(province) != "undefined" && province != "undefined" && province != "") {
      params["province"] = province;
    }
    if(typeof(city) != "undefined" && city != "undefined" && city != "") {
      params["city"] = city;
    }
    if(typeof(area) != "undefined" && area != "undefined" && area != "") {
      params["area"] = area;
    }
    if(typeof(address) != "undefined" && address != "undefined" && address != "") {
      params["address"] = address;
    }
    if(typeof(clientId) != "undefined" && clientId != "undefined" && clientId != "") {
      params["clientId"] = clientId;
    }
    if(typeof(status) != "undefined" && status != "undefined" && status != "") {
      params["status"] = status;
    }

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
        url: $setting.serverUrl + "user",
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '添加成功！');

             this.page(1);
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  update: function (id,source,username,nickname,mail,phone,tel,qq,weixin,weibo,name,idcard,sex,clientId,province,city,area,address,status) {
    var params = {"checkMail" : true};

    if(typeof(source) != "undefined" && source != "undefined") {
      params["source"] = source;
    }
    if(typeof(username) != "undefined" && username != "undefined") {
      params["username"] = username;
    }
    if(typeof(nickname) != "undefined" && nickname != "undefined") {
      params["nickname"] = nickname;
    }
    if(typeof(mail) != "undefined" && mail != "undefined") {
      params["mail"] = mail;
    }
    if(typeof(phone) != "undefined" && phone != "undefined") {
      params["phone"] = phone;
    }
    if(typeof(tel) != "undefined" && tel != "undefined") {
      params["tel"] = tel;
    }
    if(typeof(qq) != "undefined" && qq != "undefined") {
      params["qq"] = qq;
    }
    if(typeof(weixin) != "undefined" && weixin != "undefined") {
      params["weixin"] = weixin;
    }
    if(typeof(weibo) != "undefined" && weibo != "undefined") {
      params["weibo"] = weibo;
    }
    if(typeof(name) != "undefined" && name != "undefined") {
      params["name"] = name;
    }
    if(typeof(idcard) != "undefined" && idcard != "undefined") {
      params["idcard"] = idcard;
    }
    if(typeof(sex) != "undefined" && sex != "undefined") {
      params["sex"] = sex;
    }
    if(typeof(province) != "undefined" && province != "undefined") {
      params["province"] = province;
    }
    if(typeof(city) != "undefined" && city != "undefined") {
      params["city"] = city;
    }
    if(typeof(area) != "undefined" && area != "undefined") {
      params["area"] = area;
    }
    if(typeof(address) != "undefined" && address != "undefined") {
      params["address"] = address;
    }
    if(typeof(clientId) != "undefined" && clientId != "undefined" && clientId != "") {
      params["clientId"] = clientId;
    }
    if(typeof(status) != "undefined" && status != "undefined" && status != "") {
      params["status"] = status;
    }

    $.ajax({
        type : "PUT",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
        url: $setting.serverUrl + "user/" + id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '修改成功！');

             this.page(1);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  del: function (id) {
    $.ajax({
        type : "DELETE",
        dataType: "json",
        url: $setting.serverUrl + "user/" + id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '删除成功！');

             this.page(1);
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  findCertification: function(userId) {
     $.ajax({
          url: $setting.serverUrl + "user/" + userId + "/certification",
          type: 'GET',
          headers : {"Token": $.cookie('token')},
          success: function (result) {
              this.data.idcardImage = result.data;
              this.emit('change');
          }.bind(this),
          error: function (xhr, type, exception) {
              $util_alertMsg(xhr);
          }.bind(this)
     });
  },

  uploadIdcard: function(formId, userId) {
     var formData = new FormData(document.getElementById(formId));

     $.ajax({
          url: $setting.serverUrl + "user/" + userId + "/certification",
          type: 'POST',
          headers : {"Token": $.cookie('token')},
          data: formData,
          async: false,
          cache: false,
          contentType: false,
          processData: false,
          success: function (result) {
              $util_alertMsg('', '上传成功，请稍后查看！',
                function() {
                  document.getElementById("certification_close").click()
                });
          }.bind(this),
          error: function (xhr, type, exception) {
              $util_alertMsg(xhr);
          }.bind(this)
     });
  },

  confirmCertification: function(userId, certification) {
    var params = {"certification": certification};

    $.ajax({
        type : "PUT",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
          url: $setting.serverUrl + "user/" + userId + "/certification",
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '修改成功！',
                function() {
                  document.getElementById("certification_close").click()
                });

             this.page(1);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
      });
  },

});

module.exports = UserStore;
