package com.leonr.appmensagem.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.leonr.appmensagem.R
import com.leonr.appmensagem.databinding.ActivityMensagemBinding
import com.leonr.appmensagem.model.Mensagem
import com.leonr.appmensagem.model.Usuario
import com.leonr.appmensagem.utils.Constantes
import com.leonr.appmensagem.utils.exibirMensagem
import com.squareup.picasso.Picasso

class MensagemActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagemBinding.inflate(layoutInflater)
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recuperarDadosUsuariosDestinatarios()
        inicializarToolbar()
        inicializarEventoClique()

    }

    private fun inicializarEventoClique() {

        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }

    }

    private fun salvarMensagem(textoMensagem: String) {

        if (textoMensagem.isNotEmpty()){
            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if(idUsuarioRemetente != null && idUsuarioDestinatario != null){
                val mensagem = Mensagem(
                    idUsuarioRemetente, textoMensagem
                )

                salvarMensagemFirestore(
                    idUsuarioRemetente, idUsuarioDestinatario, mensagem
                )
                salvarMensagemFirestore(
                    idUsuarioDestinatario, idUsuarioRemetente, mensagem
                )

                binding.editMensagem.setText("")

            }

        }

    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        mensagem: Mensagem
    ) {
        firestore
            .collection(Constantes.BD_MENSAGEM)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                exibirMensagem("Erro ao enviar mensagem")
            }
    }

    private fun inicializarToolbar() {
            val toolbar = binding.tbMensagem
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                title = ""
                if(dadosDestinatario != null){
                    binding.textNome.text = dadosDestinatario!!.nome
                    Picasso.get()
                        .load(dadosDestinatario!!.foto)
                        .into(binding.imgFotoPerfil)
                }
                setDisplayHomeAsUpEnabled(true)
            }
    }

    private fun recuperarDadosUsuariosDestinatarios() {

        val extras = intent.extras
        if(extras != null) {
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTATO) {

                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Usuario::class.java)
                }else{
                    extras.getParcelable("dadosDestinatario")
                }

            } else if (origem == Constantes.ORIGEM_CONVERSA) {



            }
        }

    }
}