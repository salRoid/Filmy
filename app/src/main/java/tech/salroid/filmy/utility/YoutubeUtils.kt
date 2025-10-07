package tech.salroid.filmy.utility


/**
 * Generates the HTML string required to embed a YouTube video using an iframe.
 *
 * @param youtubeVideoId The unique identifier of the YouTube video.
 * @return A string containing the complete HTML to embed the YouTube video.
 */
fun getYouTubeIframeHTML(youtubeVideoId: String): String {
    return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0px;padding:0px; background-color:black; overflow:hidden;">
                <div id="player" style="position:absolute; top:0; left:0; width:100%; height:100%;"></div>
                <script>
                    var player;
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$youtubeVideoId',
                            playerVars: {
                                'autoplay': 1,
                                'controls': 1, 
                                'fs': 1,
                                'rel': 0,
                                'modestbranding': 1,
                                'iv_load_policy': 3, 
                                'showinfo': 0,      
                                'playsinline': 1
                            },
                            events: {
                                'onReady': function(event) { event.target.playVideo(); }
                            }
                        });
                    }
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
                </script>
            </body>
            </html>
        """.trimIndent()
}