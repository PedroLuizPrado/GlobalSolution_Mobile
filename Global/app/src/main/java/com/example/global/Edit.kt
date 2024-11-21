package com.example.global

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.global.databinding.ActivityEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Edit : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
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

        // Verifique se o ID 'main' existe no layout activity_edit.xml
        // Se o ID correto for diferente, substitua 'android.R.id.content' pelo ID certo
        val rootView = findViewById<View>(android.R.id.content) // ou outro ID se necessário
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obter a localização atual para exibir no campo de texto
        carregarDadosDoFirebase()

        // Ação do botão btnSave - Salvar as alterações
        binding.btnSave.setOnClickListener {
            val localizacaoEditada = binding.etData.text.toString()

            if (localizacaoEditada.isEmpty()) {
                Toast.makeText(this, "Por favor, insira uma localização válida.", Toast.LENGTH_SHORT).show()
            } else {
                editarDadosNoFirebase(localizacaoEditada)
            }
        }

        // Ação do botão btnSaveUser - Salvar dados do usuário
        binding.btnSaveUsuario.setOnClickListener {
            val nomeCompleto = binding.editNomeCompleto.text.toString()
            val telefone = binding.editTelefone.text.toString()

            if (nomeCompleto.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            } else {
                salvarUsuario(nomeCompleto, telefone)
            }
        }

        // Ação do botão btnDeslogar - Realizar logout do usuário
        binding.btnDeslogar.setOnClickListener {
            autenticacao.signOut()
            Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show()
            // Redirecionar para a tela de login
            finish()  // Fecha a tela atual e volta para a anterior
        }

        // Ação do botão btnHome2 - Redirecionar para a home
        binding.btnHome2.setOnClickListener {
            // Redirecionar para a activity de home
            // Caso você tenha uma Activity Home, você pode usar o código abaixo
            // startActivity(Intent(this, HomeActivity::class.java))
            // Ou apenas fechar a tela atual
            finish() // Voltar para a tela anterior, se necessário
        }
    }

    private fun carregarDadosDoFirebase() {
        val idUsuarioAtual = autenticacao.currentUser?.uid

        if (idUsuarioAtual != null) {
            // Buscar a primeira localização na subcoleção "id1"
            bancoDados.collection("Id1")
                .document(idUsuarioAtual)  // Documento com o UID do usuário
                .collection("id1")  // Subcoleção "id1"
                .limit(1)  // Limitamos para 1 item
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documento = querySnapshot.documents[0]
                        val localizacao = documento.getString("Localizacao")
                        binding.etData.setText(localizacao)
                    } else {
                        Toast.makeText(this, "Nenhuma localização encontrada.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editarDadosNoFirebase(localizacaoEditada: String) {
        val idUsuarioAtual = autenticacao.currentUser?.uid

        if (idUsuarioAtual != null) {
            // Criar o mapa de dados para editar
            val dadosMap = hashMapOf(
                "Localizacao" to localizacaoEditada
            )

            // Atualizar os dados na subcoleção "id1" do documento com o UID do usuário
            bancoDados.collection("Id1")
                .document(idUsuarioAtual)  // Documento com o UID do usuário
                .collection("id1")  // Subcoleção "id1"
                .limit(1)  // Garantir que editaremos apenas o primeiro documento
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documento = querySnapshot.documents[0]
                        val documentId = documento.id
                        bancoDados.collection("Id1")
                            .document(idUsuarioAtual)
                            .collection("id1")
                            .document(documentId)  // ID do documento a ser editado
                            .set(dadosMap)  // Atualiza os dados
                            .addOnSuccessListener {
                                Toast.makeText(this, "Localização editada com sucesso!", Toast.LENGTH_SHORT).show()
                                binding.etData.text.clear()  // Limpar o campo após salvar
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao editar localização: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Nenhuma localização para editar encontrada.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao recuperar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarUsuario(nomeCompleto: String, telefone: String) {
        val idUsuarioAtual = autenticacao.currentUser?.uid

        if (idUsuarioAtual != null) {
            val dadosMap = hashMapOf(
                "nomeCompleto" to nomeCompleto,
                "telefone" to telefone
            )

            bancoDados.collection("usuarios")
                .document(idUsuarioAtual)
                .set(dadosMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dados do usuário salvos com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar dados do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
