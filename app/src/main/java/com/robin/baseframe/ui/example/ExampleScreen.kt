package com.robin.baseframe.ui.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast

/**
 * 示例页面 Compose Screen — UDF 样板。
 *
 * 数据流：
 *   state <- ViewModel.uiState (单向)
 *   onEvent -> ViewModel.onEvent (单向)
 */
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 观察一次性副作用
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExampleUiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is ExampleUiEffect.ShowError -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 首次进入加载数据
    LaunchedEffect(Unit) {
        viewModel.onEvent(ExampleUiEvent.LoadHome)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> {
                    Text("加载失败: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.onEvent(ExampleUiEvent.Refresh) }) {
                        Text("重试")
                    }
                }
                else -> {
                    Text(
                        "首页数据 (Banner: ${uiState.homeData?.banner?.size ?: 0}, " +
                            "Channel: ${uiState.homeData?.channels?.size ?: 0})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = {
                        viewModel.onEvent(ExampleUiEvent.Refresh)
                        viewModel.showToast("刷新完成")
                    }) {
                        Text("刷新")
                    }
                }
            }
        }
    }
}
