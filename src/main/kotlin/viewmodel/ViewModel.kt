package viewmodel

import kotlinx.coroutines.CoroutineScope

open class ViewModel {

    protected lateinit var viewModelScope: CoroutineScope

    fun init(viewModelScope: CoroutineScope) {
        this.viewModelScope = viewModelScope
    }
}