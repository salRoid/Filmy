package tech.salroid.filmy.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter
import tech.salroid.filmy.data_classes.CharacterDetailsData
import tech.salroid.filmy.fragment.FullReadFragment
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork
import java.lang.Exception

class CharacterDetailsActivity : AppCompatActivity(), CharacterDetailsActivityAdapter.ClickListener {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @BindView(R.id.more)
    var more: TextView? = null

    @BindView(R.id.name)
    var name: TextView? = null

    @BindView(R.id.overview)
    var overview: TextView? = null

    @BindView(R.id.dob)
    var dateOfBirth: TextView? = null

    @BindView(R.id.birth_place)
    var birthPlace: TextView? = null

    @BindView(R.id.display_profile)
    var profileHolder: CircularImageView? = null

    @BindView(R.id.character_movies)
    var movies: RecyclerView? = null

    @BindView(R.id.overview_container)
    var overViewContainer: FrameLayout? = null

    @BindView(R.id.logo)
    var logo: TextView? = null
    var co: Context = this
    private var character_id: String? = null
    private var character_title: String? = null
    private var movie_json: String? = null
    private var character_bio: String? = null
    private var fullReadFragment: FullReadFragment? = null
    private var nightMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_cast)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        supportActionBar!!.setTitle("")
        val typeface = ResourcesCompat.getFont(this, R.font.rubik)
        logo!!.setTypeface(typeface)
        if (nightMode) {
            allThemeLogic()
        }
        more!!.setOnClickListener {
            if (!(movie_json == null && character_title == null)) {
                val intent = Intent(this@CharacterDetailsActivity, FullMovieActivity::class.java)
                intent.putExtra("cast_json", movie_json)
                intent.putExtra("toolbar_title", character_title)
                startActivity(intent)
            }
        }
        movies!!.layoutManager = LinearLayoutManager(this@CharacterDetailsActivity)
        movies!!.isNestedScrollingEnabled = false
        overViewContainer!!.setOnClickListener {
            if (character_title != null && character_bio != null) {
                fullReadFragment = FullReadFragment()
                val args = Bundle()
                args.putString("title", character_title)
                args.putString("desc", character_bio)
                fullReadFragment!!.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fullReadFragment!!).addToBackStack("DESC").commit()
            }
        }
        val intent = intent
        if (intent != null) {
            character_id = intent.getStringExtra("id")
        }
        detailedMovieAndCast
    }

    private fun allThemeLogic() {
        logo!!.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }

    private val detailedMovieAndCast: Unit
        private get() {
            val volleySingleton = TmdbVolleySingleton.getInstance()
            val requestQueue = volleySingleton.requestQueue
            val api_key = BuildConfig.TMDB_API_KEY
            val BASE_URL_PERSON_DETAIL = "https://api.themoviedb.org/3/person/$character_id?api_key=$api_key"
            val BASE_URL_PEOPLE_MOVIES = "https://api.themoviedb.org/3/person/$character_id/movie_credits?api_key=$api_key"
            val personDetailRequest = JsonObjectRequest(BASE_URL_PERSON_DETAIL, null,
                    { response -> personDetailsParsing(response.toString()) }
            ) { error -> Log.e("webi", "Volley Error: " + error.cause) }
            val personMovieDetailRequest = JsonObjectRequest(BASE_URL_PEOPLE_MOVIES, null,
                    { response ->
                        movie_json = response.toString()
                        cast_parseOutput(response.toString())
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

    fun personDetailsParsing(detailsResult: String?) {
        try {
            val jsonObject = JSONObject(detailsResult)
            val dataName = jsonObject.getString("name")
            val dataProfile = "http://image.tmdb.org/t/p/w185" + jsonObject.getString("profile_path")
            val dataOverview = jsonObject.getString("biography")
            val dataBirthday = jsonObject.getString("birthday")
            val dataBirthPlace = jsonObject.getString("place_of_birth")
            character_title = dataName
            character_bio = dataOverview
            name!!.text = dataName
            if (dataBirthday == "null") {
                dateOfBirth!!.visibility = View.GONE
            } else {
                dateOfBirth!!.text = dataBirthday
            }
            if (dataBirthPlace == "null") {
                birthPlace!!.visibility = View.GONE
            } else {
                birthPlace!!.text = dataBirthPlace
            }
            if (dataOverview.length <= 0) {
                overViewContainer!!.visibility = View.GONE
                overview!!.visibility = View.GONE
            } else {
                if (Build.VERSION.SDK_INT >= 24) {
                    overview!!.text = Html.fromHtml(dataOverview, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    overview!!.text = Html.fromHtml(dataOverview)
                }
            }
            try {
                Glide.with(co)
                        .load(dataProfile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter().into(profileHolder!!)
            } catch (e: Exception) {
                //Log.d(LOG_TAG, e.getMessage());
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun cast_parseOutput(cast_result: String) {
        val par = CharacterDetailActivityParseWork(this, cast_result)
        val char_list = par.char_parse_cast()
        val char_adapter = CharacterDetailsActivityAdapter(this, char_list, true)
        char_adapter.setClickListener(this)
        movies!!.adapter = char_adapter
        if (char_list.size > 4) more!!.visibility = View.VISIBLE else more!!.visibility = View.INVISIBLE
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
        Glide.with(this).clear(profileHolder!!)
    }
}