package com.coddle.ui.viewModels

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coddle.repository.UserRepository
import com.coddle.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val validate = MutableLiveData<Resource<Int>>()
    val isEmailExist = MutableLiveData<Resource<Boolean>>()


    fun validateInputs(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            if (name.isEmpty() || name.isBlank())
                validate.value = Resource(Resource.ResourceState.ERROR, 0, "Invalid name")
            else if (email.isEmpty() || email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            )
                validate.value = Resource(Resource.ResourceState.ERROR, 1, "Invalid email")
            else if (password.isBlank() || password.isEmpty())
                validate.value = Resource(Resource.ResourceState.ERROR, 2, "Invalid password")
            else if (password.length < 6)
                validate.value =
                    Resource(Resource.ResourceState.ERROR, 2, "Password should be 6 characters")
            else
                validate.value = Resource(Resource.ResourceState.SUCCESS)
        }
    }

    @ExperimentalCoroutinesApi
    fun isEmailExist(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isEmailExist.postValue(Resource(Resource.ResourceState.LOADING))
            userRepository.isEmailExist(email).collect {
                isEmailExist.postValue(it)
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class RegisterProvider(
    private val application: Application,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java))
            return RegisterViewModel(userRepository) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}