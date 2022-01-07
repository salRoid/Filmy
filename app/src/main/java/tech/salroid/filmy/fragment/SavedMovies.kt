package tech.salroid.filmy.fragment

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.adapters.SavedMoviesAdapter
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.databinding.FragmentSavedMoviesBinding
import tech.salroid.filmy.utility.Constants

class SavedMovies : Fragment(), LoaderManager.LoaderCallbacks<Cursor?> {

    private var mainActivityAdapter: SavedMoviesAdapter? = null
    private var _binding: FragmentSavedMoviesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedMoviesBinding.inflate(inflater, container, false)
        val view = binding.root

        val tabletSize = resources.getBoolean(R.bool.isTablet)
        when {
            tabletSize -> {
                when (resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        val gridLayoutManager = StaggeredGridLayoutManager(
                            6,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        binding.mySavedRecycler.layoutManager = gridLayoutManager
                    }
                    else -> {
                        val gridLayoutManager = StaggeredGridLayoutManager(
                            8,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        binding.mySavedRecycler.layoutManager = gridLayoutManager
                    }
                }
            }
            else -> {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    val gridLayoutManager = StaggeredGridLayoutManager(
                        3,
                        StaggeredGridLayoutManager.VERTICAL
                    )
                    binding.mySavedRecycler.layoutManager = gridLayoutManager
                } else {
                    val gridLayoutManager = StaggeredGridLayoutManager(
                        5,
                        StaggeredGridLayoutManager.VERTICAL
                    )
                    binding.mySavedRecycler.layoutManager = gridLayoutManager
                }
            }
        }
        mainActivityAdapter =
            SavedMoviesAdapter(clickListener = { movieId, title -> itemClicked(movieId, title) },
                longClickListener = { dataCursor, position ->
                    itemLongClicked(
                        dataCursor,
                        position
                    )
                })
        binding.mySavedRecycler.adapter = mainActivityAdapter
        return view
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        val selection =
            FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_FLAG + "= ?"
        val selectionArgs = arrayOf(Constants.FLAG_OFFLINE.toString())

        return CursorLoader(
            activity!!,
            FilmContract.SaveEntry.CONTENT_URI,
            GET_SAVE_COLUMNS,
            selection,
            selectionArgs,
            "_ID DESC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        if (cursor != null && cursor.count > 0) mainActivityAdapter?.swapCursor(cursor)
        else binding.emptyContainer.visibility = View.VISIBLE
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        mainActivityAdapter?.swapCursor(null)
        binding.emptyContainer.visibility = View.VISIBLE
    }

    private fun itemClicked(movieId: String, title: String) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("saved_database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("title", title)
        intent.putExtra("id", movieId)
        startActivity(intent)
    }

    private fun itemLongClicked(dataCursor: Cursor, position: Int) {
        val adb = MaterialAlertDialogBuilder(activity!!)
        val arrayAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_list_item_1)
        arrayAdapter.add("Remove")

        adb.setAdapter(arrayAdapter) { _: DialogInterface?, i: Int ->
            val deleteSelection =
                FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_ID + " = ? AND " +
                        FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_FLAG + " = ? "
            val flagIndex = dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_FLAG)
            val flag = dataCursor.getInt(flagIndex)

            val deletionArgs = arrayOf(
                dataCursor.getString(dataCursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)),
                flag.toString()
            )

            val deletionId = context!!.contentResolver.delete(
                FilmContract.SaveEntry.CONTENT_URI,
                deleteSelection,
                deletionArgs
            ).toLong()

            if (deletionId != -1L) {
                mainActivityAdapter?.notifyItemRemoved(position)
                if (mainActivityAdapter?.itemCount == 1) binding.mySavedRecycler.visibility =
                    View.GONE
            }
        }
        adb.show()
    }

    override fun onResume() {
        super.onResume()
        activity?.supportLoaderManager?.initLoader(SAVED_DETAILS_LOADER, null, this)
    }

    companion object {
        private const val SAVED_DETAILS_LOADER = 3
        private val GET_SAVE_COLUMNS = arrayOf(
            FilmContract.SaveEntry.SAVE_ID,
            FilmContract.SaveEntry.SAVE_TITLE,
            FilmContract.SaveEntry.SAVE_BANNER,
            FilmContract.SaveEntry.SAVE_DESCRIPTION,
            FilmContract.SaveEntry.SAVE_TAGLINE,
            FilmContract.SaveEntry.SAVE_TRAILER,
            FilmContract.SaveEntry.SAVE_RATING,
            FilmContract.SaveEntry.SAVE_LANGUAGE,
            FilmContract.SaveEntry.SAVE_RELEASED,
            FilmContract.SaveEntry.getID(),
            FilmContract.SaveEntry.SAVE_YEAR,
            FilmContract.SaveEntry.SAVE_CERTIFICATION,
            FilmContract.SaveEntry.SAVE_RUNTIME,
            FilmContract.SaveEntry.SAVE_POSTER_LINK,
            FilmContract.SaveEntry.SAVE_FLAG
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}