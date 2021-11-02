package tech.salroid.filmy.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.FilmyApplication.Companion.context
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.databinding.CustomRowBinding

class MainActivityAdapter(
    private var dataCursor: Cursor? = null,
    private val clickListener: ((Cursor) -> Unit)? = null
) :
    RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainActivityViewHolder, position: Int) {
        dataCursor?.let { holder.bindData(it) }
    }

    override fun getItemCount(): Int = if (dataCursor == null) 0 else dataCursor!!.count

    fun swapCursor(cursor: Cursor?): Cursor? {

        if (dataCursor === cursor) {
            return null
        }

        val oldCursor = dataCursor
        dataCursor = cursor
        if (cursor != null) {
            notifyDataSetChanged()
        }

        return oldCursor
    }

    inner class MainActivityViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.main.setOnClickListener {
                dataCursor?.let {
                    it.moveToPosition(adapterPosition)
                    clickListener?.invoke(it)
                }
            }
        }

        fun bindData(dataCursor: Cursor) {
            dataCursor.moveToPosition(adapterPosition)

            val titleIndex = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)
            val posterIndex = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_POSTER_LINK)
            val yearIndex = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_YEAR)
            val movieTitle = dataCursor.getString(titleIndex)
            val moviePoster = dataCursor.getString(posterIndex)
            val movieYear = dataCursor.getInt(yearIndex)

            binding.title.text = "$movieTitle / $movieYear"

            Glide.with(context)
                .load(moviePoster)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.poster)
        }
    }
}