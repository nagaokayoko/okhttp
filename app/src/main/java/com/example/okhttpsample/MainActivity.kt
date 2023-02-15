package com.example.okhttpsample

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    private val okHttpClient = OkHttpClient.Builder().build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            val text1 = findViewById<TextView>(R.id.text1)
            val request = Request.Builder()
                .url("https://refine-ea.com/data.json")
                .build()
            // 変数には型つける！何も返してないもののせいで型が何かわからなくてエラーになる
            val result = withContext(Dispatchers.IO) {
                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()
                    } else {
                        Log.d("failed", "code:${response.code} message:${response.message}")
                        ""
                    }
                }
            }
            Log.d("result", "$result")
            val text2: String = try {
                val data: Data = Gson().fromJson(result, Data::class.java)
                Log.d("data", "$data")
                data.key1
            } catch (e: JsonSyntaxException) {
                Log.d("JsonSyntaxException", "$e")
                ""
            }
            // メインスレッドでやる
            withContext(Dispatchers.Main) {
                text1.text = text2
                Log.d("text2", text2)
            }
        }
    }
}