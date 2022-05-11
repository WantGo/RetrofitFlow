package com.wentgo.retrofit.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import com.wentgo.retrofit.flow.lib.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RetrofitManager.init("https://www.wanandroid.com/")
        findViewById<Button>(R.id.request).setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                Api::class.java.createApi().getBannerList().onSuccess {
                    findViewById<TextView>(R.id.tx).text = Gson().toJson(it)
                }
            }
        }
    }
}