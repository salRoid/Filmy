package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.SimilarMovie
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