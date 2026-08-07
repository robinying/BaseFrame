package com.robin.baseframe.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.robin.baseframe.R

/** Compose counterpart of [DemoStateView], with the same state vocabulary. */
@Composable
fun DemoStateContent(
    isLoading: Boolean,
    errorMessage: String?,
    isEmpty: Boolean,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    when {
        isLoading -> DemoStateMessage(
            title = stringResource(R.string.demo_state_loading_title),
            message = stringResource(R.string.demo_state_loading_message),
            showProgress = true
        )
        errorMessage != null -> DemoStateMessage(
            title = stringResource(R.string.demo_state_error_title),
            message = errorMessage,
            action = stringResource(R.string.demo_state_retry),
            onAction = onRetry
        )
        isEmpty -> DemoStateMessage(
            title = stringResource(R.string.demo_state_empty_title),
            message = stringResource(R.string.demo_state_empty_message)
        )
        else -> content()
    }
}

@Composable
private fun DemoStateMessage(
    title: String,
    message: String,
    showProgress: Boolean = false,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        if (showProgress) CircularProgressIndicator()
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        if (action != null && onAction != null) {
            Button(onClick = onAction) { Text(action) }
        }
    }
}
