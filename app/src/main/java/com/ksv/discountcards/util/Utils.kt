package com.ksv.discountcards.util

import android.net.Uri
import androidx.core.net.toUri
import com.ksv.discountcards.MyApp
import java.io.File

object Utils {
    fun fileNameToUri(fineName: String): Uri{
        val file = File(MyApp.appContext.filesDir.absoluteFile, fineName)
        return if(file.exists())
            file.toUri()
        else Uri.EMPTY
    }
}