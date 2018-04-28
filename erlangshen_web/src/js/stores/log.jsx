var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var LogStore = assign({}, EventEmitter.prototype, {
  data: {
       logs: [],
       total: 1,
       pageSize: 10,
       pageNum: 1,
       info: ""
  },

  page: function (pageNum,username,nickname,mail,phone,clientId) {
    if(pageNum == '' || typeof(pageNum) == "undefined" || pageNum == "undefined") {
      pageNum = 1;
    }

    var url = $setting.serverUrl + 'loginLog?pageSize='+this.data.pageSize+'&pageNum='+pageNum;

    if(typeof(username) != "undefined" && username != "undefined" && username != "") {
      url += "&username="+username;
    }
    if(typeof(nickname) != "undefined" && nickname != "undefined" && nickname != "") {
      url += "&nickname="+nickname;
    }
    if(typeof(mail) != "undefined" && mail != "undefined" && mail != "") {
      url += "&mail="+mail;
    }
    if(typeof(phone) != "undefined" && phone != "undefined" && phone != "") {
      url += "&phone="+phone;
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
              this.data.logs = result.data.dataList;
              this.data.pageNum = pageNum;
              this.data.total = result.data.page.totalCount;
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },
});

module.exports = LogStore;
