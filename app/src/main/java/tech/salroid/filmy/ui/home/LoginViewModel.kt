package tech.salroid.filmy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.login.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiStateToken = MutableStateFlow<RequestTokenResponse?>(null)
    private val _uiStateProfile = MutableStateFlow<Profile?>(null)
    val uiStateToken: StateFlow<RequestTokenResponse?> = _uiStateToken.asStateFlow()
    val uiStateProfile: StateFlow<Profile?> = _uiStateProfile.asStateFlow()

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
                }.collect {
                    _uiStateProfile.emit(it)
                    // Save Profile in local
                    viewModelScope.launch(Dispatchers.IO) {
                        accountRepository.saveProfileToLocal(it)
                    }
                }
        }
    }

    private fun getProfileFromLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = accountRepository.getProfileFromLocal()
            _uiStateProfile.emit(profile)
        }
    }

    fun logout() {
        viewModelScope.launch {
            val sessionId = accountRepository.getSessionIdFromPref()
            sessionId?.let { id ->
                accountRepository.deleteSession(id)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        it.printStackTrace()
                    }.collect {
                        if (it.success == true) {
                            // Delete DB Profile
                            viewModelScope.launch(Dispatchers.IO) {
                                accountRepository.clearProfile()
                            }
                            // Nullify Profile Data
                            _uiStateProfile.emit(null)

                            // Nullify Session ID
                            accountRepository.storeSessionId(null)
                        }
                    }
            }
        }
    }
}