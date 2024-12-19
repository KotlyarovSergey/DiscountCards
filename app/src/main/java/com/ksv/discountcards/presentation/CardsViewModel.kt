package com.ksv.discountcards.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksv.discountcards.data.Repository
import com.ksv.discountcards.entity.Card
import com.ksv.discountcards.entity.OuterImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.NullPointerException

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
            card?.let {
                _cards.update {
                    _cards.value.toMutableList().apply {
                        this.add(card)
                    }
                }
            }
        }
    }

    fun updateCard(card: Card){
        val repository = Repository()
        viewModelScope.launch {
            repository.updateCard(card)
            _cards.value = repository.getAllCards().toMutableList()     // !!!!!!!!! заменить на LiveData
        }
    }

    fun selectCard(card: Card) {
        _selectedCard = try {
            Uri.parse(card.fileUri)
            card
        } catch (ex: NullPointerException) {
            null
        }
    }

    fun deleteCards(deletedCards: List<Card>){
        val repository = Repository()
        viewModelScope.launch {
            deletedCards.forEach {
                repository.deleteCard(it)
            }
            _cards.value = repository.getAllCards().toMutableList()     // !!!!!!!!! заменить на LiveData
        }
    }

    fun selectCard(index: Int) {
        _selectedCard = try {
            val card = cards.value[index]
            Uri.parse(card.fileUri)
            card
        } catch (ex: NullPointerException) {
            null
        }
    }

}