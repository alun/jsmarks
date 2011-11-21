var embed = document.getElementsByTagName("embed")[1];

if (ctvSwfSrc && ctvSwfSrc != "") {
	var src = embed.getAttribute("src");
	var idx = src.indexOf("?");
	if (idx > 0){
		src = ctvSwfSrc + src.substring(idx)
	} else {
		src = ctvSwfSrc
	}
	embed.setAttribute("src",src);
}

var fv = embed.getAttribute("flashvars");
var kvs = fv.split("&");
var context;

for (var i = 0; i < kvs.length; ++i) {
	var kv = kvs[i];
	kv = kv.split("=");
	var key = kv[0];
	var value = kv[1];
	if (key == "context") {
		context = eval(value)
		break;
	}
}

var ext = context.gExternal;
if (!ext) {
	context.gExternal = ext = {};
}
ext.adEngine1 = vastXML;
ext.adEngineName1 = "test";
if (useOverlay) {
	ext.adType = "overlay";
}

var parent = embed.parentNode;
parent.removeChild(embed);
parent.appendChild(embed)
