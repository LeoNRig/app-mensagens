package com.leonr.appmensagem.utils

import android.app.Activity
import android.widget.Toast

fun Activity.exibirMensagem(mensagem: String){
    Toast.makeText(applicationContext, mensagem, Toast.LENGTH_LONG).show()
}