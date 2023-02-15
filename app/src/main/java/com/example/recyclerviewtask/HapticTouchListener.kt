package com.example.recyclerviewtask

import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View

class HapticTouchListener : View.OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event?.actionMasked){
            MotionEvent.ACTION_DOWN->
                v?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            MotionEvent.ACTION_UP ->
                v?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
        }
        return true
    }
}