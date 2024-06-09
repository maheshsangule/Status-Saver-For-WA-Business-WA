package com.developermaheshapps.statussaver.utils

import android.app.Dialog
import android.content.Context
import com.developermaheshapps.statussaver.R

object Progressbar {
    fun showProgressBar(context: Context): Dialog {
        val dialogue = Dialog(context)
        dialogue.setContentView(R.layout.progressbar)
        dialogue.show()

        return dialogue
    }
}