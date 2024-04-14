package com.coddle.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coddle.model.Report
import com.coddle.util.Constants.REPORTS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {

    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun report(report: Report){
        viewModelScope.launch {
            firebaseFirestore.collection(REPORTS)
                .add(report)
        }
    }

}