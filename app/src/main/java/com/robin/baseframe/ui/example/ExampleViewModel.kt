package com.robin.baseframe.ui.example

import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.base.arch.Result
import com.robin.baseframe.domain.usecase.GetHomeDataUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 示例页面 ViewModel — UDF 样板。
 *
 * 完整链路：UI → onEvent(ExampleUiEvent) → UseCase → Repository → Result → updateState
 */
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase
) : BaseViewModel<ExampleUiState, ExampleUiEvent, ExampleUiEffect>(ExampleUiState()) {

    override fun onEvent(event: ExampleUiEvent) {
        when (event) {
            is ExampleUiEvent.LoadHome -> loadHomeData()
            is ExampleUiEvent.Refresh -> loadHomeData()
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            getHomeDataUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> updateState { copy(isLoading = true, error = null) }
                    is Result.Success -> updateState {
                        copy(isLoading = false, homeData = result.data, error = null)
                    }
                    is Result.Error -> updateState {
                        copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun showToast(message: String) {
        sendEffect(ExampleUiEffect.ShowToast(message))
    }
}
