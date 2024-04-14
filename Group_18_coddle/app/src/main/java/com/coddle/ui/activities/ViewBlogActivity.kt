package com.coddle.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.coddle.databinding.ActivityViewBlogBinding
import com.coddle.util.AppUtil.Companion.shortToast

class ViewBlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewBlogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadWebView()

        binding.btnRetry.setOnClickListener { loadWebView() }

        binding.imgBack.setOnClickListener { onBackPressed() }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView() {
        binding.progressCircular.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.INVISIBLE
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            loadUrl(intent.getStringExtra("link").toString())
            webViewClient = object : WebViewClient() {
                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    binding.webView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                }


                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    binding.webView.visibility = View.INVISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.btnRetry.visibility = View.VISIBLE
                }
            }
        }
    }
}