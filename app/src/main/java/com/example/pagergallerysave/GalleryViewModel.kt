package com.example.pagergallerysave

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val factory=PixabayDataSourceFactory(application)
    val pagedListPhotoLiveData=factory.toLiveData(50)
    val networkStatus: LiveData<NetworkStatus> =Transformations.switchMap(factory.pixabayDataSource){it.networkStatus}
    fun ResetQuery(){
        pagedListPhotoLiveData.value?.dataSource?.invalidate()
    }
    fun retry(){
        factory.pixabayDataSource.value?.retry?.invoke()
    }
}