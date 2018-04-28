var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var KeyStore = assign({}, EventEmitter.prototype, {
  data: {
	keys: [],
       total: 1,
       pageSize: 10,
       pageNum: 1
  },

  page: function (pageNum,clientId) {
    if(pageNum == '' || typeof(pageNum) == "undefined" || pageNum == "undefined") {
      pageNum = 1;
    }

    var url = $setting.serverUrl + 'key?pageSize='+this.data.pageSize+'&pageNum='+pageNum;

    if(typeof(clientId) != "undefined" && clientId != "undefined" && clientId != "") {
      url += "&clientId="+clientId;
    }

    $.ajax({
        type : "GET",
        dataType: "json",
        url: url,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              this.data.keys = result.data.dataList;
              this.data.pageNum = pageNum;
              this.data.total = result.data.page.totalCount;
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  add: function (clientId) {
    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "key/" + clientId,
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

  del: function (id) {
    $.ajax({
        type : "DELETE",
        dataType: "json",
        url: $setting.serverUrl + "key/" + id,
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

  start: function (id) {
    var params = {"id":id};

    $.ajax({
        type : "PUT",
        dataType: "json",
        url: $setting.serverUrl + "key/start/"+id,
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
              $util_alertMsg('', '启用成功！',
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

  stop: function (id) {
    var params = {"id":id};

    $.ajax({
        type : "PUT",
        dataType: "json",
        url: $setting.serverUrl + "key/stop/"+id,
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
              $util_alertMsg('', '停用成功！',
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
  }

});

module.exports = KeyStore;
