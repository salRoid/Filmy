package tech.salroid.filmy.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import tech.salroid.filmy.data.local.model.SimilarMovie

object SimilarMovieDiffCallback : DiffUtil.ItemCallback<SimilarMovie>() {

    override fun areItemsTheSame(oldItem: SimilarMovie, newItem: SimilarMovie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SimilarMovie, newItem: SimilarMovie): Boolean {
        return oldItem == newItem
    }
}