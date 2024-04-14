package com.coddle.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coddle.model.User
import com.coddle.repository.UserRepository
import com.coddle.util.Resource
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SettingsViewModel(
    private val userRepository: UserRepository
):ViewModel() {

    val validate = MutableLiveData<Resource<Int>>()
    val update = MutableLiveData<Boolean>()


    fun updateUser(user: User){
        viewModelScope.launch {
            userRepository.updateUser(user)
            update.value = true
        }
    }

    fun validateInputs(
            user: User
    ) {
        viewModelScope.launch {
            if (user.username.isEmpty() || user.username.isBlank())
                validate.value = Resource(Resource.ResourceState.ERROR, 0, "Invalid name")
            else if (user.petName.isEmpty() || user.petName.isBlank())
                validate.value = Resource(Resource.ResourceState.ERROR, 1, "Invalid name")
            else if (user.type.isBlank() || user.type.isEmpty())
                validate.value = Resource(Resource.ResourceState.ERROR, 2, "Invalid type")
            else if (user.breed.isBlank() || user.breed.isEmpty())
                validate.value = Resource(Resource.ResourceState.ERROR, 3, "Invalid breed")
            else
                validate.value = Resource(Resource.ResourceState.SUCCESS)
        }
    }

}

@Suppress("UNCHECKED_CAST")
class SettingsProvider(
    private val userRepository: UserRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java))
            return SettingsViewModel(userRepository) as T
        throw IllegalArgumentException("unknown viewModel class")
    }
}