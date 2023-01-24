package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastCrew
import tech.salroid.filmy.databinding.ItemMemberBinding

class CastCrewAdapter(
    private val castCrewList: List<CastCrew>,
    private val fixedSize: Boolean,
    private val clickListener: ((CastCrew, Int, View) -> Unit)? = null,
) : RecyclerView.Adapter<CastCrewAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bindData(castCrewList[position])
    }

    override fun getItemCount(): Int =
        if (fixedSize) castCrewList.size.coerceAtMost(5) else castCrewList.size

    inner class CastViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickListener?.invoke(castCrewList[adapterPosition], adapterPosition, it)
            }
        }

        fun bindData(castCrew: CastCrew) {
            val profilePath = when (castCrew) {
                is CastCrew.CastData -> {
                    binding.memberName.text = castCrew.cast.name
                    binding.memberDescription.text = castCrew.cast.character
                    castCrew.cast.profilePath

                }
                is CastCrew.CrewData -> {
                    binding.memberName.text = castCrew.crew.name
                    binding.memberDescription.text = castCrew.crew.job
                    castCrew.crew.profilePath
                }
            }

            binding.root.context.let {
                Glide.with(it)
                    .load(it.getString(R.string.member_profile_url, profilePath))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .fitCenter()
                    .into(binding.memberProfileImage)
            }
        }
    }
}