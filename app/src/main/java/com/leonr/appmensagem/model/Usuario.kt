package com.leonr.appmensagem.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    var id: String = "",
    var nome: String = "",
    var email: String = "",
    var foto: String = ""
) : Parcelable
