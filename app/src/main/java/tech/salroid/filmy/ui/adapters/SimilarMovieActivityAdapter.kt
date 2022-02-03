package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.data.local.model.SimilarMovie
import tech.salroid.filmy.databinding.CustomRowBinding

class SimilarMovieActivityAdapter(
    private val similarList: List<SimilarMovie>,
    private val clickListener: ((SimilarMovie, Int) -> Unit)? = null
) : RecyclerView.Adapter<SimilarMovieActivityAdapter.SimilarMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMovieViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimilarMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarMovieViewHolder, position: Int) {
        holder.bindData(similarList[position])
    }

    override fun getItemCount(): Int = similarList.size

    inner class SimilarMovieViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(similarMovie: SimilarMovie) {
            binding.movieName.text = similarMovie.title

            Glide.with(binding.root.context)
                .load("http://image.tmdb.org/t/p/w185${similarMovie.posterPath}")
                .fitCenter()
                .into(binding.poster)
        }

        init {
            itemView.setOnClickListener {
                clickListener?.invoke(similarList[adapterPosition], adapterPosition)
            }
        }
    }
}