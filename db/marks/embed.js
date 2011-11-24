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

try {
  embedNum = parseInt(embedNum)
} catch(e) {
  log("Bad embedNum value");
  return
}

var embeds = document.getElementsByTagName('embed'),
    embed = embeds[embedNum];

if (embed) {
  var oldSrc = embed.getAttribute('src'),
      parts = oldSrc.split("?"),
      newSrc = newBaseUrl + "?" + parts[1];

  embed.setAttribute('src', newSrc);
  refresh(embed);
} else {
  log("No embed with num: " + embedNum);
  log("Embeds on page: " + embeds);
}
