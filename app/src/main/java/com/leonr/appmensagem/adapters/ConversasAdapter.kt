package com.leonr.appmensagem.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.leonr.appmensagem.databinding.ItemMensagensDestinatariosBinding
import com.leonr.appmensagem.databinding.ItemMensagensRemetenteBinding
import com.leonr.appmensagem.model.Mensagem
import com.leonr.appmensagem.utils.Constantes

class ConversasAdapter: Adapter<ViewHolder>() {

    private var listaMensagens = emptyList<Mensagem>()
    fun adicionarLista(lista: List<Mensagem>){
        listaMensagens = lista
        notifyDataSetChanged()
    }

    class MensagensRemetenteViewHolder(
        private val binding: ItemMensagensRemetenteBinding
    ): ViewHolder(binding.root){

        fun bind(mensagem: Mensagem){
            binding.textMensagemRemetente.text = mensagem.mensagem
        }

        companion object{
            fun iflarLayout(parent: ViewGroup): MensagensRemetenteViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView =  ItemMensagensRemetenteBinding.inflate(
                    inflater, parent, false
                )
                return MensagensRemetenteViewHolder(itemView)
            }
        }
    }

    class MensagemDestinatarioViewHolder(
        private val binding: ItemMensagensDestinatariosBinding
    ): ViewHolder(binding.root){

        fun bind(mensagem: Mensagem){
            binding.textMensagemDestinatario.text = mensagem.mensagem
        }

        companion object{
            fun iflarLayout(parent: ViewGroup): MensagemDestinatarioViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView =  ItemMensagensDestinatariosBinding.inflate(
                    inflater, parent, false
                )
                return MensagemDestinatarioViewHolder(itemView)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        val mensagem = listaMensagens[position]
        val idUsuarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if(idUsuarioLogado == mensagem.idUsuario){
            Constantes.TIPO_REMETENTE
        }else{
            Constantes.TIPO_DESTINATARIO
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if(viewType == Constantes.TIPO_REMETENTE)
            return MensagensRemetenteViewHolder.iflarLayout(parent)

           return MensagemDestinatarioViewHolder.iflarLayout(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mensagem = listaMensagens[position]

        when(holder){
            is MensagensRemetenteViewHolder -> holder.bind(mensagem)
            is MensagemDestinatarioViewHolder -> holder.bind(mensagem)
        }

        /*val mensagemRemetenteViewHolder = holder as MensagensRemetenteViewHolder
        mensagemRemetenteViewHolder.bind()*/

    }

    override fun getItemCount(): Int {
        return listaMensagens.size
    }

}