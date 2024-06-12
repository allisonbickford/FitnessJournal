package com.catscoffeeandkitchen.exercises.youtube_player

import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import timber.log.Timber

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun YouTubePlayer(
    videoId: String,
    width: Int,
    isVolumeOn: Boolean
) {
    val colorHexFormat = HexFormat {
        upperCase = false
        number.removeLeadingZeros = true
    }
    val backgroundColor = (MaterialTheme.colorScheme.surfaceContainerLow
        .toArgb() and 0xFFFFFF)
        .toHexString(colorHexFormat)

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.setSupportZoom(false)

                val assetLoader = WebViewAssetLoader.Builder()
                    .addPathHandler("/assets/",
                        WebViewAssetLoader.AssetsPathHandler(context)
                    )
                    .build()
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        if (request?.url == null) {
                            return null
                        }
                        return assetLoader.shouldInterceptRequest(request.url)
                    }
                }

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        update = {
            Timber.d("Setting width=${width}, color=${backgroundColor}")
            it.loadUrl(
                "https://appassets.androidplatform.net/assets/index.html" +
                        "?videoId=${videoId}&width=${width}&color=${backgroundColor}" +
                        if (isVolumeOn) "" else "&muted=1"
            )
        }
    )
}
