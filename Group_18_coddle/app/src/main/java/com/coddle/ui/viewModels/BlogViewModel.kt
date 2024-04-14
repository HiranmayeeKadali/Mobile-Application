package com.coddle.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coddle.model.Blog
import com.coddle.repository.BlogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class BlogViewModel(
    private val blogRepository: BlogRepository
):ViewModel() {

    val getBlogs = MutableLiveData<List<Blog>>()


    fun getBlogs(){
        viewModelScope.launch (Dispatchers.IO){
            getBlogs.postValue(blogRepository.getBlogs())
        }
    }

}

@Suppress("UNCHECKED_CAST")
class BlogProvider(
    private val blogRepository: BlogRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java))
            return BlogViewModel(blogRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}