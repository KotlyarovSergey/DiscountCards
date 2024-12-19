package com.ksv.discountcards.presentation

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ksv.discountcards.R
import com.ksv.discountcards.databinding.CardItemViewBinding
import com.ksv.discountcards.entity.Card

class CardRecyclerAdapter(
    private val openCard: (Card) -> Unit,
    private val canBeEdit: (Int?) -> Unit,
    private val canBeDelete: (List<Card>) -> Unit
) : ListAdapter<Card, CardRecyclerAdapter.CardViewHolder>(DiffUtilCallback()) {
    private val selectedItems = mutableSetOf<Int>()
    private var _isSelectMode = false
    val isSelectMode get() = _isSelectMode

    class DiffUtilCallback: DiffUtil.ItemCallback<Card>(){
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean =
            oldItem == newItem
    }

    inner class CardViewHolder(val binding: CardItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val holder = CardViewHolder(
            CardItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        holder.binding.mainFrame.setOnClickListener {
            onItemClickTwo(holder.adapterPosition)
        }

        holder.binding.mainFrame.setOnLongClickListener {
            onItemLongClickTwo(holder.adapterPosition)
            true
        }
        return holder
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = getItem(position)
        with(holder.binding) {
            title.text = card.title
            image.setImageURI(Uri.parse(card.fileUri))
            if (selectedItems.contains(position)) {
                val color = linear.context.getColor(R.color.grey)
                linear.setBackgroundColor(color)
            } else {
                linear.setBackgroundColor(linear.context.getColor(R.color.card_background_normal))
            }
        }
    }

    fun unSelectAll() {
        val allSelectedItems = selectedItems.toList()
        selectedItems.clear()
        notifySelectedItemsChanged()
        allSelectedItems.forEach {
            notifyItemChanged(it)
        }
        _isSelectMode = false
    }

    private fun onItemClickTwo(position: Int){
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
            openCard(currentList[position])
        }
    }

    private fun onItemLongClickTwo(position: Int) {
        selectedItems.add(position)
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

        val deletedCards = selectedItems.map { position -> currentList[position] }
        canBeDelete(deletedCards)
    }

}