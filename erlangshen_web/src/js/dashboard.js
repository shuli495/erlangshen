import SelfApp from 'components/self.jsx';
import ClientApp from 'components/client.jsx';
import UserApp from 'components/user.jsx';
import MenuApp from 'components/menu.jsx';
import PermissionApp from 'components/permission.jsx';
import LogApp from 'components/log.jsx';
import KeyApp from 'components/key.jsx';
import ApiApp from 'components/api.jsx';

window.onload=function(){
	if(typeof($.cookie('token')) == "undefined") {
		$util_alertStr("token失效，请重新登录！");
	}
}

ReactDOM.render(<ClientApp />, document.getElementById('main'));

$(".navigation").find('a').on('click',function() {
	var type = $(this).attr('data-type')
	$(this).addClass('title_clicked');
	$(this).parent().siblings().children().removeClass('title_clicked');

	if(type == 'self') {
		ReactDOM.render(<SelfApp />, document.getElementById('main'));
	} else if(type == 'client') {
		ReactDOM.render(<ClientApp />, document.getElementById('main'));
	} else if(type == 'user') {
		ReactDOM.render(<UserApp />, document.getElementById('main'));
	} else if(type == 'menu') {
		ReactDOM.render(<MenuApp />, document.getElementById('main'));
	} else if(type == 'permission') {
		ReactDOM.render(<PermissionApp />, document.getElementById('main'));
	} else if(type == 'log') {
		ReactDOM.render(<LogApp />, document.getElementById('main'));
	} else if(type == 'key') {
		ReactDOM.render(<KeyApp />, document.getElementById('main'));
	} else if(type == 'api') {
		ReactDOM.render(<ApiApp />, document.getElementById('main'));
	}
})