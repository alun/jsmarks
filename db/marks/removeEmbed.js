function log(str) {
  if (console && console.log) {
    console.log(str);
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
  embed.parentNode.removeChild(embed);
} else {
  log("No embed with num: " + embedNum);
  log("Embeds on page: " + embeds);
}
