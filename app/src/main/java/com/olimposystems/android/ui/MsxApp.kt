package com.olimposystems.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.olimposystems.android.Maquina
import com.olimposystems.android.audio.RingtonePlayer

enum class Tela { INICIO, MSX1, MSX2, SOBRE }

private val PilhaSaver = listSaver<SnapshotStateList<Tela>, String>(
    save = { pilha -> pilha.map { it.name } },
    restore = { nomes -> mutableStateListOf(*nomes.map(Tela::valueOf).toTypedArray()) },
)

/** Navegação simples por pilha de telas; o botão/gesto "voltar" desempilha. */
@Composable
fun MsxApp() {
    val pilha = rememberSaveable(saver = PilhaSaver) { mutableStateListOf(Tela.INICIO) }
    val atual = pilha.last()
    val abre: (Tela) -> Unit = { pilha.add(it) }
    val volta: () -> Unit = { if (pilha.size > 1) pilha.removeAt(pilha.lastIndex) }

    BackHandler(enabled = pilha.size > 1, onBack = volta)

    val context = LocalContext.current
    val player = remember { RingtonePlayer(context) }
    DisposableEffect(Unit) { onDispose { player.para() } }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { player.para() }
    LaunchedEffect(atual) { player.para() }

    AnimatedContent(
        targetState = atual,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "tela",
    ) { tela ->
        when (tela) {
            Tela.INICIO -> HomeScreen(
                onMaquina = { abre(if (it == Maquina.MSX1) Tela.MSX1 else Tela.MSX2) },
                onSobre = { abre(Tela.SOBRE) },
            )
            Tela.MSX1 -> GameListScreen(Maquina.MSX1, player, onVoltar = volta, onSobre = { abre(Tela.SOBRE) })
            Tela.MSX2 -> GameListScreen(Maquina.MSX2, player, onVoltar = volta, onSobre = { abre(Tela.SOBRE) })
            Tela.SOBRE -> AboutScreen(onVoltar = volta)
        }
    }
}
