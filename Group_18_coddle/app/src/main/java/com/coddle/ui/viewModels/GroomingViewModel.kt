package com.coddle.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coddle.model.Grooming
import com.coddle.repository.GroomRepository
import com.coddle.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class GroomingViewModel(
    private val groomRepository: GroomRepository
):ViewModel() {

    val addGrooming = MutableLiveData<Boolean>()
    val getGrooming = MutableLiveData<Resource<List<Grooming>>>()

    fun addGrooming(grooming: Grooming){
        viewModelScope.launch {
            groomRepository.addGrooming(grooming)
            addGrooming.value = true
        }
    }


    fun getGrooming(){
        viewModelScope.launch  @ExperimentalCoroutinesApi{
            getGrooming.value = Resource(Resource.ResourceState.LOADING)
            groomRepository.getGrooming().collect {
                getGrooming.value = it
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class GroomProvider(
    private val groomRepository: GroomRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroomingViewModel::class.java))
            return GroomingViewModel(groomRepository) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}