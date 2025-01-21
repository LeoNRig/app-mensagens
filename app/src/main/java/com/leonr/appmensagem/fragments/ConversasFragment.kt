package com.leonr.appmensagem.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.leonr.appmensagem.R
import com.leonr.appmensagem.activities.MensagemActivity
import com.leonr.appmensagem.adapters.ContatosAdapter
import com.leonr.appmensagem.adapters.ConversasAdapter
import com.leonr.appmensagem.databinding.FragmentContatosBinding
import com.leonr.appmensagem.databinding.FragmentConversasBinding
import com.leonr.appmensagem.model.Conversa
import com.leonr.appmensagem.model.Usuario
import com.leonr.appmensagem.utils.Constantes
import com.leonr.appmensagem.utils.exibirMensagem

class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventoSnapshot: ListenerRegistration

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var convesasAdapter: ConversasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversasBinding.inflate(
            inflater, container, false
        )
        convesasAdapter = ConversasAdapter {conversa->

            val intent = Intent(context, MensagemActivity::class.java)

            val usuario = Usuario(
                id = conversa.idUsuarioDestinatario,
                nome = conversa.nome,
                foto = conversa.foto
            )

            intent.putExtra("dadosDestinatario", usuario)
//            intent.putExtra("origem", Constantes.ORIGEM_CONVERSA)
            startActivity(intent)
        }

        binding.rvConversas.adapter = convesasAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerConversas()
    }

    private fun adicionarListenerConversas() {
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if(idUsuarioRemetente != null){
            eventoSnapshot = firestore
                .collection(Constantes.CONVERSAS)
                .document(idUsuarioRemetente)
                .collection(Constantes.ULTIMAS_CONVERSAS)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->

                    if(error != null){
                        activity?.exibirMensagem("Erro ao recuperar conversa")
                    }

                    val listaConversas = mutableListOf<Conversa>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach {documentSnapshot->

                        val conversa = documentSnapshot.toObject(Conversa::class.java)
                        if(conversa != null){
                            listaConversas.add(conversa)
                            Log.i("exibicao_conversas", "${conversa.nome} - ${conversa.ultimaMensagem} ")
                        }

                    }
                    if(listaConversas.isNotEmpty()){
                        convesasAdapter.adicionarLista(listaConversas)
                    }

                }
        }
    }

}