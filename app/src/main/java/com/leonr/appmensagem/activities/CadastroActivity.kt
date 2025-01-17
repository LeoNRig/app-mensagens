package com.leonr.appmensagem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.leonr.appmensagem.databinding.ActivityCadastroBinding
import com.leonr.appmensagem.model.Usuario
import com.leonr.appmensagem.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarToolbar()
        inicializarEventoClique()
    }

    private fun inicializarEventoClique() {

        binding.btnCadastrar.setOnClickListener {
            if ( validarCampos()){
                cadastrarUsuario(nome,email,senha)
            }
        }

    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
      firebaseAuth.createUserWithEmailAndPassword(
          email, senha
      ).addOnCompleteListener{ resultado ->
          if(resultado.isSuccessful){
          val idUsuario = resultado.result.user?.uid
          if(idUsuario != null){
              val usuario = Usuario(
                  idUsuario,nome,email
              )
              salvarUsuarioFirestore(usuario)
          }

        }
      }.addOnFailureListener {erro ->
          try {
              throw erro
          }catch (erroSenhafraca: FirebaseAuthWeakPasswordException) {
              erroSenhafraca.printStackTrace()
              exibirMensagem("Senha Fraca, digite outra senha")
          }catch (erroEmailExistente: FirebaseAuthUserCollisionException) {
              erroEmailExistente.printStackTrace()
              exibirMensagem("O email ja possui conta")
          }catch (erroEmailInvalido: FirebaseAuthInvalidCredentialsException){
              erroEmailInvalido.printStackTrace()
            exibirMensagem("Email Inválido, digite outro email")
          }
      }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        firestore
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao fazer o seu cadastro")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener{
                exibirMensagem("Erro ao fazer o seu cadastro")
            }
    }

    private fun validarCampos(): Boolean {

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        if(nome.isNotEmpty()){
            binding.textInputLayoutNome.error = null
            if (email.isNotEmpty()){
                binding.textInputLayoutNome.error = null

                if (senha.isNotEmpty()){
                    binding.textInputLayoutNome.error = null
                }else{
                    binding.textInputLayoutSenha.error = "Preencha sua senha!"
                    return false
                }
            }else{
                binding.textInputLayoutEmail.error = "Preencha seu email!"
                return false
            }
            return true
        }else{
            binding.textInputLayoutNome.error = "Preencha seu nome!"
            return false
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.IncludeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}