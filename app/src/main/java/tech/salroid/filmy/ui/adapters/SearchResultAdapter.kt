package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.data.local.SearchResult
import tech.salroid.filmy.databinding.CustomRowBinding

class SearchResultAdapter(
    private val searchList: List<SearchResult>,
    private val clickListener: ((SearchResult, Int) -> Unit)? = null
) :
    RecyclerView.Adapter<SearchResultAdapter.SearchResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val binding =
            CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        holder.bindData(searchList[position])
    }

    override fun getItemCount(): Int = searchList.size

    inner class SearchResultsViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(searchData: SearchResult) {
            val poster = searchData.posterPath

            Glide.with(binding.root.context)
                .load("http://image.tmdb.org/t/p/w185$poster")
                .into(binding.poster)
        }

        init {
            binding.main.setOnClickListener {
                clickListener?.invoke(searchList[adapterPosition], adapterPosition)
            }
        }
    }
}