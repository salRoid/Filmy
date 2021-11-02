package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import tech.salroid.filmy.custom_adapter.CastAdapter
import tech.salroid.filmy.R
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.view.MenuItem
import android.view.View
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork
import tech.salroid.filmy.data.CastMemberDetailsData
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import tech.salroid.filmy.databinding.ActivityFullCastBinding

class FullCastActivity : AppCompatActivity(), CastAdapter.ClickListener {

    private var castResult: String? = null
    private var nightMode = false
    private lateinit var binding: ActivityFullCastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)
        binding = ActivityFullCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.fullCastRecycler.layoutManager = LinearLayoutManager(this@FullCastActivity)

        castResult = intent?.getStringExtra("cast_json")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent?.getStringExtra("toolbar_title")

        val par = MovieDetailsActivityParseWork(castResult)
        val castList = par.parseCastMembers()
        val fullCastAdapter = CastAdapter(this, castList, false)
        fullCastAdapter.setClickListener(this)
        binding.fullCastRecycler.adapter = fullCastAdapter
    }

    override fun itemClicked(setterGetter: CastMemberDetailsData, position: Int, view: View) {
        val intent = Intent(this, CharacterDetailsActivity::class.java)
        intent.putExtra("id", setterGetter.castId)
        val p1 = Pair.create(view.findViewById<View>(R.id.cast_poster), "profile")
        val p2 = Pair.create(view.findViewById<View>(R.id.cast_name), "name")
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2)
        startActivity(intent, options.toBundle())
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}