package com.olimposystems.android

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

enum class Maquina(@param:StringRes val titulo: Int, @param:DrawableRes val logo: Int) {
    MSX1(R.string.msx1, R.drawable.msx1_default),
    MSX2(R.string.msx2, R.drawable.msx2_default),
}

/**
 * Todos os toques do app. Para adicionar um jogo: coloque o .ogg em res/raw, a imagem em
 * res/drawable-hdpi e acrescente uma linha aqui. Nada mais precisa mudar.
 */
enum class MsxGame(
    val nome: String,
    val maquina: Maquina,
    @param:RawRes val arquivoId: Int,
    @param:DrawableRes val imagemId: Int,
) {
    ANTARTIC_ADVENTURE("Antartic Adventure", Maquina.MSX1, R.raw.antartic, R.drawable.game_antartic),
    GOONIES("Goonies", Maquina.MSX1, R.raw.goonies, R.drawable.goonies),
    GUARDIC("Guardic", Maquina.MSX1, R.raw.guardic, R.drawable.game_guardic),
    KINGS_VALLEY("King's Valley", Maquina.MSX1, R.raw.kingsvalley, R.drawable.kingsvalley),
    GRADIUS2("Gradius 2", Maquina.MSX1, R.raw.gradius2, R.drawable.gradius2),
    PACMANIA("Pacmania", Maquina.MSX1, R.raw.pacmania, R.drawable.pacmania),
    PIPPOLS("Pippols", Maquina.MSX1, R.raw.pippols, R.drawable.pippols),
    SALAMANDER("Salamander", Maquina.MSX1, R.raw.salamander, R.drawable.salamander),
    ZANAC("Zanac", Maquina.MSX1, R.raw.zanac, R.drawable.zanac),

    ALESTE("Aleste 2", Maquina.MSX2, R.raw.aleste, R.drawable.aleste),
    FIREHAWK("Firehawk", Maquina.MSX2, R.raw.firehawk, R.drawable.firehawk),
    FRAY("Fray", Maquina.MSX2, R.raw.fray, R.drawable.fray),
    GOLVELLIUS("Golvellius", Maquina.MSX2, R.raw.golvellius, R.drawable.golvellius),
    METAL_GEAR("Metal Gear", Maquina.MSX2, R.raw.metalgear1, R.drawable.metalgear1),
    SD_SNATCHER("SD Snatcher", Maquina.MSX2, R.raw.sdsnatcher, R.drawable.sdsnatcher),
    USAS("Treasure of USAS", Maquina.MSX2, R.raw.usas, R.drawable.usas);

    companion object {
        fun da(maquina: Maquina): List<MsxGame> = entries.filter { it.maquina == maquina }
    }
}
