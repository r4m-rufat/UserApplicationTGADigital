package com.codingwithrufat.userapplication.presentation.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingwithrufat.userapplication.network.models.ItemsItem
import com.codingwithrufat.userapplication.network.models.UserResponse
import com.codingwithrufat.userapplication.repository.MainActivityRepository
import com.codingwithrufat.userapplication.util.NetworkState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel: ViewModel() {

    private val mutable_users: MutableLiveData<NetworkState<List<ItemsItem?>>> = MutableLiveData(NetworkState.LOADING(true))
    private val compositeDisposable = CompositeDisposable()
    private val search_qeury = MutableLiveData("")

    /**
     * fetch user information from the api
     * and observable data is added to compositeDisposable object
     */
    fun fetchUsers(name: String) {
        val repository = MainActivityRepository()
        compositeDisposable.add(
            repository.getUsers(page = 1, order = "asc", name = name, site = "stackoverflow")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { data ->
                    var allItems = ArrayList<ItemsItem>(data.items) // our first list
                    var sortedList = ArrayList(data.items) // list which we will sort
                    if (data.items!!.size >= 20){ // if items is more than 20 then we take 20 items of list
                        sortedList = ArrayList(data.items.subList(0,20) )
                    }

                    /**
                     * and items is sorted in here
                     * and doesn't care about capital letters
                     */
                    sortedList.sortWith(Comparator { first, second ->
                        first?.displayName!!.compareTo(
                            second?.displayName!!,
                            ignoreCase = true
                        )
                    })
                    allItems.subList(0, sortedList.size).clear() // after is sorted 20 items of list we clear first 20 items of whole list

                    return@map sortedList + allItems // is combined two list: sorted list and all items which cleared first 20 items
                }
                .subscribe({ data ->

                    mutable_users.postValue(NetworkState.SUCCESS(data))

                }, { exception ->

                    mutable_users.postValue(NetworkState.ERROR(exception))

                })
        )
    }

    // updated search query value
    fun setSearchQuery(name: String) {
        search_qeury.value = name
        mutable_users.value = NetworkState.LOADING(true)
    }

    val search: LiveData<String>
    get() {
        return search_qeury
    }

    val users: LiveData<NetworkState<List<ItemsItem?>>>
    get() {
        return mutable_users
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}