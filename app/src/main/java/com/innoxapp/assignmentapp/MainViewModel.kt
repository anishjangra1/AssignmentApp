package com.innoxapp.assignmentapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val articles = MutableLiveData<List<Article>?>()

    fun getArticles(): MutableLiveData<List<Article>?> {
        return articles
    }

    fun fetchArticles() {
        viewModelScope.launch {
            val jsonString = repository.fetchData()
            val parsedArticles = jsonString?.let { repository.parseJson(it) }
            articles.value = parsedArticles
        }
    }
}


