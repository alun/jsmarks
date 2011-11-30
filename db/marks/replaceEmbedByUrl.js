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

var embeds = document.getElementsByTagName('embed'),
  embed = null,
  i = 0;

for (i = 0; i < embeds.length; ++i) {
  var candidate = embeds[i];
  if (candidate.getAttribute("src").indexOf(oldBaseUrl) === 0) {
    embed = candidate;
    break;
  }
}

if (embed) {
  var oldSrc = embed.getAttribute('src'),
    parts = oldSrc.split("?"),
    newSrc = newBaseUrl + "?" + parts[1];

  embed.setAttribute('src', newSrc);
  refresh(embed);
} else {
  log("No embed with base url: " + oldBaseUrl);
  
  var legalBaseUrls = [];
  for (i = 0; i < embeds.length; ++i) {
    var embed = embeds[i],
      src = embed.getAttribute("src"),
      parts = src.split("?");

    legalBaseUrls.push(parts[0]);
  }
  log("Embeds on page have base urls: " + legalBaseUrls);
}
