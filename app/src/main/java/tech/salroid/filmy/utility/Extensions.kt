package tech.salroid.filmy.utility

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tech.salroid.filmy.R
import java.text.SimpleDateFormat
import java.util.*

fun View.showSnackBar(msg: String) {
    val snackBar = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
    val snackBarView: View = snackBar.view
    snackBarView.background = (ContextCompat.getDrawable(this.context, R.drawable.snackbar_bg))
    val textView =
        snackBarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    textView.setTextColor(ContextCompat.getColor(this.context, R.color.white))
    snackBar.show()
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun String.toReadableDate(): String {

    if (this.isEmpty()){
        return this
    }

    val fromDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val toDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return toDateFormat.format(fromDateFormat.parse(this))
}

fun MaterialSearchView.getQueryTextChangeStateFlow(): StateFlow<String> {
    val query = MutableStateFlow("")

    setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            val trimmedQuery = newText.trim { it <= ' ' }
            val finalQuery = trimmedQuery.replace(" ", "-")
            query.value = finalQuery
            return true
        }
    })
    return query
}