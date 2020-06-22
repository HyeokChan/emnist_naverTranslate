package kr.ac.kumoh.s20150088.emnist_android

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.nex3z.fingerpaintview.FingerPaintView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    lateinit var classifier:Classifier
    var alphabet = arrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
    companion object{
        lateinit var textViewTitle:TextView
        lateinit var textView:TextView
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_main)

        textViewTitle = findViewById(R.id.tvTitle)
        textView = findViewById(R.id.tvPrediction)

        init()
    }

    private fun init() {
        try {
            classifier = Classifier(this)
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to create classifier", Toast.LENGTH_LONG).show()
        }
    }

    fun onDetect(view: View?) {
        tvTitle.text = "Detected word"
        if (classifier == null) {
            Log.e("error", "Classifier is null")
            return
        }
        for(i in 1 until 11){
            var getId = resources.getIdentifier("fpvPaint"+i, "id", this.packageName)
            var temp:FingerPaintView = findViewById(getId)
            if(temp.isEmpty){
                tvPrediction!!.text = tvPrediction!!.text.toString() + " "
                continue
            }
            val image: Bitmap = temp.exportToBitmap(
                Classifier.IMAGE_WIDTH, Classifier.IMAGE_HEIGHT
            )
            val result: Result? = classifier.classify(image)
            renderResult(result!!)
            temp.clear()
        }
        var temp = tvPrediction!!.text.toString()
        temp = temp.trim()
        tvPrediction!!.text = temp
    }

    private fun renderResult(result:Result) {
        tvPrediction!!.text = tvPrediction.text.toString() + alphabet.get(result.getPrediction())
    }

    fun onBackspace(view: View?) {
        var blDraw = false
        for (i in 10 downTo 1){
            var getId = resources.getIdentifier("fpvPaint"+i, "id", this.packageName)
            var temp:FingerPaintView = findViewById(getId)
            if(temp.isEmpty){
                continue
            }
            else{
                blDraw = true
                temp.clear()
                break
            }
        }
        if (!blDraw){
            val text = tvPrediction!!.text.toString()
            try {
                tvPrediction!!.text = text.substring(0, text.length - 1)
            } catch (e: StringIndexOutOfBoundsException) {
                tvPrediction!!.text = ""
            }
        }
    }

    fun onDelete(view: View?) {
        var blDraw = false
        for (i in 1 until 11){
            var getId = resources.getIdentifier("fpvPaint"+i, "id", this.packageName)
            var temp:FingerPaintView = findViewById(getId)
            if (!temp.isEmpty){
                blDraw = true
            }
            temp.clear()
        }
        if(!blDraw){
            tvPrediction!!.text = ""
        }
    }

    fun onDictionary(view: View?){
        if(!tvPrediction!!.text.toString().equals("")){
            var asyncTask = NaverTranslateTask()
            asyncTask.execute(tvPrediction!!.text.toString().toLowerCase())
        }
        else{
            Toast.makeText(this,"word is null",Toast.LENGTH_LONG).show()
        }

    }



}
