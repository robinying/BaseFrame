package com.robin.baseframe.ui.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robin.baseframe.R
import com.robin.baseframe.ui.component.DemoStateContent

/** Compose MVVM/UDF example using the shared app shell and state language. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
    onBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val navigateUpLabel = stringResource(R.string.navigate_up)
    val refreshSubmittedMessage = stringResource(R.string.example_refresh_submitted)
    val homeData = uiState.homeData

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExampleUiEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                is ExampleUiEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ExampleUiEvent.LoadHome)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.example_toolbar_title)) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Text(
                                text = "‹",
                                modifier = Modifier.semantics {
                                    contentDescription = navigateUpLabel
                                }
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DemoStateContent(
                isLoading = uiState.isLoading || (homeData == null && uiState.error == null),
                errorMessage = uiState.error,
                isEmpty = homeData != null && homeData.banner.isEmpty() && homeData.channels.isEmpty(),
                onRetry = { viewModel.onEvent(ExampleUiEvent.Refresh) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            R.string.example_data_summary,
                            homeData?.banner?.size ?: 0,
                            homeData?.channels?.size ?: 0
                        ),
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = {
                        viewModel.onEvent(ExampleUiEvent.Refresh)
                        viewModel.showToast(refreshSubmittedMessage)
                    }) {
                        Text(stringResource(R.string.example_refresh))
                    }
                }
            }
        }
    }
}
