package com.kivitool.roomdatabasetutorial

import android.accounts.AccountManager.get
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kivitool.roomdatabasetutorial.datas.db.User

class MainActivity : AppCompatActivity(), UsersRecyclerView.OnItemClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var userList: ArrayList<User>
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.apply {

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        }

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        viewModel.getAllUsersObservers().observe(this, Observer {
            recyclerView.adapter = UsersRecyclerView(this@MainActivity, it)
            UsersRecyclerView(this@MainActivity, it).notifyDataSetChanged()
        })


    }

    override fun onDeleteItemClickListener(user: User) {
        TODO("Not yet implemented")
    }

    override fun onItemClickListener(user: User) {
        TODO("Not yet implemented")
    }


}