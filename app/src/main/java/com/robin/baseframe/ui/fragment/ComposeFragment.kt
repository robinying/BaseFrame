package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.databinding.FragmentComposeBinding

class ComposeFragment : BaseFragment<BaseViewModel, FragmentComposeBinding>() {
    private val strList = arrayListOf("test1","test2","test3","test4","test5")
    override fun initView(savedInstanceState: Bundle?) {
        binding.composeView.setContent {
            Column {
                greeting("This is greeting string")
                TextView()
                listItems(list = strList)
            }
        }
    }

    @Composable
    private fun greeting(text: String) {
        Text(text = text)
    }

    //Compose中使用Android原生View
    @Composable
    fun TextView() {
        Row() {
            Text(text = "this is a compose TextView")
            Spacer(modifier = Modifier.padding(horizontal = 20.dp))
            Text("Next")
            // 使用androidx.compose.ui.viewinterop.AndroidView
            AndroidView(
                // 需要提供一个factory来返回我们要使用的Android原生View，这里我们返回一个原生的ImageView
                factory = { context ->
                    val imageView = ImageView(context)
                    imageView.setImageResource(R.drawable.shape_icon)
                    imageView
                },
                // modifier，可以不提供
                //modifier = Modifier.padding(20.dp),
                // update，每次compose重组会调用到这里，可以不提供
                update = { imageView ->

                    Log.d("Compsose", "$imageView has update")
                })
        }
    }


    @Composable
    private fun listItems(list: ArrayList<String>) {
        LazyColumn(content = {
            list.forEachIndexed { index, str ->
                item {
                    Text(str)
                }
            }
        })
    }


}