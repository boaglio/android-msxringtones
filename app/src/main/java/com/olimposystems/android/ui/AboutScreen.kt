package com.olimposystems.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.olimposystems.android.BuildConfig
import com.olimposystems.android.R

private const val POLITICA_PRIVACIDADE = "https://github.com/boaglio/android-msxringtones/blob/main/PRIVACY.md"

@Composable
fun AboutScreen(onVoltar: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Scaffold(topBar = { BarraSuperior(stringResource(R.string.sobre), onVoltar = onVoltar) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding.mais(32.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(stringResource(R.string.titulo), style = MaterialTheme.typography.headlineMedium)
            Image(painterResource(R.drawable.olimposystems), contentDescription = stringResource(R.string.sobre))
            Button(onClick = { uriHandler.openUri(POLITICA_PRIVACIDADE) }) {
                Text(stringResource(R.string.comments))
            }
            Text(
                stringResource(R.string.versao, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
