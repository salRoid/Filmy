package tech.salroid.filmy.ui.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.model.Person
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    moviesRepository: MoviesRepository
) : ViewModel() {

    val people: Flow<PagingData<Person>> = moviesRepository.getPeople()
        .cachedIn(viewModelScope)
}
