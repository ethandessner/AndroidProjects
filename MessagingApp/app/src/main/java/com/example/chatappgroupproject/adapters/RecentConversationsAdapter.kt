package com.example.chatappgroupproject.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappgroupproject.adapters.RecentConversationsAdapter.ConversionViewHolder
import com.example.chatappgroupproject.databinding.ItemContainerRecentConversationBinding
import com.example.chatappgroupproject.listeners.ConversionListener
import com.example.chatappgroupproject.models.ChatMessage
import com.example.chatappgroupproject.models.User

class RecentConversationsAdapter(
    private val chatMessages: List<ChatMessage>,
    private val conversionListener: ConversionListener
) : RecyclerView.Adapter<ConversionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    inner class ConversionViewHolder(var binding: ItemContainerRecentConversationBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun setData(chatMessage: ChatMessage) {
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversationImage))
            binding.textName.text = chatMessage.conversationName
            binding.textRecentMessage.text = chatMessage.message
            binding.root.setOnClickListener { v: View? ->
                val user = User()
                user.id = chatMessage.conversationId!!
                user.name = chatMessage.conversationName!!
                user.image = chatMessage.conversationImage!!
                conversionListener.onConversionClicked(user)
            }
        }
    }

    private fun getConversionImage(encodedImage: String?): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}