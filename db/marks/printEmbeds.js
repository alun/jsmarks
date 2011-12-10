function log(str) {
  if (console && console.log) {
    console.log(str);
  }
}

function stars(text) {
  var s = "***************";
  return [s, text, s].join(" ");
}

log(stars("Begin embeds"));

for(
  j = 0,
  embeds = document.getElementsByTagName("embed"),
  embed = embeds[j],
  totalEmbeds = embeds.length;  
  j < totalEmbeds; ++j) {

  log(embed.src.split("?")[0]);
}

log(stars("End embeds"));
