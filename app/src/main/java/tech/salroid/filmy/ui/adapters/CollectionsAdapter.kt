package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.databinding.CustomRowCollectionBinding
import tech.salroid.filmy.utility.toReadableDate

class CollectionsAdapter(
    private val clickListener: ((String, String?) -> Unit)? = null,
    private val longClickListener: ((MovieDetails, Int) -> Unit)? = null
) : ListAdapter<MovieDetails, CollectionsAdapter.SavedMoviesViewHolder>(MovieDetailsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedMoviesViewHolder {
        val binding =
            CustomRowCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedMoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedMoviesViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class SavedMoviesViewHolder(private val binding: CustomRowCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickListener?.invoke(
                    getItem(adapterPosition).id.toString(),
                    getItem(adapterPosition).originalTitle
                )
            }

            binding.root.setOnLongClickListener {
                longClickListener?.invoke(getItem(adapterPosition), adapterPosition)
                true
            }
        }

        fun bindData(movie: MovieDetails) {
            binding.movieTitle.text = movie.title
            binding.movieYear.text = movie.releaseDate?.toReadableDate()
            binding.moviePlot.text = movie.overview

            binding.root.context.let {
                Glide.with(it)
                    .load(it.getString(R.string.movie_poster_url, movie.posterPath))
                    .placeholder(R.drawable.movie_skeleton)
                    .error(R.drawable.movie_skeleton)
                    .into(binding.poster)
            }
        }
    }
}
