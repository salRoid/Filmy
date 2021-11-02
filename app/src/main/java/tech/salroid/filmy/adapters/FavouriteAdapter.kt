package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.FavouriteData
import tech.salroid.filmy.databinding.CustomRowBinding

class FavouriteAdapter(
    private val favouriteList: List<FavouriteData>,
    private val clickListener: ((FavouriteData, Int) -> Unit)? = null,
    private val longClickListener: ((FavouriteData, Int) -> Unit)? = null
) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bindData(favouriteList[position])
    }

    override fun getItemCount(): Int = favouriteList.size

    inner class FavouriteViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(favouriteData: FavouriteData) {
            val title = favouriteData.title
            val poster = favouriteData.poster

            binding.title.text = title

            Glide.with(binding.root.context)
                .load(poster)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.poster)
        }

        init {
            binding.main.setOnClickListener {
                clickListener?.invoke(favouriteList[adapterPosition], adapterPosition)
            }

            binding.main.setOnLongClickListener {
                longClickListener?.invoke(favouriteList[adapterPosition], adapterPosition)
                true
            }
        }
    }
}