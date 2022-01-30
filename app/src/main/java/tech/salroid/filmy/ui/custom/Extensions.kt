package tech.salroid.filmy.ui.custom

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import tech.salroid.filmy.R

fun View.showSnackBar(msg: String) {
    val snackBar = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
    val snackBarView: View = snackBar.view
    snackBarView.background = (ContextCompat.getDrawable(this.context, R.drawable.snackbar_bg))
    val textView = snackBarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    textView.setTextColor(ContextCompat.getColor(this.context, R.color.white))
    snackBar.show()
}

fun View.visible(){
    this.visibility = View.VISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}
