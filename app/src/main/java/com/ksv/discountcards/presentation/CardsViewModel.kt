package com.ksv.discountcards.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.discountcards.data.Repository
import com.ksv.discountcards.entity.Card
import com.ksv.discountcards.entity.OuterImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardsViewModel : ViewModel() {
    private var _selectedCard: Card? = null
    val selectedCard get() = _selectedCard

    private val _cards = MutableStateFlow<MutableList<Card>>(mutableListOf())
    val cards = _cards.asStateFlow()

    init {
        val repository = Repository()
        viewModelScope.launch {
            _cards.value = repository.getAllCards().toMutableList()
        }
    }

    fun saveOuterImage(outerImage: OuterImage) {
        val repository = Repository()
        viewModelScope.launch {
            val card = repository.saveOuterImageAsCard(outerImage)
            Log.d("ksvlog", card.toString())
            card?.let { _cards.value.add(it) }
            Log.d("ksvlog", cards.toString())
        }
    }

    fun selectCard(card: Card){
        _selectedCard = card
    }

}