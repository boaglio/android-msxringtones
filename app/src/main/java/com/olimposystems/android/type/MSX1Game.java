package com.olimposystems.android.type;

import com.olimposystems.android.R;

public enum MSX1Game {

    antarticAdventure("Antartic Adventure", R.id.botao_play_antartic,R.id.botao_use_ringtone_antartic,R.raw.antartic),
    goonies("Goonies",R.id.botao_play_goonies, R.id.botao_use_ringtone_goonies, R.raw.goonies),
    guardic("Guardic",R.id.botao_play_guardic, R.id.botao_use_ringtone_guardic, R.raw.guardic),
    kingsvalley("King's Valley",R.id.botao_play_kingsv, R.id.botao_use_ringtone_kings, R.raw.kingsvalley),
    gradius2("Gradius 2",R.id.botao_play_nemesis2, R.id.botao_use_ringtone_nemesis2, R.raw.gradius2),
    salamander("Salamander",R.id.botao_play_salamander, R.id.botao_use_ringtone_salamander, R.raw.salamander),
    pacmania("Pacmania",R.id.botao_play_pacmania, R.id.botao_use_ringtone_pacmania, R.raw.pacmania),
    pippols("Pippols",R.id.botao_play_pippols, R.id.botao_use_ringtone_pippols, R.raw.pippols),
    zanac("Zanac",R.id.botao_play_zanac, R.id.botao_use_ringtone_zanac, R.raw.zanac);

    private String nome;
    private int botaoPlayId;
    private int botaoRingtoneId;
    private int arquivoId;

    MSX1Game(String nome, int botaoPlayId, int botaoRingtoneId, int arquivoId) {
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
