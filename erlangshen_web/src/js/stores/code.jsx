var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var CodeStore = assign({}, EventEmitter.prototype, {
  data: {
	codes: []
  },
  list: function (parentId, groupId) {
    if(typeof(parentId) == "undefined" || parentId == "undefined") {
      parentId = "";
    }
    if(typeof(groupId) == "undefined" || groupId == "undefined") {
      groupId = "";
    }

    $.ajax({
        type : "GET",
        dataType: "json",
        url: $setting.serverUrl + 'code?parentId='+parentId+'&groupId='+groupId,
        headers : {"Token": $.cookie('token')},
        success: function(result) {
             if(groupId != "") {
                this.data.codes[groupId] = result.data;
             } else {
                this.data.codes = result.data;
             }
             this.emit('change');
        }.bind(this),
        error: function(xhr, type, exception) {
                $util_alertMsg(xhr);
        }.bind(this)
    });
  }
});

module.exports = CodeStore;
