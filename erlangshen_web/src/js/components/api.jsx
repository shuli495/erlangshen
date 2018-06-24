class ApiApp extends React.Component {

	setHeight() {
		var ifm= document.getElementById("iframepage");
		ifm.height =document.body.scrollHeight;
	}

    render(){
        { }

        return (
        	<div>
	            <iframe id="iframepage" src="api_doc.html" onLoad={this.setHeight}></iframe>
            </div>
        );
    }
}

module.exports = ApiApp;