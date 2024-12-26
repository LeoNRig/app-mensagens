package com.leonr.appmensagem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.leonr.appmensagem.databinding.ActivityLoginBinding
import com.leonr.appmensagem.utils.exibirMensagem

class LoginActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var email: String
    private lateinit var senha: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        InicializarEventClique()
//        firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if(usuarioAtual != null){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun InicializarEventClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this,CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener {
            if(validarCampos()){
                logarUsuario()
            }
        }


    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener {
            exibirMensagem("Logado com Sucesso")
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }.addOnFailureListener { erro ->
            try {
                throw erro
            } catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("Email não encontrado")
            } catch (erroUsuarioInvalido: FirebaseAuthInvalidCredentialsException) {
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("Email ou Senha não encontrado")
            }
        }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if(email.isNotEmpty()){
            binding.textInputLayoutLoginEmail.error = null
            if (senha.isNotEmpty()){
                binding.textInputLayoutLoginSenha.error = null
                return true
            }else{
                binding.textInputLayoutLoginSenha.error = "Preencha o e-mail"
                return false
            }

        }else{
            binding.textInputLayoutLoginEmail.error = "Preencha o e-mail"
            return false
        }
    }
}