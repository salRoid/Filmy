package tech.salroid.filmy.ui.collections

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.adapters.CollectionsPagerAdapter
import tech.salroid.filmy.databinding.FragmentCollectionsBinding

@AndroidEntryPoint
class CollectionsFragment : Fragment() {

    private lateinit var binding: FragmentCollectionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        setupViewPager()
        return binding.root
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
                    tab.icon = ContextCompat.getDrawable(
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