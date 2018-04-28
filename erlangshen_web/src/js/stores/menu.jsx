var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var MenuStore = assign({}, EventEmitter.prototype, {
  data: {
       menus: []
  },

  list: function (clientId) {
    var url = $setting.serverUrl + 'permissionMenu?clientId=' + clientId;

    $.ajax({
        type : "GET",
        dataType: "json",
        url: url,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              this.data.menus = result.data;
              this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  add: function (clientId, parentId, name, url, tag, type) {
    var params = {"clientId":clientId, "parentId":parentId, "name":name, "url":url, "tag":tag, "type":type};

    $.ajax({
        type : "POST",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionMenu",
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
              $util_alertMsg('', '添加成功！');

             this.list(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  update: function (clientId,id, name, url, tag) {
    var params = {"id":id, "name":name, "url":url, "tag":tag};

    $.ajax({
        type : "PUT",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionMenu/"+id,
        headers : {"Token": $.cookie('token')},
        data: JSON.stringify(params),
        success: function(result) {
              $util_alertMsg('', '修改成功！');

             this.list(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  },

  del: function (clientId, id) {
    $.ajax({
        type : "DELETE",
        contentType: "application/json; charset=utf-8",
        url: $setting.serverUrl + "permissionMenu/" + id,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
              $util_alertMsg('', '删除成功！');

             this.list(clientId);
        }.bind(this),
        error: function(xhr, type, exception) {
              $util_alertMsg(xhr);
        }.bind(this)
    });
  }

});

module.exports = MenuStore;
