package tech.salroid.filmy.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tech.salroid.filmy.FilmyApplication.Companion.context
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.databinding.CustomRowBinding

class MainActivityAdapter(
    private var dataCursor: Cursor? = null,
    private val clickListener: ((Cursor) -> Unit)? = null
) : RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder>() {

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

            val posterIndex = dataCursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_POSTER_LINK)
            val moviePoster = dataCursor.getString(posterIndex)

            Glide.with(context)
                .load(moviePoster)
                .into(binding.poster)
        }
    }
}