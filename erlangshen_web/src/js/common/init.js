// cdn
var cs = [
	'https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css',
	'https://cdn.bootcss.com/Buttons/2.0.0/css/buttons.min.css',
	'https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css',
	'/src/css/common/default.css'
	]
var js = [
	'/build/common.js',
	'/src/js/common/setting.js',
	'/src/js/common/util.js',
	'https://cdn.bootcss.com/react/15.4.1/react.min.js',
	'https://cdn.bootcss.com/react/15.4.1/react-dom.min.js',
	'https://cdn.bootcss.com/jquery/3.1.1/jquery.min.js',
	'https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js',
	'https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js'
	]

loadCs();
loadScript();

// 初始化cdn cs
function loadCs() {
	try {
		var head = document.getElementsByTagName('head')[0];
		var style = document.createElement('link');
		style.rel = 'stylesheet';
		style.type = 'text/css';
		style.href = cs[0];
		head.appendChild(style);

		cs.splice(0,1);
		if(cs.length != 0) {
			loadCs();
		} else {
			dynamicCs();
		}
	} catch(err) {
		// 加载cs失败，跳过当前cs加载，继续加载下一个
		console.error(err);
	}
}

// 初始化cdn js
function loadScript() {
	try {
		var head= document.getElementsByTagName('head')[0];
		var script= document.createElement('script');
		script.src=js[0];

		// 异步加载，加载成功后的同步方法
		script.onreadystatechange= function () {
			loadScriptOver();
		}
		script.onload= function(){
			loadScriptOver();
		}

		head.appendChild(script);
	} catch(err) {
		// 加载js失败，跳过当前js加载，继续加载下一个
		console.error(err);
	}
}

// 加载js成功后执行
function loadScriptOver() {
	js.splice(0,1);
	if(js.length != 0) {
		// 加载下个js
		loadScript(js[0]);
	} else {
		// 加载当前html自身的js、css
		dynamicJs();
	}
}

// 如果body设置class，加载当前html自身的css
function dynamicCs() {
	if(document.body.className) {
		// body第一个className
		var thisClassName = document.body.className.split(' ')[0];
		var head = document.getElementsByTagName('head')[0];

		// 加载css下与第一个class同名的css文件
		try {
			var style = document.createElement('link');
			style.rel = 'stylesheet';
			style.type = 'text/css';
			style.href = '/src/css/' + thisClassName + '.css';
			head.appendChild(style);
		} catch(err) {
			// 跳过失败项
			console.error(err);
		}
	}
}

// 如果body设置class，加载当前html自身的js
function dynamicJs() {
	if(document.body.className) {
		// body第一个className
		var thisClassName = document.body.className.split(' ')[0];
		var head = document.getElementsByTagName('head')[0];

		// 加载build下与第一个class同名的js文件
		try {
			var script = document.createElement('script');
			script.src = '/build/' + thisClassName + '.js';
			head.appendChild(script);
		} catch(err) {
			// 跳过失败项
			console.error(err);
		}
	}
}