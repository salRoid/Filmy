package tech.salroid.filmy.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import tech.salroid.filmy.R

object CustomToast {

    @JvmStatic
    fun show(context: Context?, message: String?, showImage: Boolean) {

        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val text = layout.findViewById<View>(R.id.text) as TextView
        text.text = message

        val imageView = layout.findViewById<View>(R.id.image) as ImageView
        if (!showImage) imageView.visibility = View.GONE
        layout.requestLayout()

        val toast = Toast(context.applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}