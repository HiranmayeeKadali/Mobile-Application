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

class PetDetailsViewModel(
    private val userRepository: UserRepository
) : ViewModel(){

    val validate = MutableLiveData<Resource<Int>>()
    val addUser = MutableLiveData<Resource<Boolean>>()

    fun validateInputs(
        name: String,
        type: String,
        breed: String,
    ) {
        viewModelScope.launch {
            if (name.isEmpty() || name.isBlank())
                validate.value = Resource(Resource.ResourceState.ERROR, 0, "Invalid name")
            else if (type.isEmpty() || type.isBlank())
                validate.value = Resource(Resource.ResourceState.ERROR, 1, "Invalid type")
            else if (breed.isBlank() || breed.isEmpty())
                validate.value = Resource(Resource.ResourceState.ERROR, 2, "Invalid breed")
            else
                validate.value = Resource(Resource.ResourceState.SUCCESS)
        }
    }

    fun addUser(user: User,password:String){
        viewModelScope.launch (Dispatchers.IO) @ExperimentalCoroutinesApi{
            addUser.postValue(Resource(Resource.ResourceState.LOADING))
            userRepository.addUser(user,password).collect {
                addUser.postValue(it)
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class PetDetailsProvider(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetDetailsViewModel::class.java))
            return PetDetailsViewModel(userRepository) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}