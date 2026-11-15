package com.olimposystems.android

import com.olimposystems.android.util.TipoToque
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Robolectric porque [TipoToque] lê constantes do Android (Environment, MediaStore). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AudioParaTest {

    @Test
    fun notificacaoUsaVinhetaQuandoHouver() {
        assertEquals(R.raw.nemesis3_notificacao, MsxGame.NEMESIS3.audioPara(TipoToque.NOTIFICACAO))
        assertEquals(R.raw.nemesis3, MsxGame.NEMESIS3.audioPara(TipoToque.TOQUE))
        assertEquals(R.raw.nemesis3, MsxGame.NEMESIS3.audioPara(TipoToque.ALARME))
    }

    @Test
    fun semVinhetaUsaAMusica() {
        for (tipo in TipoToque.entries) assertEquals(R.raw.zanac, MsxGame.ZANAC.audioPara(tipo))
    }
}
