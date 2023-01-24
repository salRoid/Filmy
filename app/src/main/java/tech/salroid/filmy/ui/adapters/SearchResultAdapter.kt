package tech.salroid.filmy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.SearchResult
import tech.salroid.filmy.databinding.SearchCustomRowBinding
import tech.salroid.filmy.utility.toReadableDate

class SearchResultAdapter(
    private val searchList: List<SearchResult>,
    private val clickListener: ((SearchResult, Int) -> Unit)? = null
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val binding =
            SearchCustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        holder.bindData(searchList[position])
    }

    override fun getItemCount(): Int = searchList.size

    inner class SearchResultsViewHolder(private val binding: SearchCustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickListener?.invoke(searchList[adapterPosition], adapterPosition)
            }
        }

        fun bindData(searchData: SearchResult) {
            binding.movieName.text = searchData.originalTitle
            binding.movieYear.text = searchData.releaseDate?.toReadableDate()

            binding.root.context.let {
                Glide.with(it)
                    .load(it.getString(R.string.movie_poster_url, searchData.posterPath))
                    .into(binding.moviePoster)
            }
        }
    }
}