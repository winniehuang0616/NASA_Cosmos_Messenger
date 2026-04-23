package com.example.nasacosmosmessengerapp.presentation.nova

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class NovaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NovaUiState.initial())
    val uiState: StateFlow<NovaUiState> = _uiState.asStateFlow()

    fun onDraftChange(value: String) {
        _uiState.update { it.copy(draft = value) }
    }

    fun onOpenDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun onDismissDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun onDateSelected(millis: Long) {
        _uiState.update {
            it.copy(
                draft = formatPickedDateUtcMillis(millis),
                showDatePicker = false
            )
        }
    }

    fun onSendClick() {
        _uiState.update { s -> if (s.draft.isNotBlank()) s.copy(draft = "") else s }
    }

    private fun formatPickedDateUtcMillis(millis: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.getDefault(), "%d/%02d/%02d", y, m, d)
    }
}
