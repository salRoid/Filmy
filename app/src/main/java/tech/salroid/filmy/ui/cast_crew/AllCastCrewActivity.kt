package tech.salroid.filmy.ui.cast_crew

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.elevation.SurfaceColors
import tech.salroid.filmy.data.local.model.CastCrew
import tech.salroid.filmy.databinding.ActivityFullCastBinding
import tech.salroid.filmy.ui.adapters.AllCastCrewAdapter
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.CAST_CREW_LIST
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.TOOLBAR_TITLE
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MEMBER_ID
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationFragment.Companion.IS_TV
import tech.salroid.filmy.utility.themeSystemBars

class AllCastCrewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullCastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeSystemBars(lightStatusBar = true)
        window.statusBarColor = SurfaceColors.SURFACE_3.getColor(this)

        binding = ActivityFullCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setBackgroundColor(SurfaceColors.SURFACE_3.getColor(this))
        binding.toolbar.title = intent?.getStringExtra(TOOLBAR_TITLE)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupAdapter()
    }

    private fun setupAdapter() {
        val castCrewList =
            intent?.getParcelableArrayListExtra<CastCrew>(CAST_CREW_LIST) as? ArrayList<CastCrew>
        val isTv = intent.getBooleanExtra(IS_TV, false)
        val adapter = castCrewList?.let {
            AllCastCrewAdapter(it, false) { castCrew, _, _ ->
                val id = when (castCrew) {
                    is CastCrew.CastData -> castCrew.cast.id
                    is CastCrew.CrewData -> castCrew.crew.id
                }
                val intent = Intent(this, CastCrewDetailsActivity::class.java)
                intent.putExtra(MEMBER_ID, id.toString())
                intent.putExtra(IS_TV, isTv)
                startActivity(intent)
            }
        }
        binding.recyclerView.adapter = adapter
    }
}