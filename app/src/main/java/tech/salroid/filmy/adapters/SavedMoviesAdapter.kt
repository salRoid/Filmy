package tech.salroid.filmy.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.databinding.CustomRowBinding

class SavedMoviesAdapter(
    private var dataCursor: Cursor? = null,
    private val clickListener: ((String, String) -> Unit)? = null,
    private val longClickListener: ((Cursor, Int) -> Unit)? = null
) :
    RecyclerView.Adapter<SavedMoviesAdapter.SavedMoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedMoviesViewHolder {
        val binding =
            CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedMoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedMoviesViewHolder, position: Int) {
        dataCursor?.moveToPosition(position)
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

    inner class SavedMoviesViewHolder(private val binding: CustomRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(dataCursor: Cursor) {

            val posterIndex = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_POSTER_LINK)
            val moviePoster = dataCursor.getString(posterIndex)

            Glide.with(binding.root.context)
                .load(moviePoster)
                .into(binding.poster)
        }

        init {
            binding.main.setOnClickListener {
                dataCursor?.let {
                    it.moveToPosition(adapterPosition)
                    val idIndex = it.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)
                    val titleIndex = it.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE)
                    clickListener?.invoke(it.getString(idIndex), it.getString(titleIndex))
                }
            }

            binding.main.setOnLongClickListener {
                dataCursor?.moveToPosition(adapterPosition)
                dataCursor?.let { it1 -> longClickListener?.invoke(it1, adapterPosition) }
                true
            }
        }
    }
}