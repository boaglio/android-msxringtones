package com.olimposystems.android

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MsxGameTest {

    @Test
    fun cadaMaquinaTemSeusJogos() {
        assertEquals(9, MsxGame.da(Maquina.MSX1).size)
        assertEquals(7, MsxGame.da(Maquina.MSX2).size)
        assertEquals(MsxGame.entries.size, Maquina.entries.sumOf { MsxGame.da(it).size })
    }

    @Test
    fun audiosEImagensNaoSeRepetem() {
        val audios = MsxGame.entries.map { it.arquivoId }
        val imagens = MsxGame.entries.map { it.imagemId }
        assertEquals("áudio repetido", audios.size, audios.toSet().size)
        assertEquals("imagem repetida", imagens.size, imagens.toSet().size)
    }

    @Test
    fun nomesPreenchidos() {
        assertTrue(MsxGame.entries.all { it.nome.isNotBlank() })
    }
}
