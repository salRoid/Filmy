package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.SingleTrailerViewBinding
import kotlin.properties.Delegates

class TrailerAdapter(
    private val trailers: Array<String>,
    private val trailerNameList: Array<String>,
    private val clickListener: ((String) -> Unit)? = null
) : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val binding =
            SingleTrailerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrailerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bindData(trailers[position], trailerNameList[position])
    }

    override fun getItemCount(): Int = trailers.size

    inner class TrailerViewHolder(private val binding: SingleTrailerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var prefix by Delegates.notNull<String>()
        private var suffix by Delegates.notNull<String>()

        init {
            val resources = binding.root.context.resources
            prefix = resources.getString(R.string.trailer_img_prefix)
            suffix = resources.getString(R.string.trailer_img_suffix)

            itemView.setOnClickListener {
                clickListener?.invoke(trailers[adapterPosition])
            }
        }

        fun bindData(trailerId: String, trailerName: String) {
            val thumbnail = "$prefix$trailerId$suffix"

            Glide.with(binding.root.context)
                .load(thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .into((binding.detailYoutube))

            binding.trailerTitle.text = trailerName
        }
    }
}