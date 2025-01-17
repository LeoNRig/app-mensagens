package com.leonr.appmensagem.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Mensagem(
    val idUsuario: String = "",
    val mensagem: String = "",
    @ServerTimestamp
    val data: Date? = null
)
