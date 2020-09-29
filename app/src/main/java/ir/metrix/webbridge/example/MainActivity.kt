package ir.metrix.webbridge.example

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import ir.metrix.webbridge.MetrixBridge
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.getSettings().setJavaScriptEnabled(true)
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()

        MetrixBridge.registerAndGetInstance(webView)
        try {
            webView.loadUrl("file:///android_asset/MetrixExample-WebView.html")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        MetrixBridge.unregister()
        super.onDestroy()
    }
}