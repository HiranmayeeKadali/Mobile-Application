package com.coddle.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coddle.model.Medical
import com.coddle.repository.MedicalRepository
import com.coddle.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MedicalViewModel(
    private val medicalRepository: MedicalRepository
):ViewModel() {

    val addMedical = MutableLiveData<Boolean>()
    val getMedical = MutableLiveData<Resource<List<Medical>>>()

    fun addMedical(grooming: Medical){
        viewModelScope.launch {
            medicalRepository.addMedical(grooming)
            addMedical.value = true
        }
    }


    fun getMedical(){
        viewModelScope.launch  @ExperimentalCoroutinesApi{
            getMedical.value = Resource(Resource.ResourceState.LOADING)
            medicalRepository.getMedical().collect {
                getMedical.value = it
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class MedicalProvider(
    private val medicalRepository: MedicalRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicalViewModel::class.java))
            return MedicalViewModel(medicalRepository) as T
        throw IllegalArgumentException("Unknown viewModel class")
    }
}