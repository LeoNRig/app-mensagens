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
import com.leonr.appmensagem.R
import com.leonr.appmensagem.activities.MensagemActivity
import com.leonr.appmensagem.adapters.ContatosAdapter
import com.leonr.appmensagem.databinding.FragmentContatosBinding
import com.leonr.appmensagem.model.Usuario
import com.leonr.appmensagem.utils.Constantes

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )

        contatosAdapter = ContatosAdapter{usuario->
            val intent = Intent(context, MensagemActivity::class.java)
            intent.putExtra("dadosDestinatario", usuario)
            intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
                DividerItemDecoration(
                    context, LinearLayoutManager.VERTICAL
                )
        )

        return binding.root
//        return inflater.inflate(
//            R.layout.fragment_contatos, container, false)
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {

        eventoSnapshot = firestore
            .collection("usuarios")
            .addSnapshotListener { querySnapshot, erro ->

                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents

                documentos?.forEach{documentSnapshot ->

                    val idUsuarioLogado = firebaseAuth.currentUser?.uid
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if(usuario != null && idUsuarioLogado != null){
//                        Log.i("fragmentos_contatos","nome ${usuario.nome}")
                        if(idUsuarioLogado != usuario.id){
                            listaContatos.add(usuario)
                        }
                    }
                }
                if (listaContatos.isNotEmpty()){
                    contatosAdapter.adicionarLista(listaContatos)
                }

            }
        }
    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }
}