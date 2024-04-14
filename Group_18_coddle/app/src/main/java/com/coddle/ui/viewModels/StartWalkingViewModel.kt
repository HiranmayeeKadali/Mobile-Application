package com.coddle.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coddle.model.Walking
import com.coddle.repository.WalkRepository
import java.lang.IllegalArgumentException

class StartWalkingViewModel(
    private val walkRepository: WalkRepository
):ViewModel() {


    fun updateWalking(walking: Walking){
        walkRepository.updateWalking(walking)
    }

}

@Suppress("UNCHECKED_CAST")
class StartWalkingProvider(
    private val walkRepository: WalkRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StartWalkingViewModel::class.java))
            return StartWalkingViewModel(walkRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}