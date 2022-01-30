package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.data.local.CastMovie
import tech.salroid.filmy.databinding.CharCustomRowBinding

class CharacterDetailsActivityAdapter(
    private val moviesList: List<CastMovie>,
    private val fixedSize: Boolean,
    private val clickListener: ((CastMovie, Int) -> Unit)? = null
) : RecyclerView.Adapter<CharacterDetailsActivityAdapter.CharacterDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterDetailsViewHolder {
        val binding =
            CharCustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterDetailsViewHolder, position: Int) {
        holder.bindData(moviesList[position])
    }

    override fun getItemCount(): Int =
        if (fixedSize) if (moviesList.size >= 5) 5 else moviesList.size else moviesList.size

    inner class CharacterDetailsViewHolder(private val binding: CharCustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(movie: CastMovie) {

            val movieName = movie.originalTitle
            val moviePoster = movie.posterPath
            val rolePlayed = movie.character

            binding.movieName.text = movieName
            binding.movieRolePlayed.text = rolePlayed

            Glide.with(binding.root.context)
                .load("http://image.tmdb.org/t/p/w45${moviePoster}")
                .fitCenter()
                .into(binding.moviePoster)
        }

        init {
            binding.root.setOnClickListener {
                clickListener?.invoke(moviesList[adapterPosition], adapterPosition)
            }
        }
    }
}