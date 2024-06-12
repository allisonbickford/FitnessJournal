// Load the IFrame Player API code asynchronously.
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/player_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

// Replace the 'ytplayer' element with an <iframe> and
// YouTube player after the API code downloads.
var player;
function onYouTubePlayerAPIReady() {
    var urlParams = new URLSearchParams(window.location.search);
    var videoId = urlParams.get('videoId');
    var width = urlParams.get('width');
    var color = urlParams.get('color');
    var muted = urlParams.get('muted');

    document.body.style.backgroundColor = "#" + color;

    player = new YT.Player('ytplayer', {
        videoId: videoId,
        height: (9 / 16) * width,
        width: width,
        playerVars: {
            'autoplay': 1
        },
        events: {
            'onReady': onPlayerReady
        }
    });

    if (muted == 1) {
        player.setVolume(0);
    } else {
        player.setVolume(80);
    }
}

function onPlayerReady(event) {
    event.target.setVolume(0);
}

