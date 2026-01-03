package com.negociofechado.modulos.interesse.entity;

public enum StatusInteresse {
    PENDENTE,      // Profissional demonstrou interesse, aguardando cliente visualizar
    VISUALIZADO,   // Cliente visualizou o interesse
    CONTRATADO,    // Cliente contratou este profissional
    REJEITADO      // Cliente rejeitou/ignorou
}
