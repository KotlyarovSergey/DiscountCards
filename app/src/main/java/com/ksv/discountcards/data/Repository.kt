package com.ksv.discountcards.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ksv.discountcards.MyApp
import com.ksv.discountcards.entity.Card
import com.ksv.discountcards.entity.OuterImage
import com.ksv.discountcards.util.Utils
import java.io.FileOutputStream
import java.io.IOException

class Repository {
    suspend fun getAllCards() : List<Card>{
        val cardsDao = MyApp.getCardsDao()
        return cardsDao.getAllCards()
    }


    suspend fun getCard(id: Long): Card{
        val cardsDao = MyApp.getCardsDao()
        return cardsDao.getCardAtId(id)
    }

    fun editCard(card: Card){

    }

    fun deleteCard(card: Card){

    }

    suspend fun saveOuterImageAsCard(outerImage: OuterImage): Card?{
        val fileName = copyImageToWorkDir(outerImage.uri)
        fileName?.let {
//            val card = Card(0, fileName, outerImage.title)
            val uri = Utils.fileNameToUri(fileName)
            val card = Card(0, uri.toString() , outerImage.title)
            addCard(card)
            return card
        }
        return null
    }

    private suspend fun addCard(card: Card){
        val cardsDao = MyApp.getCardsDao()
        cardsDao.inset(card)
    }

    private fun copyImageToWorkDir(outerUri: Uri): String? {
        val contentResolver = MyApp.appContext.contentResolver
        val inputStream = contentResolver.openInputStream(outerUri)
        val fileName = "card${System.currentTimeMillis()}"
        var fos: FileOutputStream? = null
        return try {
            fos = MyApp.appContext.openFileOutput(fileName, Context.MODE_PRIVATE)
            inputStream?.copyTo(fos)
            fileName
        } catch (ex: IOException) {
            Log.d("ksvlog", "copyToWorkDir: ${ex.message}")
            null
        } finally {
            fos?.close()
        }
    }

    companion object{
        private val DEFAULT_CARDS_LIST = listOf(
//            Card(0, Uri.EMPTY, "Магнит")
            Card(0, "", "Магнит")
//            Card(1, "", "Пятерочка"),
//            Card(2, "", "FixPrice")
        )
    }
}