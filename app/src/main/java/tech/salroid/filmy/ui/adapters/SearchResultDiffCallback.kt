package tech.salroid.filmy.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import tech.salroid.filmy.data.local.model.SearchResult

object SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResult>() {

    override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem == newItem
    }
}