package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Cast
import tech.salroid.filmy.databinding.CastCustomRowBinding

class CastAdapter(
    private val castList: List<Cast>,
    private val fixedSize: Boolean,
    private val clickListener: ((Cast, Int, View) -> Unit)? = null,
) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding =
            CastCustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bindData(castList[position])
    }

    override fun getItemCount(): Int =
        if (fixedSize) castList.size.coerceAtMost(5) else castList.size

    inner class CastViewHolder(private val binding: CastCustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickListener?.invoke(castList[adapterPosition], adapterPosition, it)
            }
        }

        fun bindData(cast: Cast) {
            binding.castName.text = cast.name
            binding.castDescription.text = cast.character

            Glide.with(binding.root.context)
                .load("http://image.tmdb.org/t/p/w185${cast.profilePath}")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .fitCenter()
                .into(binding.castPoster)
        }
    }
}