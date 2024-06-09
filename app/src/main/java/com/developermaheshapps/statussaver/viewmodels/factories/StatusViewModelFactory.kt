package com.developermaheshapps.statussaver.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.developermaheshapps.statussaver.data.StatusRepo
import com.developermaheshapps.statussaver.viewmodels.StatusViewModel

class StatusViewModelFactory(private val repo: StatusRepo):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatusViewModel(repo) as T
    }
}