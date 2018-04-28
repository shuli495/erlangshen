var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ClientStore = assign({}, EventEmitter.prototype, {
  data: {
	clients: [],
        mails: [],
        phones: [],
        security: {},
      	total: 1,
      	pageSize: 10,
      	pageNum: 1
  },

  page: function (pageNum, name) {
    if(pageNum == '' || typeof(pageNum) == "undefined" || pageNum == "undefined") {
      pageNum = 1;
    }
    if(typeof(name) == "undefined" || name == "undefined") {
      name = "";
    }

    $.ajax({
        type : "GET",
        dataType: "json",
        url: $setting.serverUrl + 'client?pageSize='+this.data.pageSize+'&pageNum='+pageNum+'&name='+name,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
        	this.data.clients = result.data.dataList;
              this.data.pageNum = pageNum;
              this.data.total = result.data.page.totalCount;
    	       this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  list: function (name) {
    if(typeof(name) == "undefined" || name == "undefined") {
      name = "";
    }
    $.ajax({
        type : "GET",
        dataType: "json",
        url: $setting.serverUrl + 'client?name='+name,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              this.data.clients = result.data;
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  add: function (name) {
    var params = {"name": name};

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
        url: $setting.serverUrl + 'client',
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '添加成功！',
                function() {
                  document.getElementById("info_close").click()
                }
              );

             this.page(1);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  update: function (id, name) {
    var params = {};
    if(typeof(name) != "undefined" && name != "undefined") {
      params['name'] = name;
    }

    $.ajax({
        type : "PUT",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(params),
        url: $setting.serverUrl + 'client/'+id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '修改成功！',
                function() {
                  document.getElementById("info_close").click()
                }
              );

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
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + 'client/'+id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '删除成功！',
                function() {
                  document.getElementById("info_close").click()
                }
              );

             this.page(1);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  mailList: function (clientId) {
    $.ajax({
        type : "GET",
        dataType: "json",
        url: $setting.serverUrl + 'client/mail/'+clientId,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              this.data.mails = result.data;
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  mailAdd: function (clientId, mail, username, pwd, smtp, type, subject, text) {
    var params = {"clientId":clientId}
    if(typeof(mail) != "undefined" && mail != "undefined") {
      params['mail'] = mail;
    }
    if(typeof(username) != "undefined" && username != "undefined") {
      params['username'] = username;
    }
    if(typeof(pwd) != "undefined" && pwd != "undefined") {
      params['pwd'] = pwd;
    }
    if(typeof(smtp) != "undefined" && smtp != "undefined") {
      params['smtp'] = smtp;
    }
    if(typeof(type) != "undefined" && type != "undefined") {
      params['type'] = type;
    }
    if(typeof(subject) != "undefined" && subject != "undefined") {
      params['subject'] = subject;
    }
    if(typeof(text) != "undefined" && text != "undefined") {
      params['text'] = text;
    }

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + 'client/mail',
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
             this.mailList(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  mailDel: function (clientId, id) {
    $.ajax({
        type : "DELETE",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + 'client/mail/'+id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '删除成功！',
                function() {
                  document.getElementById("info_close").click()
                }
              );

             this.mailList(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  phoneList: function (clientId) {
    $.ajax({
        type : "GET",
        dataType: "json",
        url: $setting.serverUrl + 'client/phone/'+clientId,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              this.data.phones = result.data;
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  phoneAdd: function (clientId, platform, ak, sk, sign, tmplate, type, text) {
    var params = {"clientId":clientId}
    if(typeof(platform) != "undefined" && platform != "undefined") {
      params['platform'] = platform;
    }
    if(typeof(ak) != "undefined" && ak != "undefined") {
      params['ak'] = ak;
    }
    if(typeof(sk) != "undefined" && sk != "undefined") {
      params['sk'] = sk;
    }
    if(typeof(sk) != "undefined" && sk != "undefined") {
      params['sk'] = sk;
    }
    if(typeof(sign) != "undefined" && sign != "undefined") {
      params['sign'] = sign;
    }
    if(typeof(tmplate) != "undefined" && tmplate != "undefined") {
      params['tmplate'] = tmplate;
    }
    if(typeof(type) != "undefined" && type != "undefined") {
      params['type'] = type;
    }
    if(typeof(text) != "undefined" && text != "undefined") {
      params['text'] = text;
    }

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + 'client/phone',
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
             this.phoneList(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  phoneDel: function (clientId, id) {
    $.ajax({
        type : "DELETE",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + 'client/phone/'+id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '删除成功！',
                function() {
                  document.getElementById("info_close").click()
                }
              );

             this.phoneList(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  security: function (clientId) {
    $.ajax({
        type : "GET",
        dataType: "json",
        url: $setting.serverUrl + 'client/security/'+clientId,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              this.data.security = result.data;
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  securityUpdate: function (clientId, isCheckPlace, checkPlacePriority, checkPlacePhoneTypeId, checkPlaceMailTypeId, isCheckPlatform, checkPlatformType, loginApi) {
    var params = {"clientId":clientId}
    if(typeof(isCheckPlace) != "undefined" && isCheckPlace != "undefined") {
      params['isCheckPlace'] = isCheckPlace;
    }
    if(typeof(checkPlacePriority) != "undefined" && checkPlacePriority != "undefined") {
      params['checkPlacePriority'] = checkPlacePriority;
    }
    if(typeof(checkPlacePhoneTypeId) != "undefined" && checkPlacePhoneTypeId != "undefined") {
      params['checkPlacePhoneTypeId'] = checkPlacePhoneTypeId;
    }
    if(typeof(checkPlaceMailTypeId) != "undefined" && checkPlaceMailTypeId != "undefined") {
      params['checkPlaceMailTypeId'] = checkPlaceMailTypeId;
    }
    if(typeof(isCheckPlatform) != "undefined" && isCheckPlatform != "undefined") {
      params['isCheckPlatform'] = isCheckPlatform;
    }
    if(typeof(checkPlatformType) != "undefined" && checkPlatformType != "undefined") {
      params['checkPlatformType'] = checkPlatformType;
    }
    if(typeof(loginApi) != "undefined" && loginApi != "undefined") {
      params['loginApi'] = loginApi;
    }

    $.ajax({
        type : "PUT",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + 'client/security/'+clientId,
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
              $util_alertMsg('', '修改成功！',
                function() {
                  document.getElementById("info_close").click()
                }
              );
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

});

module.exports = ClientStore;
