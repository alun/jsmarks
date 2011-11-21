var iframe = document.getElementsByTagName('iframe')[0];
var idoc = iframe.contentWindow.document;
var embed = idoc.getElementsByTagName('embed')[0];
var src = embed.getAttribute('src');
var idx = src.indexOf('?');

if (idx > 0) {
	src = newSrc + src.substring(idx);
} else {
	src = newSrc;
}

embed.setAttribute('src', src);

var parent = embed.parentNode;
parent.removeChild(embed);
parent.appendChild(embed);

var context;
var fv = embed.getAttribute('flashVars');
var kvs = fv.split("&");
for (var i = 0; i < kvs.length; ++i) {
	var kv = kvs[i];
	kv = kv.split("=");
	var key = kv[0];
	var value = kv[1];
	if (key == "context") {
		context = value;
		break;
	}
}

window.oggiContext = function() {
	return eval("window.frames[0]." + context);
}
