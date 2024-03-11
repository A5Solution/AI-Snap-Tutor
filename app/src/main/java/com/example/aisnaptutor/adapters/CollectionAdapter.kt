package com.example.aisnaptutor.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.aisnaptutor.OnCollectionItemClick
import com.example.aisnaptutor.R
import com.example.aisnaptutor.datamodels.ChatMessage


class CollectionAdapter(private val context: Context,
                        private val listener: OnCollectionItemClick,
                        private val events: List<ChatMessage>) :
    RecyclerView.Adapter<CollectionAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTxt: TextView = itemView.findViewById(R.id.itemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_collection, parent, false)
        return EventViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentCollection = events[position]
        holder.itemTxt.text = currentCollection.message
        holder.itemView.setOnClickListener {
            listener.onItemClick(currentCollection.message.toString())
        }
    }


    override fun getItemCount(): Int {
        return events.size
    }
}
