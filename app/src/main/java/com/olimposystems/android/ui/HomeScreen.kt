package com.olimposystems.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.olimposystems.android.Maquina
import com.olimposystems.android.R
import com.olimposystems.android.ui.theme.MsxTheme

@Composable
fun HomeScreen(onMaquina: (Maquina) -> Unit, onSobre: () -> Unit) {
    Scaffold(topBar = { BarraSuperior(stringResource(R.string.app_name), onSobre = onSobre) }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding.mais(24.dp)),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier.widthIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Maquina.entries.forEach { maquina ->
                    CartaoMaquina(maquina, onClick = { onMaquina(maquina) })
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.rodape),
                    Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun CartaoMaquina(maquina: Maquina, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Os logos são desenhos com antisserrilhado, não pixel art: escala suave.
            Image(
                bitmap = ImageBitmap.imageResource(maquina.logo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.75f),
                contentScale = ContentScale.FillWidth,
                filterQuality = FilterQuality.High,
            )
            Text(stringResource(maquina.titulo), style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview
@Composable
private fun HomePreview() {
    MsxTheme(corDinamica = false) { HomeScreen(onMaquina = {}, onSobre = {}) }
}
