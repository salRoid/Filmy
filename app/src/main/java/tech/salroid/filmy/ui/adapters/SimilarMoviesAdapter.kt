package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.SimilarMovie
import tech.salroid.filmy.databinding.CustomRowSimilarBinding

class SimilarMoviesAdapter(
    private val clickListener: ((SimilarMovie, Int) -> Unit)? = null
) : ListAdapter<SimilarMovie, SimilarMoviesAdapter.SimilarMovieViewHolder>(SimilarMovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMovieViewHolder {
        val binding =
            CustomRowSimilarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimilarMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarMovieViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class SimilarMovieViewHolder(private val binding: CustomRowSimilarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                clickListener?.invoke(getItem(adapterPosition), adapterPosition)
            }
        }

        fun bindData(similarMovie: SimilarMovie) {
            binding.movieName.text = similarMovie.title

            similarMovie.posterPath?.let { path ->
                binding.root.context.let {
                    Glide.with(it)
                        .load(it.getString(R.string.movie_poster_url, path))
                        .fitCenter()
                        .placeholder(R.drawable.movie_skeleton)
                        .error(R.drawable.movie_skeleton)
                        .into(binding.poster)
                }
            }
        }
    }
}