package com.coddle.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coddle.model.Walking
import com.coddle.repository.WalkRepository
import com.coddle.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class WalkingViewModel(
    private val walkRepository: WalkRepository
):ViewModel() {

    val addWalking = MutableLiveData<Boolean>()
    val getWalking = MutableLiveData<Resource<List<Walking>>>()

    fun addWalking(walking: Walking){
        viewModelScope.launch {
            walkRepository.addWalking(walking)
            addWalking.value = true
        }
    }


    fun getWalking(){
        viewModelScope.launch  @ExperimentalCoroutinesApi{
            getWalking.value = Resource(Resource.ResourceState.LOADING)
            walkRepository.getWalking().collect {
                getWalking.value = it
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class WalkProvider(
    private val walkRepository: WalkRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkingViewModel::class.java))
            return WalkingViewModel(walkRepository) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}