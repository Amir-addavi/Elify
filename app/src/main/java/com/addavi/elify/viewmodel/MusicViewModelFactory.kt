package com.addavi.elify.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MusicViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // ✅ همیشه ApplicationContext استفاده کن
        return MusicViewModel(context.applicationContext) as T
    }
}