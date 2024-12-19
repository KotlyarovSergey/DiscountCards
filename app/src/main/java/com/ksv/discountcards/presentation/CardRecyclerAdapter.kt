package com.ksv.discountcards.presentation

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksv.discountcards.R
import com.ksv.discountcards.databinding.CardItemViewBinding
import com.ksv.discountcards.entity.Card

class CardRecyclerAdapter(
    private val openCard: (Card) -> Unit,
    private val onClick: (Int) -> Unit,
    private val onLongClick: (Int) -> Unit,
    private val canBeEdit: (Int?) -> Unit,
    private val canBeDelete: (List<Card>) -> Unit

) : RecyclerView.Adapter<CardRecyclerAdapter.CardViewHolder>() {
    private var cards: List<Card> = emptyList()
    private val selectedItems = mutableSetOf<Int>()
    private var _isSelectMode = false
    val isSelectMode get() = _isSelectMode

    inner class CardViewHolder(val binding: CardItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            CardItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = cards.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        Log.d("ksvlog", "onBind. pos: $position")
        val card = cards[position]
        with(holder.binding) {
            title.text = card.title
            image.setImageURI(Uri.parse(card.fileUri))
            mainFrame.setOnClickListener { onClick(position) }
            mainFrame.setOnClickListener { onItemClick(position) }
            mainFrame.setOnLongClickListener {
                //onLongClick(position)
                onItemLongClick(position)
                return@setOnLongClickListener true
            }
            if (selectedItems.contains(position)) {
//            if (selectedItems.value.contains(position)) {
                val color = linear.context.getColor(R.color.grey)
                linear.setBackgroundColor(color)
            } else {
                linear.setBackgroundColor(linear.context.getColor(R.color.card_background_normal))
            }
        }
    }

    fun setData(cardsList: List<Card>) {
        Log.d("ksvlog", "Adapter.setData: size:${cardsList.size}")
        cards = cardsList
        notifyItemRangeChanged(0, cards.size)
    }

    fun unSelectAll() {
        val allSelectedItems = selectedItems.toList()
//        val allSelectedItems = selectedItems.value.toList()
        selectedItems.clear()
//        selectedItems.value.clear()
        notifySelectedItemsChanged()
        allSelectedItems.forEach {
            notifyItemChanged(it)
        }
        _isSelectMode = false
    }

    private fun onItemClick(position: Int) {
        if (_isSelectMode) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                _isSelectMode = selectedItems.isNotEmpty()
            } else {
                selectedItems.add(position)
            }
            notifySelectedItemsChanged()
            notifyItemChanged(position)
        } else {
            openCard(cards[position])
        }
    }

    private fun onItemLongClick(position: Int) {
        selectedItems.add(position)
//        selectedItems.value.add(position)
        notifySelectedItemsChanged()
        notifyItemChanged(position)
        _isSelectMode = true
    }

    private fun notifySelectedItemsChanged(){
        val itemToEdit = if(selectedItems.size == 1){
            selectedItems.first()
        } else {
            null
        }
        canBeEdit(itemToEdit)

        val deletedCards = selectedItems.map { position -> cards[position] }
        canBeDelete(deletedCards)
    }

}