package com.codingwithrufat.userapplication.presentation.ui.users

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingwithrufat.userapplication.R
import com.codingwithrufat.userapplication.network.models.ItemsItem
import com.codingwithrufat.userapplication.presentation.ui.user_detail.UserDetailActivity

class MainAdapter(private val context: Context, private var list: List<ItemsItem?>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    // updated list when data is changed
    fun updateList(new_userList: List<ItemsItem?>){
        list = new_userList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_user_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_reputation.text = "${list[position]?.reputation}"
        holder.txt_username.text = list[position]?.displayName
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserDetailActivity::class.java)
            intent.putExtra("profile_image", list[position]?.profileImage)
            intent.putExtra("username", list[position]?.displayName)
            intent.putExtra("reputation", list[position]?.reputation)
            intent.putExtra("bronze", list[position]?.badgeCounts?.bronze)
            intent.putExtra("gold", list[position]?.badgeCounts?.gold)
            intent.putExtra("silver", list[position]?.badgeCounts?.silver)
            intent.putExtra("location", list[position]?.location)
            intent.putExtra("creation_date", list[position]?.creationDate)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        if (list.isEmpty()){
            return 0
        }
        return list.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txt_reputation = itemView.findViewById<TextView>(R.id.txt_reputation)
        val txt_username = itemView.findViewById<TextView>(R.id.txt_username)
    }

}