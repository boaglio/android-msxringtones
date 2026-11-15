package com.olimposystems.android

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.olimposystems.android.util.TipoToque

enum class Maquina(@param:StringRes val titulo: Int, @param:DrawableRes val logo: Int) {
    MSX1(R.string.msx1, R.drawable.msx1_default),
    MSX2(R.string.msx2, R.drawable.msx2_default),
}

/**
 * Todos os toques do app. Para adicionar um jogo: coloque o .ogg em res/raw, a imagem em
 * res/drawable-hdpi e acrescente uma linha aqui. Nada mais precisa mudar.
 *
 * [notificacaoId] é opcional: um trecho curto (uma vinheta) usado só como som de notificação.
 */
enum class MsxGame(
    val nome: String,
    val maquina: Maquina,
    @param:RawRes val arquivoId: Int,
    @param:DrawableRes val imagemId: Int,
    @param:RawRes val notificacaoId: Int? = null,
) {
    ANTARTIC_ADVENTURE("Antarctic Adventure", Maquina.MSX1, R.raw.antartic, R.drawable.game_antartic),
    GOONIES("Goonies", Maquina.MSX1, R.raw.goonies, R.drawable.goonies),
    GUARDIC("Guardic", Maquina.MSX1, R.raw.guardic, R.drawable.game_guardic),
    KINGS_VALLEY("King's Valley", Maquina.MSX1, R.raw.kingsvalley, R.drawable.kingsvalley),
    KNIGHTMARE("Knightmare", Maquina.MSX1, R.raw.knightmare, R.drawable.knightmare, R.raw.knightmare_notificacao),
    MAZE_OF_GALIOUS("The Maze of Galious", Maquina.MSX1, R.raw.galious, R.drawable.galious),
    GRADIUS2("Gradius 2", Maquina.MSX1, R.raw.gradius2, R.drawable.gradius2),
    NEMESIS3(
        "Nemesis 3: The Eve of Destruction", Maquina.MSX1, R.raw.nemesis3, R.drawable.nemesis3,
        R.raw.nemesis3_notificacao,
    ),
    PACMANIA("Pac-Mania", Maquina.MSX1, R.raw.pacmania, R.drawable.pacmania),
    PIPPOLS("Pippols", Maquina.MSX1, R.raw.pippols, R.drawable.pippols),
    SALAMANDER("Salamander", Maquina.MSX1, R.raw.salamander, R.drawable.salamander),
    ZANAC("Zanac", Maquina.MSX1, R.raw.zanac, R.drawable.zanac),

    ALESTE("Aleste 2", Maquina.MSX2, R.raw.aleste, R.drawable.aleste),
    FIREHAWK("Fire Hawk", Maquina.MSX2, R.raw.firehawk, R.drawable.firehawk),
    FRAY("Fray", Maquina.MSX2, R.raw.fray, R.drawable.fray),
    GOLVELLIUS("Golvellius", Maquina.MSX2, R.raw.golvellius, R.drawable.golvellius),
    METAL_GEAR("Metal Gear", Maquina.MSX2, R.raw.metalgear1, R.drawable.metalgear1),
    SD_SNATCHER("SD Snatcher", Maquina.MSX2, R.raw.sdsnatcher, R.drawable.sdsnatcher),
    SPACE_MANBOW("Space Manbow", Maquina.MSX2, R.raw.spacemanbow, R.drawable.spacemanbow),
    USAS("Treasure of USAS", Maquina.MSX2, R.raw.usas, R.drawable.usas);

    /** O áudio salvo para [tipo]: a vinheta curta para notificação, se houver; senão a música. */
    @RawRes
    fun audioPara(tipo: TipoToque): Int =
        if (tipo == TipoToque.NOTIFICACAO) notificacaoId ?: arquivoId else arquivoId

    companion object {
        fun da(maquina: Maquina): List<MsxGame> = entries.filter { it.maquina == maquina }
    }
}
