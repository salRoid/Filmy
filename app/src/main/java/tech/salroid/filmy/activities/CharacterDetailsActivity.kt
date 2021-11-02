package tech.salroid.filmy.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter
import tech.salroid.filmy.data.PersonMovieDetailsData
import tech.salroid.filmy.databinding.ActivityDetailedCastBinding
import tech.salroid.filmy.fragment.FullReadFragment
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.parser.CharacterDetailsActivityParseWork
import java.lang.Exception

class CharacterDetailsActivity : AppCompatActivity(),
        CharacterDetailsActivityAdapter.ClickListener {

    private var characterId: String? = null
    private var characterTitle: String? = null
    private var moviesJson: String? = null
    private var characterBio: String? = null
    private var fullReadFragment: FullReadFragment? = null
    private var nightMode = false
    private lateinit var binding: ActivityDetailedCastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)
        binding = ActivityDetailedCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.logo.typeface = ResourcesCompat.getFont(this, R.font.rubik)

        if (nightMode) {
            allThemeLogic()
        }

        binding.more.setOnClickListener {
            if (!(moviesJson == null && characterTitle == null)) {
                val intent = Intent(this@CharacterDetailsActivity, FullMovieActivity::class.java)
                intent.putExtra("cast_json", moviesJson)
                intent.putExtra("toolbar_title", characterTitle)
                startActivity(intent)
            }
        }

        binding.characterMovies.layoutManager = LinearLayoutManager(this@CharacterDetailsActivity)
        binding.characterMovies.isNestedScrollingEnabled = false

        binding.overviewContainer.setOnClickListener {
            if (characterTitle != null && characterBio != null) {
                fullReadFragment = FullReadFragment()
                val args = Bundle()
                args.putString("title", characterTitle)
                args.putString("desc", characterBio)
                fullReadFragment!!.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fullReadFragment!!).addToBackStack("DESC").commit()
            }
        }
        val intent = intent
        if (intent != null) {
            characterId = intent.getStringExtra("id")
        }
        personalDetailsAndMovies
    }

    private fun allThemeLogic() {
        binding.logo.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }

    private val personalDetailsAndMovies: Unit
        get() {
            val volleySingleton = TmdbVolleySingleton.getInstance()
            val requestQueue = volleySingleton.requestQueue
            val apiKey = BuildConfig.TMDB_API_KEY

            val baseUrlPersonalDetails =
                    "https://api.themoviedb.org/3/person/$characterId?api_key=$apiKey"
            val baseUrlPersonMovies =
                    "https://api.themoviedb.org/3/person/$characterId/movie_credits?api_key=$apiKey"

            val personalDetailsRequest = JsonObjectRequest(baseUrlPersonalDetails, null,
                    { response ->
                        parsePersonalDetails(response.toString())
                    }
            ) { error -> Log.e("webi", "Volley Error: " + error.cause) }

            val personMoviesDetailsRequest = JsonObjectRequest(baseUrlPersonMovies, null,
                    { response ->
                        moviesJson = response.toString()
                        parsePersonMovies(response.toString())
                    }
            ) { error -> Log.e("webi", "Volley Error: " + error.cause) }

            requestQueue.add(personalDetailsRequest)
            requestQueue.add(personMoviesDetailsRequest)
        }

    override fun itemClicked(movie: PersonMovieDetailsData, position: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("id", movie.movieId)
        intent.putExtra("title", movie.movieTitle)
        intent.putExtra("network_applicable", true)
        intent.putExtra("activity", false)
        startActivity(intent)
    }

    private fun parsePersonalDetails(personalDetailsResponse: String) {
        try {
            val jsonObject = JSONObject(personalDetailsResponse)
            val dataName = jsonObject.getString("name")
            val dataProfile =
                    "http://image.tmdb.org/t/p/w185" + jsonObject.getString("profile_path")
            val dataOverview = jsonObject.getString("biography")
            val dataBirthday = jsonObject.getString("birthday")
            val dataBirthPlace = jsonObject.getString("place_of_birth")

            characterTitle = dataName
            characterBio = dataOverview
            binding.name.text = dataName

            if (dataBirthday == "null") {
                binding.dob.visibility = View.GONE
            } else {
                binding.dob.text = dataBirthday
            }

            if (dataBirthPlace == "null") {
                binding.birthPlace.visibility = View.GONE
            } else {
                binding.birthPlace.text = dataBirthPlace
            }

            if (dataOverview.isEmpty()) {
                binding.overviewContainer.visibility = View.GONE
                binding.overview.visibility = View.GONE
            } else {
                if (Build.VERSION.SDK_INT >= 24) {
                    binding.overview.text = Html.fromHtml(dataOverview, Html.FROM_HTML_MODE_LEGACY)
                }
            }

            try {
                Glide.with(this)
                        .load(dataProfile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter().into(binding.displayProfile)
            } catch (e: Exception) {
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun parsePersonMovies(cast_result: String) {
        val par = CharacterDetailsActivityParseWork(this, cast_result)
        val moviesList = par.parsePersonMovies()
        val charAdapter = CharacterDetailsActivityAdapter(this, moviesList, true)
        charAdapter.setClickListener(this)
        binding.characterMovies.adapter = charAdapter
        if (moviesList.size > 4) {
            binding.more.visibility = View.VISIBLE
        } else {
            binding.more.visibility = View.INVISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            supportFinishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag("DESC") as FullReadFragment?
        if (fragment != null && fragment.isVisible) {
            supportFragmentManager.beginTransaction().remove(fullReadFragment!!).commit()
        } else {
            super.onBackPressed()
        }
    }
}