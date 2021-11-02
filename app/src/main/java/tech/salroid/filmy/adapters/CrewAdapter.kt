package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.CrewMemberDetailsData
import tech.salroid.filmy.databinding.CrewCustomRowBinding

class CrewAdapter(
    private val crewList: List<CrewMemberDetailsData>,
    private val fixedSize: Boolean,
    private val clickListener: ((CrewMemberDetailsData, Int, View) -> Unit)? = null
) : RecyclerView.Adapter<CrewAdapter.CrewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        val binding =
            CrewCustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        holder.bindData(crewList[position])
    }

    override fun getItemCount(): Int = if (fixedSize) {
        if (crewList.size >= 5) 5 else crewList.size
    } else crewList.size


    inner class CrewViewHolder(private val binding: CrewCustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickListener?.invoke(crewList[adapterPosition], adapterPosition, it)
            }
        }

        fun bindData(crewMemberDetailsData: CrewMemberDetailsData) {
            val name = crewMemberDetailsData.crewMemberName
            val job = crewMemberDetailsData.crewMemberJob
            val displayProfile = crewMemberDetailsData.crewMemberProfile

            binding.crewName.text = name
            binding.crewDescription.text = job

            Glide.with(binding.root.context)
                .load(displayProfile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .into(binding.crewPoster)
        }
    }
}