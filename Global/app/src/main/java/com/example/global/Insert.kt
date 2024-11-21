package com.example.global

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.global.databinding.ActivityInsertBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Insert : AppCompatActivity() {

    private val binding by lazy {
        ActivityInsertBinding.inflate(layoutInflater)
    }

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()  // Instância do Firestore
    }

    private val autenticacao by lazy {
        FirebaseAuth.getInstance()  // Instância do Firebase Auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Ajuste do layout para o modo Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ação do botão btnHome1 - Redirecionar para a tela Home
        binding.btnHome1.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        // Ação do botão btnLogout - Realizar logout
        binding.btnLogout.setOnClickListener {
            autenticacao.signOut()  // Desloga o usuário
            val intent = Intent(this, MainActivity::class.java)  // Redireciona para o login
            startActivity(intent)
            finish()
        }

        // Inserir dados no Firestore ao clicar no botão
        binding.btnInserir.setOnClickListener {
            val localizacao = binding.etData.text.toString()
            val nomeCompleto = binding.editNomeCompleto.text.toString()
            val telefone = binding.editTelefone.text.toString()

            if (localizacao.isEmpty() || nomeCompleto.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            } else {
                inserirDadosNoFirebase(localizacao, nomeCompleto, telefone)
            }
        }
    }

    private fun inserirDadosNoFirebase(localizacao: String, nomeCompleto: String, telefone: String) {
        val idUsuarioAtual = autenticacao.currentUser?.uid

        if (idUsuarioAtual != null) {
            val dadosMap = hashMapOf(
                "localizacao" to localizacao,
                "nomeCompleto" to nomeCompleto,
                "telefone" to telefone
            )

            bancoDados.collection("Id1")
                .document(idUsuarioAtual)
                .collection("id1")
                .add(dadosMap)
                .addOnSuccessListener {
                    AlertDialog.Builder(this)
                        .setTitle("SUCESSO")
                        .setMessage("Dados inseridos com sucesso!")
                        .setPositiveButton("OK", null)
                        .create().show()

                    // Limpar os campos após inserção
                    binding.etData.text.clear()
                    binding.editNomeCompleto.text.clear()
                    binding.editTelefone.text.clear()
                }
                .addOnFailureListener { e ->
                    AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage("Erro ao inserir dados: ${e.message}")
                        .setPositiveButton("OK", null)
                        .create().show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
