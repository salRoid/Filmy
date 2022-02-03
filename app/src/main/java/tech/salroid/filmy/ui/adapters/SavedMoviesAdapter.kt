package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.databinding.CustomRowBinding
import tech.salroid.filmy.utility.toReadableDate

class SavedMoviesAdapter(
    private var moviesList: ArrayList<MovieDetails>? = null,
    private val clickListener: ((String, String?) -> Unit)? = null,
    private val longClickListener: ((MovieDetails, Int) -> Unit)? = null
) : RecyclerView.Adapter<SavedMoviesAdapter.SavedMoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedMoviesViewHolder {
        val binding =
            CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedMoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedMoviesViewHolder, position: Int) {
        moviesList?.let { holder.bindData(it[position]) }
    }

    override fun getItemCount(): Int = moviesList?.size ?: 0

    fun swapData(moviesList: ArrayList<MovieDetails>) {
        this.moviesList = moviesList
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int){
        moviesList?.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class SavedMoviesViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(movie: MovieDetails) {
            binding.movieName.text = movie.title
            binding.movieYear.text = movie.releaseDate?.toReadableDate()

            Glide.with(binding.root.context)
                .load("http://image.tmdb.org/t/p/w342${movie.posterPath}")
                .into(binding.poster)
        }

        init {
            binding.main.setOnClickListener {
                moviesList?.let {
                    clickListener?.invoke(
                        it[adapterPosition].id.toString(),
                        it[adapterPosition].originalTitle
                    )
                }
            }

            binding.main.setOnLongClickListener {
                moviesList?.let { it1 ->
                    longClickListener?.invoke(
                        it1[adapterPosition],
                        adapterPosition
                    )
                }
                true
            }
        }
    }
}
