package com.robin.baseframe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.bean.User

class CountDownViewModel:BaseViewModel() {

     val globalLiveData = GlobalLiveData.getInstance()

     val userLiveData = MutableLiveData<User>()

     val userNameLiveData = Transformations.map(userLiveData){
          user->
          "${user.name} --${user.age}"
     }
}