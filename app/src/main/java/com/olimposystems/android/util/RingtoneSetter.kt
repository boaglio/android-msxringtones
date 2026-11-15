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
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.olimposystems.android.MsxGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Salva o .ogg de res/raw na pasta pública do [TipoToque] (Ringtones/, Notifications/,
 * Alarms/), registra no MediaStore e, se o app puder alterar as configurações do sistema,
 * já o define como padrão. Sem essa permissão o arquivo fica salvo e o usuário pode
 * escolhê-lo nas configurações de som.
 */
object RingtoneSetter {

    private const val TAG = "msx ringtone"
    private const val AUDIO_OGG = "audio/ogg"
    private const val PREFS = "toques"

    sealed interface Resultado {
        /** Salvo e definido como padrão. */
        data object Ok : Resultado
        /** Salvo, mas sem permissão para defini-lo: o usuário decide (permitir ou escolher sozinho). */
        data class Salvo(val uri: Uri) : Resultado
        /** Android 6–9: falta WRITE_EXTERNAL_STORAGE para salvar o arquivo. */
        data object PrecisaPermissaoArmazenamento : Resultado
        data class Erro(val causa: Exception) : Resultado
    }

    fun podeAlterarConfiguracoes(context: Context) = Settings.System.canWrite(context)

    suspend fun define(context: Context, game: MsxGame, tipo: TipoToque): Resultado {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Resultado.PrecisaPermissaoArmazenamento
        }

        val audioId = game.audioPara(tipo)
        val nomeArquivo = context.resources.getResourceEntryName(audioId) + ".ogg"
        return withContext(Dispatchers.IO) {
            try {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    salvaMediaStore(context, game, tipo, audioId, nomeArquivo)
                } else {
                    salvaLegado(context, game, tipo, audioId, nomeArquivo)
                }
                Log.i(TAG, "${tipo.name} salvo: $uri")
                lembra(context, tipo, game, uri)
                if (podeAlterarConfiguracoes(context)) {
                    aplica(context, tipo, uri)
                    Resultado.Ok
                } else {
                    Resultado.Salvo(uri)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Erro salvando $nomeArquivo como ${tipo.name}", e)
                Resultado.Erro(e)
            }
        }
    }

    /** Define [uri] como som padrão do [tipo]. Exige [podeAlterarConfiguracoes]. */
    fun aplica(context: Context, tipo: TipoToque, uri: Uri) {
        RingtoneManager.setActualDefaultRingtoneUri(context, tipo.tipoSistema, uri)
        Log.i(TAG, "${tipo.name} definido: $uri")
    }

    /**
     * Para cada jogo, os tipos em que ele é o som padrão agora. Compara o padrão atual do
     * sistema com o arquivo que o app salvou por último para cada tipo, então também
     * reconhece quando o usuário o escolheu sozinho nas configurações de som.
     */
    fun atuais(context: Context): Map<MsxGame, Set<TipoToque>> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val resultado = mutableMapOf<MsxGame, MutableSet<TipoToque>>()
        for (tipo in TipoToque.entries) {
            val salvo = prefs.getString("${tipo.name}_uri", null)?.let(Uri::parse) ?: continue
            val jogo = prefs.getString("${tipo.name}_jogo", null)
                ?.let { nome -> MsxGame.entries.find { it.name == nome } } ?: continue
            val atual = try {
                RingtoneManager.getActualDefaultRingtoneUri(context, tipo.tipoSistema)
            } catch (e: SecurityException) {
                null
            }
            if (atual != null && mesmoAudio(atual, salvo)) {
                resultado.getOrPut(jogo) { mutableSetOf() }.add(tipo)
            }
        }
        return resultado
    }

    internal fun lembra(context: Context, tipo: TipoToque, game: MsxGame, uri: Uri) {
        // Guarda só no aparelho qual som foi salvo para cada tipo (para o selo no cartão).
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString("${tipo.name}_uri", uri.toString())
            putString("${tipo.name}_jogo", game.name)
        }
    }

    /** URIs do MediaStore podem variar no volume (external/external_primary) e nos parâmetros. */
    private fun mesmoAudio(a: Uri, b: Uri) =
        a.authority == b.authority && a.lastPathSegment != null && a.lastPathSegment == b.lastPathSegment

    /** Android 10+: grava via MediaStore na pasta do tipo, sem permissão de armazenamento. */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun salvaMediaStore(
        context: Context, game: MsxGame, tipo: TipoToque, @RawRes audioId: Int, nomeArquivo: String,
    ): Uri {
        val resolver = context.contentResolver
        val colecao = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val caminhoRelativo = tipo.pasta + "/"

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
            marcaTipo(tipo)
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(colecao, valores)
            ?: throw IOException("MediaStore insert falhou para $nomeArquivo")

        try {
            context.resources.openRawResource(audioId).use { entrada ->
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
    private fun salvaLegado(
        context: Context, game: MsxGame, tipo: TipoToque, @RawRes audioId: Int, nomeArquivo: String,
    ): Uri {
        val pasta = Environment.getExternalStoragePublicDirectory(tipo.pasta)
        if (!pasta.exists() && !pasta.mkdirs()) throw IOException("Não foi possível criar $pasta")
        val arquivo = File(pasta, nomeArquivo)

        context.resources.openRawResource(audioId).use { entrada ->
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
            marcaTipo(tipo)
        }
        return resolver.insert(colecao, valores)
            ?: throw IOException("MediaStore insert falhou para $arquivo")
    }

    private fun ContentValues.marcaTipo(tipo: TipoToque) {
        for (t in TipoToque.entries) put(t.coluna, t == tipo)
        put(MediaStore.Audio.AudioColumns.IS_MUSIC, false)
    }
}
