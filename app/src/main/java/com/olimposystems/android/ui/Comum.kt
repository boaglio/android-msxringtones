package com.olimposystems.android.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.olimposystems.android.R

/** Barra superior padrão: título, "voltar" opcional e ação "Sobre" opcional. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onVoltar: (() -> Unit)? = null, onSobre: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onVoltar != null) {
                IconButton(onClick = onVoltar) {
                    Icon(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.voltar))
                }
            }
        },
        actions = {
            if (onSobre != null) {
                IconButton(onClick = onSobre) {
                    Icon(painterResource(R.drawable.ic_info), stringResource(R.string.sobre))
                }
            }
        },
    )
}

/** Soma as margens do Scaffold (barras do sistema/app bar) com uma margem extra. */
@Composable
fun PaddingValues.mais(extra: Dp): PaddingValues {
    val direcao = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(direcao) + extra,
        top = calculateTopPadding() + extra,
        end = calculateEndPadding(direcao) + extra,
        bottom = calculateBottomPadding() + extra,
    )
}
