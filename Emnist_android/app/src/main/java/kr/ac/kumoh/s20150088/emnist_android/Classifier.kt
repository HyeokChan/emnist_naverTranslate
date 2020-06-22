package kr.ac.kumoh.s20150088.emnist_android

import android.app.Activity
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*


class Classifier {
    companion object{
        val MODEL_NAME = "emnist.tflite"
        val BATCH_SIZE = 1
        val IMAGE_HEIGHT = 28
        val IMAGE_WIDTH = 28
        val CHANNEL = 1
        val CLASSES = 26
    }

    private val options: Interpreter.Options = Interpreter.Options()
    private var mInterpreter: Interpreter? = null
    private var mImageData: ByteBuffer
    private val mImagePixels = IntArray(IMAGE_HEIGHT * IMAGE_WIDTH)
    private val mResult = Array(1) { FloatArray(CLASSES) }

    @Throws(IOException::class)
    constructor(activity: Activity){
        mInterpreter = loadModelFile(activity)?.let { Interpreter(it, options) }
        mImageData = ByteBuffer.allocateDirect(4 * BATCH_SIZE * IMAGE_HEIGHT * IMAGE_WIDTH * CHANNEL)
        mImageData.order(ByteOrder.nativeOrder())
    }

    fun classify(bitmap: Bitmap): Result? {
        convertBitmapToByteBuffer(bitmap)
        mInterpreter?.run(mImageData, mResult)
        return Result(mResult[0])
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(MODEL_NAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        if (mImageData == null) {
            return
        }
        mImageData.rewind()
        bitmap.getPixels(
            mImagePixels, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )
        var pixel = 0
        for (i in 0 until Classifier.IMAGE_WIDTH) {
            for (j in 0 until Classifier.IMAGE_HEIGHT) {
                val value = mImagePixels[pixel++]
                mImageData.putFloat(convertPixel(value))
            }
        }
    }

    private fun convertPixel(color: Int): Float {
        return (255 - ((color shr 16 and 0xFF) * 0.299f + (color shr 8 and 0xFF) * 0.587f + (color and 0xFF) * 0.114f)) / 255.0f
    }
}