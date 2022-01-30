package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.Crew
import tech.salroid.filmy.databinding.CrewCustomRowBinding

class CrewAdapter(
    private val crewList: List<Crew>,
    private val fixedSize: Boolean,
    private val clickListener: ((Crew, Int, View) -> Unit)? = null
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

        fun bindData(crew: Crew) {
            binding.crewName.text = crew.name
            binding.crewDescription.text = crew.job

            Glide.with(binding.root.context)
                .load("http://image.tmdb.org/t/p/w185${crew.profilePath}")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .fitCenter()
                .into(binding.crewPoster)
        }
    }
}