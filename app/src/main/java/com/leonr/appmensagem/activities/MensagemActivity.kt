package com.leonr.appmensagem.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.leonr.appmensagem.R
import com.leonr.appmensagem.adapters.ConversasAdapter
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

    private lateinit var listenerRegistration: ListenerRegistration

    private var dadosDestinatario: Usuario? = null

    private lateinit var conversasAdapter: ConversasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recuperarDadosUsuariosDestinatarios()
        inicializarToolbar()
        inicializarEventoClique()
        inicializarRecyclerView()
        inicializarListeners()
    }

    private fun inicializarRecyclerView() {
        with(binding){
            conversasAdapter = ConversasAdapter()
            rvMensagem.adapter = conversasAdapter
            rvMensagem.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListeners() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if(idUsuarioRemetente != null && idUsuarioDestinatario != null){

             listenerRegistration = firestore
                .collection(Constantes.BD_MENSAGEM)
                .document(idUsuarioRemetente)
                .collection(idUsuarioDestinatario)
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, erro ->

                    if(erro != null ){
                        exibirMensagem("Erro ao recuperar mensagem")
                    }
                    val listaMensagens = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents
                    documentos?.forEach{documentSnapshot ->
                        val mensagem = documentSnapshot.toObject(Mensagem::class.java)
                        if(mensagem != null){
                            listaMensagens.add(mensagem)
                            Log.i("exibicao_msg", mensagem.mensagem)
                    }
                    }

                    if(listaMensagens.isNotEmpty()){
                        conversasAdapter.adicionarLista(listaMensagens)
                    }

                }
        }
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