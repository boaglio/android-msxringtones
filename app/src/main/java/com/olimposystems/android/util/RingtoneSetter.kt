package com.olimposystems.android.util

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.olimposystems.android.MsxGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Copia o .ogg de res/raw para a pasta pública "Ringtones", registra no MediaStore
 * e o define como toque padrão.
 */
object RingtoneSetter {

    private const val TAG = "msx ringtone"
    private const val AUDIO_OGG = "audio/ogg"

    sealed interface Resultado {
        data object Ok : Resultado
        /** O usuário precisa liberar "Modificar configurações do sistema". */
        data object PrecisaPermissaoConfiguracoes : Resultado
        /** Android 6–9: falta WRITE_EXTERNAL_STORAGE. */
        data object PrecisaPermissaoArmazenamento : Resultado
        data class Erro(val causa: Exception) : Resultado
    }

    fun podeAlterarConfiguracoes(context: Context) = Settings.System.canWrite(context)

    suspend fun define(context: Context, game: MsxGame): Resultado {
        if (!podeAlterarConfiguracoes(context)) return Resultado.PrecisaPermissaoConfiguracoes
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Resultado.PrecisaPermissaoArmazenamento
        }

        val nomeArquivo = context.resources.getResourceEntryName(game.arquivoId) + ".ogg"
        return withContext(Dispatchers.IO) {
            try {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    salvaMediaStore(context, game, nomeArquivo)
                } else {
                    salvaLegado(context, game, nomeArquivo)
                }
                Log.i(TAG, "ringtone uri = $uri")
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, uri)
                Log.i(TAG, "ringtone $nomeArquivo configurado!")
                Resultado.Ok
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Erro configurando $nomeArquivo", e)
                Resultado.Erro(e)
            }
        }
    }

    /** Android 10+: grava via MediaStore em Ringtones/, sem permissão de armazenamento. */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun salvaMediaStore(context: Context, game: MsxGame, nomeArquivo: String): Uri {
        val resolver = context.contentResolver
        val colecao = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val caminhoRelativo = Environment.DIRECTORY_RINGTONES + "/"

        // Reaproveita o arquivo se já foi criado por esta instalação do app.
        resolver.query(
            colecao,
            arrayOf(MediaStore.Audio.Media._ID),
            "${MediaStore.Audio.Media.DISPLAY_NAME}=? AND ${MediaStore.Audio.Media.RELATIVE_PATH}=?",
            arrayOf(nomeArquivo, caminhoRelativo),
            null,
        )?.use { c ->
            if (c.moveToFirst()) {
                return Uri.withAppendedPath(colecao, c.getLong(0).toString())
            }
        }

        val valores = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, nomeArquivo)
            put(MediaStore.Audio.Media.TITLE, game.nome)
            put(MediaStore.Audio.Media.MIME_TYPE, AUDIO_OGG)
            put(MediaStore.Audio.Media.ARTIST, "MSX")
            put(MediaStore.Audio.Media.RELATIVE_PATH, caminhoRelativo)
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(colecao, valores)
            ?: throw IOException("MediaStore insert falhou para $nomeArquivo")

        try {
            context.resources.openRawResource(game.arquivoId).use { entrada ->
                val saida = resolver.openOutputStream(uri) ?: throw IOException("Não foi possível abrir $uri")
                saida.use { entrada.copyTo(it) }
            }
        } catch (e: IOException) {
            resolver.delete(uri, null, null)
            throw e
        }

        resolver.update(uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }, null, null)
        return uri
    }

    /** Android 6–9: grava o arquivo diretamente e registra pelo caminho (coluna DATA). */
    @Suppress("DEPRECATION")
    private fun salvaLegado(context: Context, game: MsxGame, nomeArquivo: String): Uri {
        val pasta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)
        if (!pasta.exists() && !pasta.mkdirs()) throw IOException("Não foi possível criar $pasta")
        val arquivo = File(pasta, nomeArquivo)

        context.resources.openRawResource(game.arquivoId).use { entrada ->
            FileOutputStream(arquivo).use { entrada.copyTo(it) }
        }

        val resolver = context.contentResolver
        val colecao = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        resolver.delete(colecao, "${MediaStore.MediaColumns.DATA}=?", arrayOf(arquivo.absolutePath))

        val valores = ContentValues().apply {
            put(MediaStore.MediaColumns.DATA, arquivo.absolutePath)
            put(MediaStore.MediaColumns.TITLE, game.nome)
            put(MediaStore.MediaColumns.SIZE, arquivo.length())
            put(MediaStore.MediaColumns.MIME_TYPE, AUDIO_OGG)
            put(MediaStore.Audio.Media.ARTIST, "MSX")
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
        }
        return resolver.insert(colecao, valores)
            ?: throw IOException("MediaStore insert falhou para $arquivo")
    }
}
