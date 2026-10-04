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
    private val _isLoggingOut = MutableStateFlow(false)
    private val _isAuthenticating = MutableStateFlow(false)
    val uiStateToken: StateFlow<RequestTokenResponse?> = _uiStateToken.asStateFlow()
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    /**
     * True from the moment login starts until it either succeeds (profile
     * saved) or fails at any step - including the user closing the Custom
     * Tab without approving the request token, which surfaces as a failure
     * when exchanging it for an access token. Never gets stuck on true.
     */
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    /**
     * Reactive to the local `profile` table via [AccountRepository.getProfileFlow]
     * - every screen's own [LoginViewModel] instance (Account, MyLists, details
     * screen, etc.) observes the same underlying row, so a login/logout
     * completed from any one of them is reflected everywhere else too,
     * without needing to navigate away and back.
     */
    val uiStateProfile: StateFlow<Profile?> = accountRepository.getProfileFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Whether the user's own v4 access token is on hand, which lists need on
     * top of the session. Reactive for the same reason as [uiStateProfile].
     * False while logged in means the login predates lists moving to v4, and
     * logging in once more fixes it.
     */
    val canManageLists: StateFlow<Boolean> = accountRepository.canManageListsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), accountRepository.canManageLists())

    var requestToken: String? = null
    var accessToken: String? = null
    var sessionId: String? = null
    private var accountObjectId: String? = null

    init {
        repairStaleLocalProfile()
    }

    fun getRequestToken() {
        _isAuthenticating.value = true
        viewModelScope.launch {
            val requestTokenData = RequestTokenData(redirectTo = "https://www.themoviedb.org/")
            accountRepository.getRequestToken(requestTokenData)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                    _isAuthenticating.value = false
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
                    // Reached when the user closed the Custom Tab without
                    // approving the request token - exchanging it then
                    // fails. Clear it so a later, unrelated app resume
                    // doesn't keep retrying a dead token.
                    it.printStackTrace()
                    requestToken = null
                    _isAuthenticating.value = false
                }.collect {
                    accessToken = it.accessToken
                    accountObjectId = it.accountId
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
                    _isAuthenticating.value = false
                }.collect {
                    // Logging in again over an existing session (to pick up
                    // list access) leaves the old one behind - revoke it.
                    val previousSessionId = accountRepository.getSessionIdFromPref()
                    sessionId = it.sessionId
                    accountRepository.storeSessionId(sessionId)
                    if (previousSessionId != null && previousSessionId != sessionId) {
                        revokeSession(previousSessionId)
                    }
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
                    _isAuthenticating.value = false
                }.collect { profile ->
                    // Writing here is enough - uiStateProfile is reactive to
                    // this same local table, so it (and every other screen's
                    // LoginViewModel instance) picks this up automatically.
                    withContext(Dispatchers.IO) {
                        accountRepository.saveProfileToLocal(profile)
                    }
                    // Stored last, so list access only ever appears alongside
                    // a complete login.
                    accountRepository.storeUserAccessToken(accessToken, accountObjectId)
                    _isAuthenticating.value = false
                }
        }
    }

    private fun revokeSession(sessionId: String) {
        viewModelScope.launch {
            accountRepository.deleteSession(sessionId)
                .flowOn(Dispatchers.IO)
                .catch { it.printStackTrace() }
                .collect { }
        }
    }

    /**
     * A cached profile with no matching session is stale (e.g. a prior
     * logout's cleanup got interrupted) - don't show a "logged in" UI for a
     * session that no longer exists. Just a repair write; uiStateProfile
     * picks up the result reactively.
     */
    private fun repairStaleLocalProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!accountRepository.isLoggedIn()) {
                accountRepository.clearProfile()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val sessionId = accountRepository.getSessionIdFromPref()
            val userAccessToken = accountRepository.getUserAccessToken()
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

            if (userAccessToken != null) {
                accountRepository.revokeUserAccessToken(userAccessToken)
                    .flowOn(Dispatchers.IO)
                    .catch { it.printStackTrace() }
                    .collect { }
            }

            withContext(Dispatchers.IO) {
                accountRepository.clearProfile()
            }
            accountRepository.storeSessionId(null)
            accountRepository.storeUserAccessToken(null, null)
            _isLoggingOut.value = false
        }
    }
}