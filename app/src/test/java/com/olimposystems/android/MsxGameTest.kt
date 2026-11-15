package com.olimposystems.android

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MsxGameTest {

    @Test
    fun cadaMaquinaTemSeusJogos() {
        assertEquals(12, MsxGame.da(Maquina.MSX1).size)
        assertEquals(8, MsxGame.da(Maquina.MSX2).size)
        assertEquals(MsxGame.entries.size, Maquina.entries.sumOf { MsxGame.da(it).size })
    }

    @Test
    fun audiosEImagensNaoSeRepetem() {
        val audios = MsxGame.entries.map { it.arquivoId } + MsxGame.entries.mapNotNull { it.notificacaoId }
        val imagens = MsxGame.entries.map { it.imagemId }
        assertEquals("áudio repetido", audios.size, audios.toSet().size)
        assertEquals("imagem repetida", imagens.size, imagens.toSet().size)
    }

    /**
     * O MediaPlayer do Android não toca um .ogg que também tenha vídeo (por exemplo, a capa de
     * um MP3 convertida em Theora pelo ffmpeg). O primeiro stream de cada arquivo tem que ser
     * Vorbis, e o arquivo não pode ter nenhum outro codec.
     */
    @Test
    fun oggsSaoSoAudioVorbis() {
        val arquivos = File("src/main/res/raw").listFiles { f -> f.extension == "ogg" }.orEmpty()
        assertTrue("nenhum .ogg encontrado", arquivos.isNotEmpty())
        for (f in arquivos) {
            val bytes = f.readBytes()
            val cabecalho = String(bytes, 28, 7, Charsets.ISO_8859_1)
            assertEquals("${f.name}: primeiro stream não é Vorbis", "\u0001vorbis", cabecalho)
            val texto = String(bytes, Charsets.ISO_8859_1)
            assertTrue("${f.name}: tem stream de vídeo Theora", "\u0080theora" !in texto)
        }
    }

    @Test
    fun nomesPreenchidos() {
        assertTrue(MsxGame.entries.all { it.nome.isNotBlank() })
    }
}
