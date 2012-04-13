function log(obj) {
  if (console && console.log) {
    console.log(obj);
  }
}

try {
  embedNum = parseInt(embedNum);
} catch(e) {
  log("Bad embedNum value");
  return;
}

var embeds = document.getElementsByTagName('embed'),
    embed = embeds[embedNum];

if (embed) {
  var tumriFilter = function(e) {
      return e.indexOf("tumriObject") == 0;
    },
    flashvars = embed.getAttribute("flashvars") || "",
    tumriObj = eval(flashvars.split("&").filter(tumriFilter)[0]);

  window.tumri = tumriObj;
} else {
  log("No embed with num: " + embedNum);
  log("Embeds on page: ");
  log(embeds);
}
