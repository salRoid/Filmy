package tech.salroid.filmy.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.databinding.FragmentSettingsBinding
import tech.salroid.filmy.ui.home.LoginViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setUpClickListeners()
        observerUiStates()
        return binding.root
    }

    private fun setUpClickListeners() {
        binding.btLogin.setOnClickListener {
            binding.progressBar.isVisible = true
            binding.btLogin.isClickable = false
            binding.btLogin.isFocusable = false
            viewModel.getRequestToken()
        }

        binding.btLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun observerUiStates() {
        lifecycleScope.launch {
            viewModel.uiStateToken.collect {
                it?.let {
                    it.requestToken?.let { requestToken ->
                        if (viewModel.accessToken == null)
                            validateRequestToken(requestToken)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateProfile.collect {
                binding.progressBar.isVisible = false
                it?.let {
                    binding.btLogout.isClickable = true
                    binding.btLogout.isFocusable = true
                    showProfile(it)
                } ?: kotlin.run {
                    binding.btLogin.isClickable = true
                    binding.btLogin.isFocusable = true
                    showLogin()
                }
            }
        }
    }

    private fun showLogin() {
        binding.loginTitle.text = getString(R.string.login_message)
        binding.loginSubTitle.text = getString(R.string.login_message_benefit)
        binding.avatarImage.setImageResource(R.drawable.default_avatar)
        binding.btLogin.isVisible = true
        binding.btLogout.isVisible = false
    }

    private fun showProfile(profile: Profile) {
        binding.loginTitle.text = profile.name ?: ".."
        binding.loginSubTitle.text = profile.username

        var avatarUrl = profile.avatar?.gravatar?.getCompleteUrl()
        profile.avatar?.tmdb?.avatarPath?.let {
            avatarUrl = getString(R.string.member_profile_url, profile.avatar?.tmdb?.avatarPath)
        }

        Glide.with(requireContext())
            .load(avatarUrl)
            .placeholder(R.drawable.default_avatar)
            .error(R.drawable.default_avatar)
            .into(binding.avatarImage)

        binding.btLogin.isVisible = false
        binding.btLogout.isVisible = true
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.requestToken != null && viewModel.sessionId == null) {
            viewModel.getAccessToken()
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.logout_confirmation))
            .setNegativeButton(R.string.yes) { _, _ ->
                binding.progressBar.isVisible = true
                binding.btLogout.isClickable = false
                binding.btLogout.isFocusable = false
                viewModel.logout()
            }
            .setPositiveButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun validateRequestToken(requestToken: String) {
        val url = "https://www.themoviedb.org/auth/access?request_token=$requestToken"
         /* CustomTabsIntent.Builder().setDefaultColorSchemeParams(
              CustomTabColorSchemeParams.Builder()
                  .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPR))
                  .build()
          ).build().run {
              launchUrl(requireActivity(), Uri.parse(url))
          }*/
        mCustomTabLauncher.launch(url)
    }

    private val mCustomTabLauncher =
        registerForActivityResult(object : ActivityResultContract<String, Int>() {
            override fun createIntent(context: Context, input: String): Intent {
                val builder = CustomTabsIntent.Builder()
                    .setInitialActivityHeightPx(binding.root.height - binding.loginCardContainer.height)
                val customTabsIntent = builder.build().intent
                customTabsIntent.data = Uri.parse(input)
                return customTabsIntent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Int = resultCode

        }) {
        }
}