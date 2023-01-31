package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.databinding.CustomRowBinding
import tech.salroid.filmy.utility.toReadableDate

class TvAdapter(
    private val clickListener: ((TvShow) -> Unit)? = null
) : PagingDataAdapter<TvShow, TvAdapter.TvViewHolder>(TvDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TvViewHolder, position: Int) {
        getItem(position)?.let { holder.bindData(it) }
    }

    inner class TvViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.main.setOnClickListener {
                getItem(absoluteAdapterPosition)?.let { it1 -> clickListener?.invoke(it1) }
            }
        }

        fun bindData(show: TvShow) {
            binding.movieName.text = show.name
            binding.movieYear.text = show.firstAirDate?.toReadableDate()

            binding.root.context.let {
                Glide.with(it)
                    .load(it.getString(R.string.movie_poster_url, show.posterPath))
                    .placeholder(R.drawable.movie_skeleton)
                    .error(R.drawable.movie_skeleton)
                    .into(binding.poster)
            }
        }
    }
}