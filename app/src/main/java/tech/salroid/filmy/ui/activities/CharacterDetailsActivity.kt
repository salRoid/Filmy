package tech.salroid.filmy.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import org.json.JSONException
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastDetailsResponse
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.data.local.model.CastMovieDetailsResponse
import tech.salroid.filmy.data.network.NetworkUtil
import tech.salroid.filmy.databinding.ActivityDetailedCastBinding
import tech.salroid.filmy.ui.activities.fragment.FullReadFragment
import tech.salroid.filmy.ui.adapters.CharacterDetailsActivityAdapter
import tech.salroid.filmy.utility.toReadableDate

class CharacterDetailsActivity : AppCompatActivity() {

    private var characterId: String? = null
    private var characterTitle: String? = null
    private var moviesList: ArrayList<CastMovie>? = null
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

        binding.more.setOnClickListener {
            if (!(moviesList == null && characterTitle == null)) {
                val intent = Intent(this@CharacterDetailsActivity, FullMovieActivity::class.java)
                intent.putExtra("cast_movies", moviesList)
                intent.putExtra("toolbar_title", characterTitle)
                startActivity(intent)
            }
        }

        binding.characterMovies.layoutManager = GridLayoutManager(this@CharacterDetailsActivity, 3)
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

        characterId?.let {
            NetworkUtil.getCastDetails(it, { details ->
                details?.let { it1 ->
                    showPersonalDetails(it1)
                }
            }, {
            })

            NetworkUtil.getCastMovieDetails(it, { details ->
                details?.let { it1 ->
                    this.moviesList = it1.castMovies
                    showPersonMovies(it1)
                }
            }, {
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }


    private fun showPersonalDetails(details: CastDetailsResponse) {
        try {
            val dataName = details.name
            val dataProfile = "http://image.tmdb.org/t/p/w185" + details.profilePath
            val dataOverview = details.biography
            val dataBirthday = details.birthday
            val dataBirthPlace = details.placeOfBirth

            characterTitle = dataName
            characterBio = dataOverview
            binding.name.text = dataName

            if (dataBirthday == "null") {
                binding.dob.visibility = View.GONE
            } else {
                binding.dob.text = dataBirthday?.toReadableDate()
            }

            if (dataBirthPlace == "null") {
                binding.birthPlace.visibility = View.GONE
            } else {
                binding.birthPlace.text = dataBirthPlace
            }

            if (dataOverview?.isEmpty() == true) {
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
                    .fitCenter().into(binding.displayProfile)
            } catch (e: Exception) {
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showPersonMovies(castMovieDetails: CastMovieDetailsResponse) {

        val charAdapter =
            CharacterDetailsActivityAdapter(castMovieDetails.castMovies, true) { movie, _ ->
                val intent = Intent(this, MovieDetailsActivity::class.java)
                intent.putExtra("id", movie.id.toString())
                intent.putExtra("title", movie.title)
                intent.putExtra("network_applicable", true)
                intent.putExtra("activity", false)
                startActivity(intent)
            }

        binding.characterMovies.adapter = charAdapter
        if (castMovieDetails.castMovies.size > 4) {
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