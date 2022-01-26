package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.WatchlistData
import tech.salroid.filmy.databinding.CustomRowBinding

class WatchlistAdapter(
    private val data: List<WatchlistData>,
    private val clickListener: ((WatchlistData, Int) -> Unit)? = null,
    private val longClickListener: ((WatchlistData, Int) -> Unit)? = null
) : RecyclerView.Adapter<WatchlistAdapter.WatchListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WatchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WatchListViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class WatchListViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.main.setOnClickListener {
                clickListener?.invoke(data[adapterPosition], adapterPosition)
            }
            binding.main.setOnLongClickListener {
                longClickListener?.invoke(data[adapterPosition], adapterPosition)
                true
            }
        }

        fun bindData(watchlistData: WatchlistData) {

            val poster = watchlistData.poster

            try {
                Glide.with(binding.root.context).load(poster)
                    .into(binding.poster)
            } catch (e: Exception) {
            }
        }
    }
}