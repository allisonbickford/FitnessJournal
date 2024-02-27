package com.catscoffeeandkitchen.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catscoffeeandkitchen.data.SettingRepository
import com.catscoffeeandkitchen.models.WeightUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingRepository
): ViewModel() {

    val weightUnit: Flow<WeightUnit> = settingsRepository.getWeightUnit()
    val timers: Flow<List<Long>> = settingsRepository.getTimers()

    private var _backupStatus: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val backupStatus: Flow<Boolean?> = _backupStatus

    private var _restoreStatus: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val restoreStatus: Flow<Boolean?> = _restoreStatus

    private var _importStatus: MutableStateFlow<Double?> = MutableStateFlow(null)
    val importStatus: Flow<Double?> = _importStatus

    private var _exportStatus: MutableStateFlow<Int?> = MutableStateFlow(null)
    val exportStatus: Flow<Int?> = _exportStatus

    fun setWeightUnit(unit: WeightUnit) = viewModelScope.launch {
        settingsRepository.setWeightUnit(unit)
    }

    fun setTimer(timerIndex: Int, seconds: Long) = viewModelScope.launch {
        settingsRepository.setTimer(timerIndex, seconds)
    }

    fun backupData() = viewModelScope.launch {
//        backupDataUseCase.run().collect { state ->
//            _backupStatus.value = state
//        }
    }

    fun restoreData() = viewModelScope.launch {
//        restoreDataUseCase.run().collect { state ->
//            _restoreStatus.value = state
//        }
    }

    fun backupDataToExternalFile(uri: Uri) = viewModelScope.launch {
//        backupDataUseCase.run(uri).collect { state ->
//            _backupStatus.value = state
//        }
    }

    fun restoreDataFromFile(file: File) = viewModelScope.launch {
//        restoreDataUseCase.run(file).collect { state ->
//            _restoreStatus.value = state
//        }
    }

    fun importFromCSV(uri: Uri) = viewModelScope.launch {
//        importFromCsvDataUseCase.run(uri).collect { imported ->
//            _importStatus.value = imported
//        }
    }

    fun exportToCsv(location: Uri) = viewModelScope.launch {
//        exportToCsvDataUseCase.run(location).collect { state ->
//            if (state is DataState.Error) {
//                Timber.e(state.e)
//            }
//            _exportStatus.value = state
//        }
    }
}
