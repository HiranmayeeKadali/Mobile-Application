package com.coddle.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.coddle.util.AppSharedPreference.Companion.isLogin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LauncherViewModel(
    private val application: Application
):ViewModel() {

    val login = MutableLiveData<Boolean>()

    fun splash(){
        viewModelScope.launch {
            delay(1200)
            login.value = application.isLogin()
        }
    }

}

@Suppress("UNCHECKED_CAST")
class LauncherProvider(
    private val application: Application
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LauncherViewModel::class.java))
            return LauncherViewModel(application) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}