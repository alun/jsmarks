function log(str) {
  if (console && console.log) {
    console.log(str);
  }
}

function createParamsMap(paramsArray) {
  var map = {};
  for (i = 0; i < paramsArray.length; ++i) {
    var paramString = paramsArray[i],
      idx = paramString.indexOf("="),
      key = paramString.substr(0, idx),
      value = paramString.substr(idx + 1);

    map[key] = value;
  }
  return map;
}

function createParamsArray(paramsMap) {
  var arr = [];
  for (k in paramsMap) {
    arr.push([k, paramsMap[k]].join("="));
  }
  return arr;
}

String.prototype.replaceAll = function(search, replace){
  return this.split(search).join(replace);
}
String.prototype.customUnescape = function() {
  return this.replaceAll("\x253f", "?").replaceAll("\x2526", "&");
}
String.prototype.customEscape = function() {
  return this.replaceAll("?", "\x253f").replaceAll("&", "\x2526");
}

var curLocation = window.location.href,
  urlParts = curLocation.split("?"),
  query = urlParts[1],
  params = createParamsMap(query.split("&")),
  prerollUrl = params.urlAd.customUnescape(),
  modifiedUrl = [newBaseUrl, prerollUrl.split("?")[1]].join("?");

params.urlAd = modifiedUrl.customEscape();

var newQuery = createParamsArray(params).join("&"),
  newLocation = [urlParts[0], newQuery].join("?");

window.location.href = newLocation;
