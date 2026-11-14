package com.olimposystems.android.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.olimposystems.android.util.TipoToque
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameListScreen(maquina: Maquina, player: RingtonePlayer, onVoltar: () -> Unit, onSobre: () -> Unit) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    // Quais jogos são hoje o toque/notificação/alarme (atualizado ao voltar para o app).
    var atuais by remember { mutableStateOf(RingtoneSetter.atuais(context)) }

    // Pedido pendente: salvo mas não definido (sem permissão), ou aguardando armazenamento.
    var pendenteJogo by rememberSaveable { mutableStateOf<MsxGame?>(null) }
    var pendenteTipo by rememberSaveable { mutableStateOf<TipoToque?>(null) }
    var pendenteUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var mostraDialogo by rememberSaveable { mutableStateOf(false) }
    var aguardandoPermissao by rememberSaveable { mutableStateOf(false) }
    var repetir by remember { mutableStateOf(false) }

    fun limpaPendente() {
        pendenteJogo = null; pendenteTipo = null; pendenteUri = null
        mostraDialogo = false; aguardandoPermissao = false
    }

    fun avisaDefinido(game: MsxGame, tipo: TipoToque) {
        atuais = RingtoneSetter.atuais(context)
        scope.launch {
            snackbar.showSnackbar(
                resources.getString(R.string.definido, game.nome, resources.getString(tipo.nomeNaFrase)),
            )
        }
    }

    val pedePermissaoArmazenamento = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { concedida ->
        if (concedida) {
            repetir = true
        } else {
            limpaPendente()
            scope.launch { snackbar.showSnackbar(resources.getString(R.string.storage_denied)) }
        }
    }

    fun define(game: MsxGame, tipo: TipoToque) {
        scope.launch {
            when (val r = RingtoneSetter.define(context, game, tipo)) {
                Resultado.Ok -> {
                    limpaPendente()
                    avisaDefinido(game, tipo)
                }
                is Resultado.Salvo -> {
                    pendenteJogo = game; pendenteTipo = tipo; pendenteUri = r.uri
                    mostraDialogo = true
                }
                Resultado.PrecisaPermissaoArmazenamento -> {
                    pendenteJogo = game; pendenteTipo = tipo
                    pedePermissaoArmazenamento.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                is Resultado.Erro -> {
                    limpaPendente()
                    snackbar.showSnackbar(resources.getString(R.string.ringtone_error))
                }
            }
        }
    }

    LaunchedEffect(repetir) {
        if (repetir) {
            repetir = false
            val game = pendenteJogo
            val tipo = pendenteTipo
            if (game != null && tipo != null) define(game, tipo)
        }
    }

    // Ao voltar das configurações: conclui se a permissão foi dada e atualiza os selos
    // (o usuário pode ter escolhido o som sozinho em "Som").
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val game = pendenteJogo
        val tipo = pendenteTipo
        val uri = pendenteUri
        if (aguardandoPermissao && game != null && tipo != null && uri != null &&
            RingtoneSetter.podeAlterarConfiguracoes(context)
        ) {
            try {
                RingtoneSetter.aplica(context, tipo, uri)
                avisaDefinido(game, tipo)
            } catch (e: SecurityException) {
                scope.launch { snackbar.showSnackbar(resources.getString(R.string.ringtone_error)) }
            }
            limpaPendente()
        }
        atuais = RingtoneSetter.atuais(context)
    }

    val dialogoJogo = pendenteJogo
    val dialogoTipo = pendenteTipo
    if (mostraDialogo && dialogoJogo != null && dialogoTipo != null) {
        val frase = stringResource(dialogoTipo.nomeNaFrase)
        AlertDialog(
            onDismissRequest = { limpaPendente() },
            title = { Text(stringResource(R.string.salvo_titulo, dialogoJogo.nome, frase)) },
            text = { Text(stringResource(R.string.salvo_texto, dialogoJogo.nome)) },
            confirmButton = {
                TextButton(onClick = {
                    mostraDialogo = false
                    aguardandoPermissao = true
                    context.startActivity(
                        Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, "package:${context.packageName}".toUri()),
                    )
                }) { Text(stringResource(R.string.permitir_e_definir)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    limpaPendente()
                    context.startActivity(Intent(Settings.ACTION_SOUND_SETTINGS))
                }) { Text(stringResource(R.string.escolher_sozinho)) }
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
                    progresso = player::progresso,
                    usos = atuais[game].orEmpty(),
                    onTocar = { player.alterna(game) },
                    onDefinir = { tipo -> define(game, tipo) },
                )
            }
        }
    }
}

@Composable
private fun CartaoJogo(
    game: MsxGame,
    tocando: Boolean,
    progresso: () -> Pair<Int, Int>?,
    usos: Set<TipoToque>,
    onTocar: () -> Unit,
    onDefinir: (TipoToque) -> Unit,
) {
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
            if (usos.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoToque.entries.filter { it in usos }.forEach { Selo(stringResource(it.rotulo)) }
                }
            }
            if (tocando) {
                Spacer(Modifier.height(12.dp))
                BarraProgresso(progresso)
            }
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
                MenuDefinir(onDefinir)
            }
        }
    }
}

@Composable
private fun MenuDefinir(onDefinir: (TipoToque) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { aberto = true }) {
            Icon(
                painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.set_as))
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            TipoToque.entries.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(stringResource(tipo.rotulo)) },
                    onClick = {
                        aberto = false
                        onDefinir(tipo)
                    },
                )
            }
        }
    }
}

@Composable
private fun Selo(texto: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(texto, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Barra e tempo do que está tocando; atualiza a cada 250 ms. */
@Composable
private fun BarraProgresso(progresso: () -> Pair<Int, Int>?) {
    var posicao by remember { mutableIntStateOf(0) }
    var duracao by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            progresso()?.let { (p, d) -> posicao = p; duracao = d }
            delay(250)
        }
    }
    Column {
        LinearProgressIndicator(
            progress = { if (duracao > 0) posicao.toFloat() / duracao else 0f },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.tempo, minSeg(posicao), minSeg(duracao)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun minSeg(ms: Int): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}
