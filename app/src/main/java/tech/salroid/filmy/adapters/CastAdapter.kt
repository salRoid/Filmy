package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.CastMemberDetailsData
import tech.salroid.filmy.databinding.CastCustomRowBinding

class CastAdapter(
    private val castList: List<CastMemberDetailsData>,
    private val fixedSize: Boolean,
    private val clickListener: ((CastMemberDetailsData, Int, View) -> Unit)? = null,
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

        fun bindData(castMemberDetailsData: CastMemberDetailsData) {
            val castName = castMemberDetailsData.castName
            val castRole = castMemberDetailsData.castRolePlayed
            val castDisplayProfile = castMemberDetailsData.castDisplayProfile

            binding.castName.text = castName
            binding.castDescription.text = castRole

            Glide.with(binding.root.context)
                .load(castDisplayProfile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .into(binding.castPoster)
        }
    }
}