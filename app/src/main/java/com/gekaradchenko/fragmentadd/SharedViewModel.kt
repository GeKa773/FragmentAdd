package com.gekaradchenko.fragmentadd

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val dataStore = DataStoreRepository(app)

    val count: LiveData<Int> = dataStore.readDolorFromDataStore.asLiveData()

    private val _listFragment = MutableLiveData<ArrayList<CountFragment>>()
    val listFragment: LiveData<ArrayList<CountFragment>> = _listFragment

    private val _pushNotification = SingleLiveEvent<Int>()
    val pushNotification: LiveData<Int> = _pushNotification

    private val _deleteNotification = SingleLiveEvent<Int>()
    val deleteNotification: LiveData<Int> = _deleteNotification

    fun onPushNotification(count: Int) {
        _pushNotification.postValue(count)
    }

     fun setList() {
        val arrayList = ArrayList<CountFragment>()
        count.value?.let { count ->
            repeat(count) {
                arrayList.add(CountFragment(it + 1))
            }
        }
        _listFragment.value = arrayList
    }

    fun plusFragment() {
        coroutineScope.launch {
            count.value?.let {
                dataStore.saveCountToDataStore(it + 1)
            }
        }
    }

    fun minusFragment() {
        coroutineScope.launch {
            count.value?.let {

                if (it > 1) {
                    dataStore.saveCountToDataStore(it - 1)
                    _deleteNotification.postValue(it)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}