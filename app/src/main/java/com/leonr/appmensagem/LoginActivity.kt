package com.leonr.appmensagem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.leonr.appmensagem.databinding.ActivityLoginBinding

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


    }

    private fun InicializarEventClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this,CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener {
            if(validarCampos()){
                
            }
        }


    }

    private fun validarCampos(): Boolean {
        
    }
}