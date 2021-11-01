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
import tech.salroid.filmy.data_classes.CharacterDetailsData
import tech.salroid.filmy.databinding.ActivityDetailedCastBinding
import tech.salroid.filmy.fragment.FullReadFragment
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork
import java.lang.Exception

class CharacterDetailsActivity : AppCompatActivity(),
    CharacterDetailsActivityAdapter.ClickListener {

    private var characterId: String? = null
    private var characterTitle: String? = null
    private var movieJson: String? = null
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
            if (!(movieJson == null && characterTitle == null)) {
                val intent = Intent(this@CharacterDetailsActivity, FullMovieActivity::class.java)
                intent.putExtra("cast_json", movieJson)
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
        detailedMovieAndCast
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

    private val detailedMovieAndCast: Unit
        get() {
            val volleySingleton = TmdbVolleySingleton.getInstance()
            val requestQueue = volleySingleton.requestQueue
            val apiKey = BuildConfig.TMDB_API_KEY

            val baseUrlPersonDetail =
                "https://api.themoviedb.org/3/person/$characterId?api_key=$apiKey"
            val baseUrlPeopleMovies =
                "https://api.themoviedb.org/3/person/$characterId/movie_credits?api_key=$apiKey"

            val personDetailRequest = JsonObjectRequest(
                baseUrlPersonDetail,
                null, { response ->
                    parsePersonDetails(response.toString())
                }) { error ->
                Log.e("webi", "Volley Error: " + error.cause)
            }

            val personMovieDetailRequest = JsonObjectRequest(baseUrlPeopleMovies, null,
                { response ->
                    movieJson = response.toString()
                    parsePersonMovies(response.toString())
                }
            ) { error -> Log.e("webi", "Volley Error: " + error.cause) }
            requestQueue.add(personDetailRequest)
            requestQueue.add(personMovieDetailRequest)
        }

    override fun itemClicked(setterGetterchar: CharacterDetailsData, position: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("id", setterGetterchar.char_id)
        intent.putExtra("title", setterGetterchar.char_movie)
        intent.putExtra("network_applicable", true)
        intent.putExtra("activity", false)
        startActivity(intent)
    }

    private fun parsePersonDetails(detailsResult: String) {
        try {
            val jsonObject = JSONObject(detailsResult)
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
        val par = CharacterDetailActivityParseWork(this, cast_result)
        val charList = par.char_parse_cast()
        val charAdapter = CharacterDetailsActivityAdapter(this, charList, true)
        charAdapter.setClickListener(this)
        binding.characterMovies.adapter = charAdapter
        if (charList.size > 4) binding.more.visibility = View.VISIBLE else binding.more.visibility =
            View.INVISIBLE
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

    override fun onStop() {
        super.onStop()
        Glide.with(this).clear(binding.displayProfile)
    }
}