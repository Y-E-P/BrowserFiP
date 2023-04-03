package com.yupo.browserfip

import android.annotation.SuppressLint
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
fun WebView.defaultSetup(){
    settings.javaScriptEnabled = true
}