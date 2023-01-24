package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.databinding.CustomRowBinding
import tech.salroid.filmy.utility.toReadableDate

class MoviesAdapter(
    private var movies: List<Movie> = emptyList(),
    private val clickListener: ((Movie) -> Unit)? = null
) : RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bindData(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun swapData(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged() // TODO improvement required
    }

    inner class MoviesViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.main.setOnClickListener {
                clickListener?.invoke(movies[adapterPosition])
            }
        }

        fun bindData(movie: Movie) {
            binding.movieName.text = movie.title
            binding.movieYear.text = movie.releaseDate?.toReadableDate()

            binding.root.context.let {
                Glide.with(it)
                    .load(it.getString(R.string.movie_poster_url, movie.posterPath))
                    .into(binding.poster)
            }
        }
    }
}