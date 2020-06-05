package com.example.pagergallerysave

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
enum class NetworkStatus{
    LOADING,
    FAILED,
    COMPLETED,
    INITIAL_LOADING,
    LOADED
}
class PixabayDataSource(private val context: Context):PageKeyedDataSource<Int,PhotoItem>() {
    var retry:(()->Any)?=null
    private val _networkStatus= MutableLiveData<NetworkStatus>()
    val networkStatus=_networkStatus
    private val queryWords = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal").random()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, PhotoItem>) {
        retry=null
        _networkStatus.postValue(NetworkStatus.INITIAL_LOADING)
        val url="https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${queryWords}&per_page=100&page=1"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {

                callback.onResult(Gson().fromJson(it,Pixabay::class.java).hits.toList(),null,2)
                _networkStatus.postValue(NetworkStatus.LOADED)

            },
            Response.ErrorListener {
                retry={loadInitial(params,callback)}
                _networkStatus.postValue(NetworkStatus.FAILED)
                Log.d("error","$it")
            }
        ).also {
            VolleySingleton.getInstance(context).requestQueue.add(it)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        retry=null
        _networkStatus.postValue(NetworkStatus.LOADING)
        val url="https://pixabay.com/api/?key=12472743-874dc01dadd26dc44e0801d61&q=${queryWords}&per_page=100&page=${params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {

                callback.onResult(Gson().fromJson(it,Pixabay::class.java).hits.toList(),params.key+1)
                _networkStatus.postValue(NetworkStatus.LOADED)
            },
            Response.ErrorListener {
                if (it.toString()=="com.android.volley.ClientError"){
                    _networkStatus.postValue(NetworkStatus.COMPLETED)
                }else{
                    retry={loadAfter(params,callback)}
                    _networkStatus.postValue(NetworkStatus.FAILED)
                }

                Log.d("error","$it")
            }
        ).also {
            VolleySingleton.getInstance(context).requestQueue.add(it)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {

    }
}