package tech.salroid.filmy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.login.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiStateToken = MutableStateFlow<RequestTokenResponse?>(null)
    private val _uiStateProfile = MutableStateFlow<Profile?>(null)
    private val _isLoggingOut = MutableStateFlow(false)
    val uiStateToken: StateFlow<RequestTokenResponse?> = _uiStateToken.asStateFlow()
    val uiStateProfile: StateFlow<Profile?> = _uiStateProfile.asStateFlow()
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    var requestToken: String? = null
    var accessToken: String? = null
    var sessionId: String? = null

    init {
        getProfileFromLocal()
    }

    fun getRequestToken() {
        viewModelScope.launch {
            val requestTokenData = RequestTokenData(redirectTo = "https://www.themoviedb.org/")
            accountRepository.getRequestToken(requestTokenData)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect {
                    requestToken = it.requestToken
                    _uiStateToken.emit(it)
                }
        }
    }

    fun getAccessToken() {
        viewModelScope.launch {
            val requestTokenData = RequestTokenData(requestToken = requestToken)
            accountRepository.getAccessToken(requestTokenData)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect {
                    accessToken = it.accessToken
                    getSession()
                }
        }
    }

    private fun getSession() {
        viewModelScope.launch {
            val accessTokenData = AccessTokenData(accessToken = accessToken)
            accountRepository.getSession(accessTokenData)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect {
                    sessionId = it.sessionId
                    accountRepository.storeSessionId(sessionId)
                    sessionId?.let { session ->
                        getProfile(session)
                    }
                }
        }
    }

    private fun getProfile(sessionId: String) {
        viewModelScope.launch {
            accountRepository.getProfile(sessionId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { profile ->
                    _uiStateProfile.emit(profile)
                    // Save Profile in local
                    viewModelScope.launch(Dispatchers.IO) {
                        accountRepository.saveProfileToLocal(profile)
                    }
                }
        }
    }

    private fun getProfileFromLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            // A cached profile with no matching session is stale (e.g. a prior
            // logout's cleanup got interrupted) - don't show a "logged in" UI
            // for a session that no longer exists.
            if (!accountRepository.isLoggedIn()) {
                accountRepository.clearProfile()
                _uiStateProfile.emit(null)
                return@launch
            }
            val profile = accountRepository.getProfileFromLocal()
            _uiStateProfile.emit(profile)
        }
    }

    fun logout() {
        viewModelScope.launch {
            val sessionId = accountRepository.getSessionIdFromPref()
            _isLoggingOut.value = true

            // Best-effort server-side revoke. Whether this succeeds, fails,
            // or returns success=false, the local logout below still always
            // runs — the user should never be stuck showing as "logged in"
            // in the UI just because the network call had a hiccup.
            if (sessionId != null) {
                accountRepository.deleteSession(sessionId)
                    .flowOn(Dispatchers.IO)
                    .catch { it.printStackTrace() }
                    .collect { }
            }

            withContext(Dispatchers.IO) {
                accountRepository.clearProfile()
            }
            accountRepository.storeSessionId(null)
            _uiStateProfile.emit(null)
            _isLoggingOut.value = false
        }
    }
}