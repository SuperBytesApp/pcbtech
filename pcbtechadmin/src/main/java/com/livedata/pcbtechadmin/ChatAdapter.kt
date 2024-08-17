package com.livedata.pcbtechadmin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val ownUserId: String,
    private val context: Context
) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderNameTextView: TextView = itemView.findViewById(R.id.username)
        val messageTextView: TextView = itemView.findViewById(R.id.progressbar)
        val time: TextView = itemView.findViewById(R.id.dateTextView)
        val chatimg : ImageView = itemView.findViewById(R.id.chatimg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Check if the message is sent by the user or others, and inflate the appropriate layout
        val layoutResId = if (viewType == VIEW_TYPE_OWN) {

            R.layout.sent_message_item_room_other
        } else {
            R.layout.sent_message_item_room
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.senderNameTextView.text = message.senderName
        holder.messageTextView.text = message.message
        holder.time.text = getTimeString(message.timestamp)


        holder.senderNameTextView.setOnClickListener {
            val intent = Intent(context, ChattingActivity::class.java)
            intent.putExtra("DATA_KEY", message.senderId)
            context.startActivity(intent)
        }



        if (message.imageUrl.toString() != "" && message.imageUrl != null){

            holder.chatimg.visibility = View.VISIBLE
            Glide.with(context)
                .load(message.imageUrl)
                .into(holder.chatimg)

            holder.chatimg.setOnClickListener {
                val intent = Intent(context, ZoomableImageActivity::class.java)
                intent.putExtra("image_url", message.imageUrl)
                context.startActivity(intent)
            }





        }else{ holder.chatimg.visibility = View.GONE }


    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        // Compare the sender's ID with the current user's ID to determine the view type
        return if (messages[position].senderId == ownUserId) {
            VIEW_TYPE_OWN
        } else {
            VIEW_TYPE_OTHER
        }
    }

    private fun getTimeString(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }


    companion object {
        private const val VIEW_TYPE_OWN = 1
        private const val VIEW_TYPE_OTHER = 2
    }
}

