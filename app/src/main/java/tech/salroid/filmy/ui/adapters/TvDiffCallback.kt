package tech.salroid.filmy.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import tech.salroid.filmy.data.local.model.TvShow

object TvDiffCallback : DiffUtil.ItemCallback<TvShow>() {

    override fun areItemsTheSame(oldItem: TvShow, newItem: TvShow): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TvShow, newItem: TvShow): Boolean {
        return oldItem == newItem
    }
}