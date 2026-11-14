package com.olimposystems.android.util

import android.media.RingtoneManager
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.StringRes
import com.olimposystems.android.R

/** Onde o som pode ser usado: toque de chamada, notificação ou alarme. */
enum class TipoToque(
    /** Constante de [RingtoneManager] (TYPE_RINGTONE etc.). */
    val tipoSistema: Int,
    /** Pasta pública onde o arquivo é salvo. */
    val pasta: String,
    /** Coluna do MediaStore que marca o arquivo para este uso. */
    val coluna: String,
    /** Rótulo no menu e no selo do cartão ("Ringtone"). */
    @param:StringRes val rotulo: Int,
    /** Nome no meio de uma frase ("ringtone", "notification sound"). */
    @param:StringRes val nomeNaFrase: Int,
) {
    TOQUE(
        RingtoneManager.TYPE_RINGTONE, Environment.DIRECTORY_RINGTONES,
        MediaStore.Audio.AudioColumns.IS_RINGTONE, R.string.tipo_toque, R.string.frase_toque,
    ),
    NOTIFICACAO(
        RingtoneManager.TYPE_NOTIFICATION, Environment.DIRECTORY_NOTIFICATIONS,
        MediaStore.Audio.AudioColumns.IS_NOTIFICATION, R.string.tipo_notificacao, R.string.frase_notificacao,
    ),
    ALARME(
        RingtoneManager.TYPE_ALARM, Environment.DIRECTORY_ALARMS,
        MediaStore.Audio.AudioColumns.IS_ALARM, R.string.tipo_alarme, R.string.frase_alarme,
    ),
}
