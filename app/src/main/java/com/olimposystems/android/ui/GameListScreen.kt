package com.olimposystems.android.ui

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.olimposystems.android.Maquina
import com.olimposystems.android.MsxGame
import com.olimposystems.android.R
import com.olimposystems.android.audio.RingtonePlayer
import com.olimposystems.android.util.RingtoneSetter
import com.olimposystems.android.util.RingtoneSetter.Resultado
import kotlinx.coroutines.launch

@Composable
fun GameListScreen(maquina: Maquina, player: RingtonePlayer, onVoltar: () -> Unit, onSobre: () -> Unit) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    // Jogo aguardando permissão: o toque é configurado assim que ela for concedida.
    var aguardandoConfiguracoes by rememberSaveable { mutableStateOf<MsxGame?>(null) }
    var mostraDialogo by rememberSaveable { mutableStateOf(false) }
    var aguardandoArmazenamento by rememberSaveable { mutableStateOf<MsxGame?>(null) }
    var repetir by remember { mutableStateOf<MsxGame?>(null) }

    val pedePermissaoArmazenamento = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { concedida ->
        if (concedida) {
            repetir = aguardandoArmazenamento
        } else {
            scope.launch { snackbar.showSnackbar(resources.getString(R.string.storage_denied)) }
        }
        aguardandoArmazenamento = null
    }

    fun defineToque(game: MsxGame) {
        scope.launch {
            when (val r = RingtoneSetter.define(context, game)) {
                Resultado.Ok ->
                    snackbar.showSnackbar(resources.getString(R.string.ringtone_set, game.nome))
                Resultado.PrecisaPermissaoConfiguracoes -> {
                    aguardandoConfiguracoes = game
                    mostraDialogo = true
                }
                Resultado.PrecisaPermissaoArmazenamento -> {
                    aguardandoArmazenamento = game
                    pedePermissaoArmazenamento.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                is Resultado.Erro ->
                    snackbar.showSnackbar(resources.getString(R.string.ringtone_error))
            }
        }
    }

    LaunchedEffect(repetir) {
        repetir?.let { defineToque(it) }
        repetir = null
    }

    // Volta da tela "Modificar configurações do sistema": conclui se o usuário liberou.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val game = aguardandoConfiguracoes
        if (game != null && !mostraDialogo && RingtoneSetter.podeAlterarConfiguracoes(context)) {
            aguardandoConfiguracoes = null
            defineToque(game)
        }
    }

    if (mostraDialogo) {
        AlertDialog(
            onDismissRequest = { mostraDialogo = false; aguardandoConfiguracoes = null },
            title = { Text(stringResource(R.string.permission_title)) },
            text = { Text(stringResource(R.string.permission_write_settings)) },
            confirmButton = {
                TextButton(onClick = {
                    mostraDialogo = false
                    context.startActivity(
                        Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, "package:${context.packageName}".toUri()),
                    )
                }) { Text(stringResource(R.string.open_settings)) }
            },
            dismissButton = {
                TextButton(onClick = { mostraDialogo = false; aguardandoConfiguracoes = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        topBar = { BarraSuperior(stringResource(maquina.titulo), onVoltar = onVoltar, onSobre = onSobre) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            contentPadding = padding.mais(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(MsxGame.da(maquina), key = { it.name }) { game ->
                CartaoJogo(
                    game = game,
                    tocando = player.tocando == game,
                    onTocar = { player.alterna(game) },
                    onDefinir = { defineToque(game) },
                )
            }
        }
    }
}

@Composable
private fun CartaoJogo(game: MsxGame, tocando: Boolean, onTocar: () -> Unit, onDefinir: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Image(
            bitmap = ImageBitmap.imageResource(game.imagemId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.Black),
            contentScale = ContentScale.Fit,
            // Pixel art: amplia sem borrar.
            filterQuality = FilterQuality.None,
        )
        Column(Modifier.padding(16.dp)) {
            Text(
                game.nome,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconToggleButton(checked = tocando, onCheckedChange = { onTocar() }) {
                    Icon(
                        painterResource(if (tocando) R.drawable.ic_stop else R.drawable.ic_play),
                        contentDescription = stringResource(
                            if (tocando) R.string.cd_stop else R.string.cd_play,
                            game.nome,
                        ),
                    )
                }
                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = onDefinir) {
                    Icon(
                        painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.set_ringtone))
                }
            }
        }
    }
}
