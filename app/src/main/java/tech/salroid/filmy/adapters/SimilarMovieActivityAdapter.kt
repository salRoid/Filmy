package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.SimilarMoviesData
import tech.salroid.filmy.databinding.SimilarCustomRowBinding

class SimilarMovieActivityAdapter(
    private val similarList: List<SimilarMoviesData>,
    private val clickListener: ((SimilarMoviesData, Int) -> Unit)? = null
) : RecyclerView.Adapter<SimilarMovieActivityAdapter.SimilarMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMovieViewHolder {
        val binding =
            SimilarCustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimilarMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarMovieViewHolder, position: Int) {
        holder.bindData(similarList[position])
    }

    override fun getItemCount(): Int = similarList.size

    inner class SimilarMovieViewHolder(private val binding: SimilarCustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(similarMoviesData: SimilarMoviesData) {

            val title = similarMoviesData.title
            val banner = similarMoviesData.banner
            binding.title.text = title

            Glide.with(binding.root.context)
                .load(banner)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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