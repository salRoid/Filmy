package tech.salroid.filmy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.data.SearchData
import tech.salroid.filmy.databinding.CustomRowSearchBinding

class SearchResultAdapter(
    private val searchList: List<SearchData>,
    private val clickListener: ((SearchData, Int) -> Unit)? = null
) :
    RecyclerView.Adapter<SearchResultAdapter.SearchResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val binding =
            CustomRowSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        holder.bindData(searchList[position])
    }

    override fun getItemCount(): Int = searchList.size

    inner class SearchResultsViewHolder(private val binding: CustomRowSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(searchData: SearchData) {

            val name = searchData.movie
            val poster = searchData.poster
            val date = searchData.date
            binding.title.text = name

            if (date != "null") binding.date.text = date else {
                binding.date.visibility = View.INVISIBLE
            }

            Glide.with(binding.root.context)
                .load(poster)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.poster)
        }

        init {
            binding.main.setOnClickListener {
                clickListener?.invoke(searchList[adapterPosition], adapterPosition)
            }
        }
    }
}