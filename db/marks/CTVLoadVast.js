function refresh(elem) {
  var p = elem.parentNode;
  p.removeChild(elem);
  p.appendChild(elem);
}

function log(str) {
  if (console && console.log) {
    console.log(str);
  }
}

var targetEmbed = null;

// Find the CTV embed
for (
  var embeds = document.getElementsByTagName('embed'),
      total = embeds.length,
      idx = 0;
  idx < total; ++idx) {

  var embed = embeds[idx],
      url = embed.src.split("?")[0];

  // Not a perfect way, but working
  if (url.indexOf("-") != -1) {
    targetEmbed = embed;
    break;
  }
}

if (!embed) {
  log("No embed found");
  return;
}

var flashVarsStr = embed.getAttribute("flashvars"),
    flashVarsEntries = flashVarsStr.split("&"),
    context = null;

// Find unit context
for (
  var idx = 0,
      total = flashVarsEntries.length;
  idx < total; ++idx) {
  
  var entry = flashVarsEntries[idx],
      keyValuePair = entry.split("="),
      key = keyValuePair[0],
      value = keyValuePair[1];

  if (key == "context") {
    context = eval(value);
    break;
  }
}

if (!context) {
  log("No context found");
  return;
}

var gExternal = context.gExternal;
gExternal.adEngine1 = vastUrl;
gExternal.adEngineName1 = "JSMarks injected engine";
if (overlay == "1") {
  gExternal.adType = "overlay";
}

refresh(embed);
