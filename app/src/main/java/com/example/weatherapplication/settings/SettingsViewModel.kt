package com.example.weatherapplication.settings

import androidx.lifecycle.ViewModel
import com.example.weatherapplication.remote.RepositryInterface

class SettingsViewModel(private val repository: RepositryInterface) : ViewModel() {

    fun putStringInSharedPreferences(key: String, stringInput: String){
        repository.putStringInSharedPreferences(key, stringInput)
    }

    fun putBooleanInSharedPreferences(key: String, boolean: Boolean){
        repository.putBooleanInSharedPreferences(key, boolean)
    }

    fun getStringFromSharedPreferences(key: String, stringDefault: String) : String{
        return repository.getStringFromSharedPreferences(key, stringDefault)
    }

}