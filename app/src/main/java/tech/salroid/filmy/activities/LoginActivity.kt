package tech.salroid.filmy.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityLoginBinding
import tech.salroid.filmy.views.CustomToast

class LoginActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    private var tokenization = false
    private var requestToken: String? = null
    private var nightMode = false
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setTitle(" ")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val typeface = ResourcesCompat.getFont(this, R.font.rubik)
        binding.logo.setTypeface(typeface)

        if (nightMode) allThemeLogic()
        loginNow()
    }

    private fun allThemeLogic() {
        binding.logo.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun loginNow() {
        progressDialog = ProgressDialog(this).apply {
            setTitle("TMDB Login")
            setMessage("Please wait...")
            isIndeterminate = true
        }
        progressDialog?.show()
        logInBackground()
    }

    private fun logInBackground() {
      /*  val apiKey = BuildConfig.TMDB_API_KEY
        val baseUrl = "https://api.themoviedb.org/3/authentication/token/new?api_key=$apiKey"
        val jsonObjectRequest = JsonObjectRequest(baseUrl, null,
                { response -> parseOutput(response) }) { error ->
            Log.e(
                    "webi",
                    "Volley Error: " + error.cause
            )
        }
        tmdbrequestQueue.add(jsonObjectRequest)*/
    }

    private fun parseOutput(response: JSONObject) {
        try {
            val status = response.getBoolean("success")
            if (status) {
                requestToken = response.getString("request_token")
                tokenization = true
                validateToken(requestToken)
            } else {
                progressDialog!!.dismiss()
                CustomToast.show(this, "Failed to login", false)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun validateToken(requestToken: String?) {
        val url = "https://www.themoviedb.org/authenticate/$requestToken"
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    override fun onResume() {
        super.onResume()
        if (tokenization && requestToken != null) {
            val apiKey = BuildConfig.TMDB_API_KEY
            val sessionQuery =
                    "https://api.themoviedb.org/3/authentication/session/new?api_key=$apiKey&request_token=$requestToken"
            querySession(sessionQuery)
        }
    }

    private fun querySession(session_query: String) {
       /* val jsonObjectRequest = JsonObjectRequest(
                session_query,
                null, { response -> parseSession(response) }) { error ->
            Log.e(
                    "webi",
                    "Volley Error: " + error.cause
            )
        }
        tmdbrequestQueue.add(jsonObjectRequest)*/
    }

    private fun parseSession(response: JSONObject) {
        try {
            val status = response.getBoolean("success")
            if (status) {
                val sessionId = response.getString("session_id")
                progressDialog?.dismiss()
                val resultIntent = Intent()
                resultIntent.putExtra("session_id", sessionId)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                progressDialog?.dismiss()
                CustomToast.show(this, "Failed to login", false)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}