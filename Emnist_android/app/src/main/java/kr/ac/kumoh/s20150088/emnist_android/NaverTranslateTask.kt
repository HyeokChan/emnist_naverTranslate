package kr.ac.kumoh.s20150088.emnist_android

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class NaverTranslateTask : AsyncTask<String?, Void?, String?>() {
    var sourceLang = "en"
    var targetLang = "ko"
    var clientId = "Xr9P0CpMB494U0Hzgevt"
    var clientSecret = "QLisMgbQJh"

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strings: String?): String? {
        val sourceText = strings[0]
        try {
            val text = URLEncoder.encode(sourceText, "UTF-8")
            val apiURL = "https://openapi.naver.com/v1/papago/n2mt"
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("X-Naver-Client-Id", clientId)
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret)
            // post request
            val postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + text
            con.doOutput = true
            val wr = DataOutputStream(con.outputStream)
            wr.writeBytes(postParams)
            wr.flush()
            wr.close()
            val responseCode = con.responseCode
            val br: BufferedReader
            br = if (responseCode == 200) {
                BufferedReader(InputStreamReader(con.inputStream))
            } else {
                BufferedReader(InputStreamReader(con.errorStream))
            }
            var inputLine: String?
            val response = StringBuffer()
            while (br.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            br.close()
            return response.toString()
        } catch (e: Exception) {
            Log.d("error", e.message)
            return null
        }
    }

    override fun onPostExecute(s: String?) {
        super.onPostExecute(s)
        val gson: Gson = GsonBuilder().create()
        val parser = JsonParser()
        val rootObj: JsonElement = parser.parse(s.toString())
            .getAsJsonObject().get("message")
            .getAsJsonObject().get("result")
        val items: TranslatedItem = gson.fromJson(
            rootObj.toString(),
            TranslatedItem::class.java
        )
        val temp = items.translatedText
        Log.i("result??",temp)

        MainActivity.textViewTitle.text = "Translated word"
        MainActivity.textView.text = temp
    }

    private inner class TranslatedItem {
        var translatedText: String? = null
    }
}