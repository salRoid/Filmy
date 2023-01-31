package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.databinding.CustomRowBinding
import tech.salroid.filmy.utility.toReadableDate

class MoviesViewHolder(
    private val binding: CustomRowBinding,
    private val clickListener: ((Movie) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(
            clickListener: ((Movie) -> Unit)?,
            parent: ViewGroup
        ): MoviesViewHolder {
            return MoviesViewHolder(
                CustomRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), clickListener
            )
        }
    }

    fun bind(movie: Movie) {
        with(binding) {
            main.setOnClickListener {
                clickListener?.invoke(movie)
            }

            movieName.text = movie.title
            movieYear.text = movie.releaseDate?.toReadableDate()

            movie.posterPath?.let { path ->
                binding.root.context.let {
                    Glide.with(it)
                        .load(it.getString(R.string.movie_poster_url, path))
                        .placeholder(R.drawable.movie_skeleton)
                        .error(R.drawable.movie_skeleton)
                        .into(poster)
                }
            }
        }
    }
}