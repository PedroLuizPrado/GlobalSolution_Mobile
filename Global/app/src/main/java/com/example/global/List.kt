package com.example.global

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.global.databinding.ActivityListBinding

class List : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var adapter: FirestoreRecyclerAdapter<Localizacao, LocalizacaoViewHolder>
    private val bancoDados by lazy { FirebaseFirestore.getInstance() }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configurar o RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Configuração do FirebaseRecyclerAdapter
        val idUsuarioAtual = auth.currentUser?.uid ?: ""
        Log.d("ListActivity", "UID do usuário: $idUsuarioAtual")  // Verificando o UID do usuário

        // Consulta para pegar os documentos da subcoleção 'id1' que possuem as informações
        val query = bancoDados.collection("Id1")
            .document(idUsuarioAtual)
            .collection("id1") // Subcoleção onde os dados estão armazenados

        val options = FirestoreRecyclerOptions.Builder<Localizacao>()
            .setQuery(query, Localizacao::class.java)
            .build()

        // Adaptador para exibir os dados
        adapter = object : FirestoreRecyclerAdapter<Localizacao, LocalizacaoViewHolder>(options) {
            override fun onBindViewHolder(holder: LocalizacaoViewHolder, position: Int, model: Localizacao) {
                Log.d("ListActivity", "Exibindo localização: ${model.nomeCompleto}, ${model.localizacao}")
                holder.bind(model)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalizacaoViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_localizacao, parent, false)
                return LocalizacaoViewHolder(view)
            }
        }

        // Definir o adaptador no RecyclerView
        binding.recyclerView.adapter = adapter

        // Configurar os botões de logout e home
        val btnDeslogar = findViewById<ImageView>(R.id.btnDeslogar3)
        val btnHome = findViewById<ImageView>(R.id.btnHome3)

        btnDeslogar.setOnClickListener {
            deslogarUsuario()
        }

        btnHome.setOnClickListener {
            voltarParaHome()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening() // Começar a escutar as mudanças no Firestore
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening() // Parar de escutar quando a Activity não estiver mais visível
    }

    // Função para fazer o logout
    private fun deslogarUsuario() {
        auth.signOut()  // Desloga o usuário
        val intent = Intent(this, MainActivity::class.java)  // Vai para a tela de login
        startActivity(intent)
        finish()  // Finaliza a atividade atual
    }

    // Função para voltar para a página home
    private fun voltarParaHome() {
        val intent = Intent(this, Home::class.java)  // Vai para a home
        startActivity(intent)
    }

    // ViewHolder para o item
    class LocalizacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeTextView: TextView = itemView.findViewById(R.id.nome)
        private val enderecoTextView: TextView = itemView.findViewById(R.id.endereco)

        fun bind(localizacao: Localizacao) {
            nomeTextView.text = localizacao.nomeCompleto
            enderecoTextView.text = localizacao.localizacao
        }
    }
}
