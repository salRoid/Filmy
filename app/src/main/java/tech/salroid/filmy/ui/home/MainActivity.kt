package tech.salroid.filmy.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityMainBinding
import tech.salroid.filmy.ui.intro.FilmyIntroActivity
import tech.salroid.filmy.ui.search.SearchViewModel
import tech.salroid.filmy.utility.PreferenceHelper.isColdStart
import tech.salroid.filmy.utility.PreferenceHelper.setColdStartDone
import tech.salroid.filmy.utility.themeSystemBars

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val viewModelSearch: SearchViewModel by viewModels()
    private var throughShortcut: Boolean? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        // For recreate case
        if (savedInstanceState != null && Build.VERSION.SDK_INT >= 35) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        // For Backward Compatibility
        WindowCompat.enableEdgeToEdge(window)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        throughShortcut = intent.getBooleanExtra("throughShortcut", false)
        themeSystemBars(lightStatusBar = true, navigationColorAsStatus = false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModelSearch.isSearchOpen.value) {
                    viewModelSearch.closeSearch()
                } else {
                    if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
        introLogic()
        setupNavigation()
        observerUiStates()
    }

    private fun observerUiStates() {
        lifecycleScope.launch {
            /*viewModelSearch.uiStateSearchView.collect {
                it?.let {
                    val margin = when (it) {
                        SearchViewUiState.Hidden -> 0
                        SearchViewUiState.Visible -> binding.navigationBarView.height
                    }
                    animateBottomNavigation(margin)
                }
            }*/
        }

        lifecycleScope.launch {
            viewModel.uiStateNavigationVisibility.collect {
                // binding.mainAppBarContent.root.isVisible = it
                // animateTopNavigation(it)
            }
        }
    }

    private fun animateBottomNavigation(margin: Int) {
        /*binding.navigationBarView.clearAnimation()
        binding.navigationBarView
            .animate()
            .translationY(margin.toFloat()).apply {
                duration = 175
                startDelay = if (margin == 0) 100 else 0
                interpolator =
                    if (margin == 0) LinearOutSlowInInterpolator() else FastOutLinearInInterpolator()
            }*/
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.navHostFragment)
        NavigationUI.setupWithNavController(binding.navigationBarView, navController)

        if (throughShortcut == true) {
            navController.navigate(R.id.collections)
            binding.navigationBarView.selectedItemId = R.id.collections
        }
    }

    private fun introLogic() {
        if (isColdStart(this)) {
            val intent = Intent(this@MainActivity, FilmyIntroActivity::class.java)
            startActivity(intent)
            setColdStartDone(this)
        }
    }
}