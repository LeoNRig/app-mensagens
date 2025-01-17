package com.leonr.appmensagem.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.leonr.appmensagem.R
import com.leonr.appmensagem.adapters.ViewPagerAdapter
import com.leonr.appmensagem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarNavegacaoAbas()
    }

    private fun inicializarNavegacaoAbas() {
        val tabLayout = binding.tabLayoutPrincipal
        val viewPager = binding.viewPagerPrincipal

        val abas = listOf("Conversas", "Contatos")

        viewPager.adapter = ViewPagerAdapter(
            abas ,supportFragmentManager, lifecycle
        )

        tabLayout.isTabIndicatorFullWidth = true

        TabLayoutMediator(tabLayout,viewPager){ aba, posicao ->
            aba.text = abas[posicao]
        }.attach()
    }

    private fun inicializarToolbar() {
        val toolbar = binding.IncludeMainToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Pombo"
        }

        addMenuProvider(
            object: MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.item_perfil -> {
                            startActivity(
                                Intent(applicationContext, PerfilActivity::class.java)
                            )
                        }
                        R.id.item_sair -> {
                            deslogarUsuario()
                        }

                    }
                    return true
                }

            }
        )

    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não"){dialog, posicao -> }
            .setPositiveButton("Sim"){dialog, posicao ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
            .create()
            .show()
    }
}