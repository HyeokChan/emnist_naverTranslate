package kr.ac.kumoh.s20150088.emnist_android

class Result {
    companion object{
        private var mPrediction = 0
        private var mProbability = 0f
        private var mTimeCost: Long = 0
    }
    constructor(probs:FloatArray){
        mPrediction = argmax(probs)
        mProbability = probs[mPrediction]
    }

    fun getPrediction(): Int {
        return mPrediction
    }

    fun getProbability(): Float {
        return mProbability
    }

    fun getTimeCost(): Long {
        return mTimeCost
    }

    fun argmax(probs:FloatArray):Int{
        var maxIdx = -1
        var maxProb = 0.0f
        for (i in probs.indices) {
            if (probs[i] > maxProb) {
                maxProb = probs[i]
                maxIdx = i
            }
        }
        return maxIdx
    }
}