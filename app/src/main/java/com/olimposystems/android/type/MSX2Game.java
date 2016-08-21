package com.olimposystems.android.type;

import com.olimposystems.android.R;

public enum MSX2Game {

    aleste("Aleste",R.id.botao_play_aleste,R.id.botao_use_ringtone_aleste,R.raw.aleste),
    firehawk("Firehawk",R.id.botao_play_firehawk,R.id.botao_use_ringtone_firehawk,R.raw.firehawk),
    fray("Fray",R.id.botao_play_fray,R.id.botao_use_ringtone_fray,R.raw.fray),
    golvellius("Golvellius",R.id.botao_play_golvellius1,R.id.botao_use_ringtone_golvellius1,R.raw.golvellius),
    metalgear1("Metal Gear",R.id.botao_play_metalgear,R.id.botao_use_ringtone_metalgear,R.raw.metalgear1),
    sdsnatcher("SD Snatcher",R.id.botao_play_sdsnatcher,R.id.botao_use_ringtone_sdsnatcher,R.raw.sdsnatcher),
    usas("Treasure of USAS",R.id.botao_play_usas,R.id.botao_use_ringtone_usas,R.raw.usas);

    private String nome;
    private int botaoPlayId;
    private int botaoRingtoneId;
    private int arquivoId;

    MSX2Game(String nome, int botaoPlayId, int botaoRingtoneId, int arquivoId) {
        this.nome = nome;
        this.botaoPlayId = botaoPlayId;
        this.botaoRingtoneId = botaoRingtoneId;
        this.arquivoId = arquivoId;
    }

    public String nome() {
        return nome;
    }

    public int botaoPlayId() {
        return botaoPlayId;
    }

    public int botaoRingtoneId() {
        return botaoRingtoneId;
    }

    public int arquivoId() {
        return arquivoId;
    }
}
