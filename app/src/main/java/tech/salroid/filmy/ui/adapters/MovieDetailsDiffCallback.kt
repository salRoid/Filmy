package tech.salroid.filmy.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import tech.salroid.filmy.data.local.db.entity.MovieDetails

object MovieDetailsDiffCallback : DiffUtil.ItemCallback<MovieDetails>() {

    override fun areItemsTheSame(oldItem: MovieDetails, newItem: MovieDetails): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieDetails, newItem: MovieDetails): Boolean {
        return oldItem == newItem
    }
}