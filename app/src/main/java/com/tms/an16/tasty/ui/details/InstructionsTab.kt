package com.tms.an16.tasty.ui.details

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun InstructionsTab(
    sourceUrl: String,
    onWebViewCreated: (WebView) -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(sourceUrl)
                onWebViewCreated(this)
            }
        }
    )
}

fun printPdf(context: Context, webView: WebView?, jobName: String) {
    webView?.let {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = it.createPrintDocumentAdapter(jobName)
        printManager.print(
            jobName,
            printAdapter,
            PrintAttributes.Builder().build()
        )
    }
}
