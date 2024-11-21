package com.example.global

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.global.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializando a binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuração de botões
        binding.btnLogin.setOnClickListener {
            logarUsuario()
        }

        binding.txtEsqueciSenha.setOnClickListener {
            esqueceuSenha()
        }

        // Redirecionar para SignUp
        binding.txtCadastro.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun verificarUsuarioLogado() {
        val usuario = autenticacao.currentUser
        if (usuario != null) {
            // Usuário logado, vai para a Home
            startActivity(Intent(this, Home::class.java))
            finish()  // Finaliza a MainActivity para evitar que o usuário volte ao login
        }
    }

    private fun logarUsuario() {
        val email = binding.etEmail.text.toString()
        val senha = binding.etSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            exibirDialogo("Erro", "Preencha todos os campos!")
            return
        }

        autenticacao.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                verificarUsuarioLogado()  // Verifica o login após tentativa
            }
            .addOnFailureListener {
                exibirDialogo("Erro", "E-mail ou senha incorretos.")
            }
    }

    private fun esqueceuSenha() {
        val email = binding.etEmail.text.toString()

        if (email.isEmpty()) {
            exibirDialogo("Erro", "Insira seu e-mail para recuperação.")
            return
        }

        autenticacao.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                exibirDialogo("Sucesso", "E-mail de recuperação enviado.")
            }
            .addOnFailureListener {
                exibirDialogo("Erro", "Erro ao enviar e-mail de recuperação.")
            }
    }

    private fun exibirDialogo(titulo: String, mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("OK", null)
            .show()
    }
}
