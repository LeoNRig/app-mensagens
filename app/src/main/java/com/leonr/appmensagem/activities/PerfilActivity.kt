package com.leonr.appmensagem.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.leonr.appmensagem.databinding.ActivityPerfilBinding
import com.leonr.appmensagem.utils.exibirMensagem
import com.squareup.picasso.Picasso


class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        if(uri != null){
            binding.imagePerfil.setImageURI(uri)
            uploadImagemStorage(uri)
        }else{
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        solicitarPermissao()
        inicializarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuarios()
    }

    private fun recuperarDadosIniciaisUsuarios() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if(idUsuario != null){

            firestore
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener {documentSnapshot->

                    val dadosUsuarios = documentSnapshot.data
                    if (dadosUsuarios != null){
                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String

                        binding.editNomePerfil.setText(nome)
                        if(foto.isNotEmpty()){
                            Picasso.get()
                                .load(foto)
                                .into(binding.imagePerfil)
                        }

                    }

                }
        }

    }


    private fun uploadImagemStorage(uri: Uri) {
        val idUsuario = firebaseAuth.currentUser?.uid
        if( idUsuario!= null){
            storage
                .getReference("fotos")
                .child("usuarios")
                .child("id")
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener {task ->

                    exibirMensagem("Sucesso ao enviar a imagem")
                    task.metadata?.
                    reference?.
                    downloadUrl?.
                    addOnSuccessListener { url ->

                        val dados = mapOf(
                            "foto" to url.toString()
                        )
                        atualizarDadosPerfil(idUsuario, dados)

                    }

                }.addOnFailureListener{

                    exibirMensagem("Erro ao enviar a imagem")

                }
        }

    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {

        firestore.collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Atualizado com Sucesso")
            }
            .addOnFailureListener{
                exibirMensagem("Erro ao atualizar perfil usuário")
            }

    }


    private fun inicializarEventosClique() {

        binding.floatingActionButton.setOnClickListener {
            if (temPermissaoGaleria){
                gerenciadorGaleria.launch("image/*")
            }else{
                exibirMensagem("Não tem permissão")
                solicitarPermissao()
            }
        }

        binding.btnSalvar.setOnClickListener {
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if(nomeUsuario.isNotEmpty()){

                val idUsuario = firebaseAuth.currentUser?.uid
                if(idUsuario != null){
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil(idUsuario, dados)
                }

            }else{
                exibirMensagem("Preencha o nome para atualizar")
            }
        }
    }

    private fun solicitarPermissao() {

        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val listaPermissoesNegadas = mutableListOf<String>()
        if( !temPermissaoCamera )
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        if( !temPermissaoGaleria )
            listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)

        if(listaPermissoesNegadas.isNotEmpty()){
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){permissoes ->

                temPermissaoCamera = permissoes[Manifest.permission.CAMERA]
                    ?: temPermissaoCamera
                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: temPermissaoGaleria


            }
            gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())
        }


    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}