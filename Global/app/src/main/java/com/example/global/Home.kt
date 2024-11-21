package com.example.global

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.contextaware.ContextAware
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.LinearLayout
import androidx.core.view.WindowCompat

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ajuste do modo Edge-to-Edge para compatibilidade
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_home)

        // Configuração do listener para o container "Ver Lista Cinza"
        val containerGray = findViewById<LinearLayout>(R.id.container_gray)
        containerGray.setOnClickListener {
            // Redireciona para a tela List
            val intent = Intent(this, List::class.java)
            startActivity(intent)
        }

        // Configuração do listener para o container "Editar Verde"
        val containerGreen = findViewById<LinearLayout>(R.id.container_green)
        containerGreen.setOnClickListener {
            // Redireciona para a tela Edit
            val intent = Intent(this, Edit::class.java)
            startActivity(intent)
        }

        // Configuração do listener para o container "Inserir Novos Dados Amarelo"
        val containerYellow = findViewById<LinearLayout>(R.id.container_yellow)
        containerYellow.setOnClickListener {
            // Redireciona para a tela Insert
            val intent = Intent(this, Insert::class.java)
            startActivity(intent)
        }

        // Ajuste do layout para o modo Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
