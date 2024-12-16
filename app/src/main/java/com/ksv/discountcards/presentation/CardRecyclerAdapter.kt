package com.ksv.discountcards.presentation

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksv.discountcards.databinding.CardItemViewBinding
import com.ksv.discountcards.entity.Card

class CardRecyclerAdapter(
    private val onClick: (Card) -> Unit
): RecyclerView.Adapter<CardRecyclerAdapter.CardViewHolder>() {
    private var cards: List<Card> = emptyList()

    inner class CardViewHolder(val binding: CardItemViewBinding): RecyclerView.ViewHolder(binding.root)

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
        val card = cards[position]
        with(holder.binding){
            title.text = card.title
            image.setImageURI(Uri.parse(card.fileUri))
            mainFrame.setOnClickListener { onClick(card) }
        }
    }

    fun setDate(cardsList: List<Card>){
        cards = cardsList
        notifyItemRangeChanged(0, cards.size)
    }
}