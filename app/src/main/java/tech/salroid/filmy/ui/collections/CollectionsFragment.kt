package tech.salroid.filmy.ui.collections

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.adapters.CollectionsPagerAdapter
import tech.salroid.filmy.databinding.FragmentCollectionsBinding
import tech.salroid.filmy.ui.home.MainViewModel
import tech.salroid.filmy.utility.PreferenceHelper.isDarkModeEnabled

@AndroidEntryPoint
class CollectionsFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var darkMode: Boolean = false
    private lateinit var binding: FragmentCollectionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        setupClickListener()
        setupViewPager()
        return binding.root
    }

    private fun setupClickListener() {
        binding.favContainer.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    CollectionTypeFragment::class.java
                )
            )
        }

        binding.watchlistContainer.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    CollectionTypeFragment::class.java
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        darkMode = isDarkModeEnabled(requireContext())
        if (darkMode) darkThemeLogic() else lightModeLogic()
    }

    private fun lightModeLogic() {
        binding.tabLayout.backgroundTintList = null
        val selectedColor = ContextCompat.getColor(requireActivity(), R.color.colorMore)
        val unSelectedColor = ContextCompat.getColor(requireActivity(), R.color.dark)
        binding.tabLayout.setSelectedTabIndicatorColor(selectedColor)
        binding.tabLayout.setTabTextColors(unSelectedColor, selectedColor)
        binding.tabLayout.tabIconTint =
            ContextCompat.getColorStateList(requireActivity(), R.color.tab_icon_tint)
    }

    private fun darkThemeLogic() {
        binding.tabLayout.backgroundTintList = ContextCompat.getColorStateList(
            requireActivity(),
            R.color.fullBlack
        )
        val selectedColor = ContextCompat.getColor(requireActivity(), R.color.colorMore)
        val unSelectedColor = ContextCompat.getColor(requireActivity(), R.color.grey3)
        binding.tabLayout.setSelectedTabIndicatorColor(selectedColor)
        binding.tabLayout.setTabTextColors(unSelectedColor, selectedColor)
        binding.tabLayout.tabIconTint =
            ContextCompat.getColorStateList(requireActivity(), R.color.tab_icon_tint_dark)
    }

    private fun setupViewPager() {
        binding.viewpager.adapter = CollectionsPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.favorite)
                    tab.icon =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_favorite_24)
                }
                1 -> {
                    tab.text = getString(R.string.watchlist)
                    tab.icon =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_round_bookmark_added_24
                        )
                }
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.tabLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
}