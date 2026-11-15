package com.olimposystems.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.media.RingtoneManager
import androidx.core.net.toUri
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.olimposystems.android.audio.RingtonePlayer
import com.olimposystems.android.ui.AboutScreen
import com.olimposystems.android.ui.GameListScreen
import com.olimposystems.android.ui.HomeScreen
import com.olimposystems.android.ui.theme.MsxTheme
import com.olimposystems.android.util.RingtoneSetter
import com.olimposystems.android.util.TipoToque
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Gera as capturas de tela do README em ../screenshots, sem aparelho:
 *   ./gradlew :app:recordRoborazziDebug
 * Em testDebugUnitTest normal as telas são só renderizadas (nada é gravado).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = TELEFONE)
class ScreenshotTest {

    // Pequenas diferenças de renderização entre máquinas (CI x local) não reprovam o teste.
    @OptIn(ExperimentalRoborazziApi::class)
    private val opcoes = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01f),
    )

    private fun captura(nome: String, tela: @Composable () -> Unit) =
        captureRoboImage("../screenshots/$nome.png", roborazziOptions = opcoes) {
            MsxTheme(corDinamica = false) { tela() }
        }

    /** Simula um jogo já definido como som padrão, para os selos aparecerem no cartão. */
    private fun defineComoPadrao(game: MsxGame, vararg tipos: TipoToque) {
        val app = RuntimeEnvironment.getApplication()
        tipos.forEachIndexed { i, tipo ->
            val uri = "content://media/external_primary/audio/media/${100 + i}".toUri()
            RingtoneSetter.lembra(app, tipo, game, uri)
            RingtoneManager.setActualDefaultRingtoneUri(app, tipo.tipoSistema, uri)
        }
    }

    @Composable
    private fun Lista(maquina: Maquina) {
        val context = LocalContext.current
        val player = remember { RingtonePlayer(context) }
        GameListScreen(maquina, player, onVoltar = {}, onSobre = {})
    }

    @Test
    fun inicio() = captura("home") { HomeScreen(onMaquina = {}, onSobre = {}) }

    @Test
    @Config(qualifiers = "+night")
    fun inicioEscuro() = captura("home-dark") { HomeScreen(onMaquina = {}, onSobre = {}) }

    @Test
    fun msx1() {
        defineComoPadrao(MsxGame.ANTARTIC_ADVENTURE, TipoToque.TOQUE, TipoToque.ALARME)
        captura("msx1") { Lista(Maquina.MSX1) }
    }

    @Test
    @Config(qualifiers = "+night")
    fun msx2Escuro() = captura("msx2-dark") { Lista(Maquina.MSX2) }

    @Test
    fun sobre() = captura("about") { AboutScreen(onVoltar = {}) }

    @Test
    @Config(qualifiers = TABLET)
    fun tablet() = captura("tablet-msx1") { Lista(Maquina.MSX1) }
}

private const val TELEFONE = "w411dp-h891dp-port-xxhdpi"
private const val TABLET = "w1280dp-h800dp-land-mdpi"
