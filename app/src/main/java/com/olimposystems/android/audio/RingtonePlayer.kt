package com.olimposystems.android.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.olimposystems.android.MsxGame

/** Toca um jogo por vez, com foco de áudio. `tocando` é observável pelo Compose. */
class RingtonePlayer(context: Context) {

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(AudioManager::class.java)
    private val atributos = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private var player: MediaPlayer? = null
    private var pedidoFoco: AudioFocusRequest? = null
    private var temFoco = false

    var tocando by mutableStateOf<MsxGame?>(null)
        private set

    private val ouvinteFoco = AudioManager.OnAudioFocusChangeListener { mudanca ->
        if (mudanca < 0) para() // qualquer AUDIOFOCUS_LOSS*
    }

    fun alterna(game: MsxGame) {
        if (tocando == game) para() else toca(game)
    }

    fun toca(game: MsxGame) {
        para()
        if (!pedeFoco()) {
            Log.w(TAG, "foco de áudio negado")
            return
        }
        val mp = MediaPlayer.create(appContext, game.arquivoId, atributos, audioManager.generateAudioSessionId())
        if (mp == null) {
            Log.w(TAG, "não foi possível tocar ${game.nome}")
            liberaFoco()
            return
        }
        mp.setOnCompletionListener { para() }
        mp.start()
        player = mp
        tocando = game
        Log.i(TAG, "tocando ${game.nome}...")
    }

    /** Posição e duração (ms) do que está tocando, ou null se nada toca. */
    fun progresso(): Pair<Int, Int>? = player?.let { mp ->
        try {
            mp.currentPosition to mp.duration
        } catch (e: IllegalStateException) {
            null
        }
    }

    fun para() {
        player?.run {
            if (isPlaying) stop()
            release()
        }
        player = null
        tocando = null
        liberaFoco()
    }

    private fun pedeFoco(): Boolean {
        val resultado = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pedido = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(atributos)
                .setOnAudioFocusChangeListener(ouvinteFoco)
                .build()
            pedidoFoco = pedido
            audioManager.requestAudioFocus(pedido)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(ouvinteFoco, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        }
        temFoco = resultado == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        return temFoco
    }

    private fun liberaFoco() {
        if (!temFoco) return
        temFoco = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pedidoFoco?.let { audioManager.abandonAudioFocusRequest(it) }
            pedidoFoco = null
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(ouvinteFoco)
        }
    }

    private companion object {
        const val TAG = "msx player"
    }
}
