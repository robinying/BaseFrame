package com.robin.baseframe.viewmodel

import androidx.lifecycle.MutableLiveData
import com.robin.baseframe.app.base.BaseViewModel

class ScrollViewModel : BaseViewModel() {
    val itemListData = MutableLiveData<ArrayList<String>>()

    fun getData() {
        val itemList = arrayListOf(
            "Item1",
            "Item2",
            "Item3",
            "Item4",
            "Item5",
            "Item6",
            "Item7",
            "Item8",
            "Item9",
            "Item10",
            "Item11",
            "Item12",
            "Item13",
            "Item14",
            "Item15",
            "Item1",
            "Item2",
            "Item3",
            "Item4",
            "Item5",
            "Item6",
            "Item7",
            "Item8",
            "Item9",
            "Item10",
            "Item11",
            "Item12",
            "Item13",
            "Item14",
            "Item15"
        )
        itemListData.postValue(itemList)
    }
}