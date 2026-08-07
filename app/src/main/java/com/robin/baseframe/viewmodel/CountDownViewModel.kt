package com.robin.baseframe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.robin.baseframe.app.base.LegacyViewModel
import com.robin.baseframe.data.bean.User

class CountDownViewModel:LegacyViewModel() {

     val globalLiveData = GlobalLiveData.getInstance()

     val userLiveData = MutableLiveData<User>()

     val userNameLiveData = userLiveData.map {
          user->
          "${user.name} --${user.age}"
     }
}