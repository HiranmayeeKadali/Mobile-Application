package com.coddle.ui.viewModels

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coddle.model.User
import com.coddle.repository.UserRepository
import com.coddle.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val validate = MutableLiveData<Resource<Int>>()
    val login = MutableLiveData<Resource<User>>()

    fun validateInputs(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            if (email.isBlank() || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            )
                validate.value = Resource(Resource.ResourceState.ERROR, 0, "Invalid email")
            else if (password.isEmpty() || password.isBlank())
                validate.value = Resource(Resource.ResourceState.ERROR, 1, "Invalid password")
            else if (password.length < 6)
                validate.value =
                    Resource(Resource.ResourceState.ERROR, 1, "Password must be 6 characters")
            else
                validate.value = Resource(Resource.ResourceState.SUCCESS)
        }
    }


    fun login(email: String, password: String){
        viewModelScope.launch (Dispatchers.IO)@ExperimentalCoroutinesApi{
            login.postValue(Resource(Resource.ResourceState.LOADING))
            userRepository.login(email, password).collect {
                login.postValue(it)
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class LoginProvider(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java))
            return LoginViewModel(userRepository) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}