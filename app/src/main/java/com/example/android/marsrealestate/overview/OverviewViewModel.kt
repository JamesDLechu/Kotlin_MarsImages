/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.network.MarsPropertyFilter
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
enum class MarsPropertyStatus{ LOADING, FAILED, DONE }

class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsPropertyStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<MarsPropertyStatus>
        get() = _status

    private val _properties= MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    private val _navigateToDetailView= MutableLiveData<MarsProperty>()
    val navigateToDetailView: LiveData<MarsProperty>
        get() = _navigateToDetailView

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsPropertyFilter.SHOW_ALL)
    }

    fun onNavigateToDetailView(marsProperty: MarsProperty) {
        _navigateToDetailView.value= marsProperty
    }

    fun onNavigateToDetailViewDone() {
        _navigateToDetailView.value= null
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsPropertyFilter) {
        viewModelScope.launch {
            _status.value= MarsPropertyStatus.LOADING
            var getPropertiesDeferred= MarsApi.retrofitService.getPropertiesAsync(filter.value)

            try{
                val listRetrieved= getPropertiesDeferred

                _status.value= MarsPropertyStatus.DONE

                if(listRetrieved.isNotEmpty()){
                    _properties.value= listRetrieved
                }
            }catch (e: Exception){
                _status.value= MarsPropertyStatus.FAILED
                _properties.value= ArrayList()
                Log.e("OverViewModel", e.message!!)
            }
        }
    }

    fun updateProperties(filter: MarsPropertyFilter){
        getMarsRealEstateProperties(filter)
    }
}
