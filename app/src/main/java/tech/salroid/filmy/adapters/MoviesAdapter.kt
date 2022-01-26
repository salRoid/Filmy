package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.FilmyApplication.Companion.context
import tech.salroid.filmy.data.MovieResult
import tech.salroid.filmy.databinding.CustomRowBinding

class MoviesAdapter(
    private var movies: List<MovieResult> = emptyList(),
    private val clickListener: ((MovieResult) -> Unit)? = null
) : RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bindData(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun swapData(newMovies: List<MovieResult>) {
        this.movies = newMovies
        notifyDataSetChanged() //TODO improvement required
    }

    inner class MoviesViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.main.setOnClickListener {
                clickListener?.invoke(movies[adapterPosition])
            }
        }

        fun bindData(movie: MovieResult) {
            Glide.with(context)
                .load("http://image.tmdb.org/t/p/w342${movie.posterPath}")
                .into(binding.poster)
        }
    }
}