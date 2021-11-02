package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.PersonMovieDetailsData
import tech.salroid.filmy.databinding.CharCustomRowBinding

class CharacterDetailsActivityAdapter(
    private val moviesList: List<PersonMovieDetailsData>,
    private val fixedSize: Boolean,
    private val clickListener: ((PersonMovieDetailsData, Int) -> Unit)? = null
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

        fun bindData(personMovieDetailsData: PersonMovieDetailsData) {

            val movieName = personMovieDetailsData.movieTitle
            val moviePoster = personMovieDetailsData.moviePoster
            val rolePlayed = personMovieDetailsData.rolePlayed

            binding.movieName.text = movieName
            binding.movieRolePlayed.text = rolePlayed

            Glide.with(binding.root.context)
                .load(moviePoster)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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