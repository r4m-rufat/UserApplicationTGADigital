package com.kivitool.roomdatabasetutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kivitool.roomdatabasetutorial.datas.db.User

class UsersRecyclerView(val listener: OnItemClickListener, val items: List<User>) : RecyclerView.Adapter<UsersRecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersRecyclerView.ViewHolder {

        var inflater = LayoutInflater.from(parent.context).inflate(R.layout.layout_for_recyler_view, parent, false)
        return ViewHolder(inflater, listener)

    }

    override fun onBindViewHolder(holder: UsersRecyclerView.ViewHolder, position: Int) {
        holder.id.text = items[position].userID.toString()
        holder.fullname.text = items[position].username + " " + items[position].username
        holder.gmail.text = items[position].gmail

        holder.imageDelete.setOnClickListener {
            listener.onDeleteItemClickListener(items[position])
        }

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(items[position])
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        var fullname: TextView = view.findViewById(R.id.txtFullName)
        var id: TextView = view.findViewById(R.id.txtUserID)
        var gmail: TextView = view.findViewById(R.id.txtGmail)
        var imageDelete: ImageView = view.findViewById(R.id.deleteItem)

    }


    interface OnItemClickListener{

        fun onDeleteItemClickListener(user: User)
        fun onItemClickListener(user: User)

    }


}