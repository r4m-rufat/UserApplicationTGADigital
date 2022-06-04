package com.codingwithrufat.userapplication.presentation.ui.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingwithrufat.userapplication.databinding.ActivityMainBinding
import com.codingwithrufat.userapplication.util.NetworkState
import com.codingwithrufat.userapplication.util.TAG

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter
    private lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        initRecyclerView()
        clickSearchButton()
        observeSearchQuery()

        getObservableData()


    }

    private fun clickSearchButton(){

        binding.btnSearch.setOnClickListener {
            val name = binding.eTxtSearch.text.toString().trim()
            viewModel.setSearchQuery(name)
        }

    }

    private fun observeSearchQuery() {
        viewModel.search.observe(this) { search ->
            viewModel.fetchUsers(search)
        }
    }

    private fun getObservableData(){

        viewModel.users.observe(this) { network_state ->
            when (network_state) {
                is NetworkState.SUCCESS -> {
                    mainAdapter.updateList(network_state.data)
                    Log.d(TAG, "getObservableData: Data successfully comes")
                    binding.progressBar.visibility = GONE
                }
                is NetworkState.ERROR -> {
                    Log.d(TAG, "getObservableData: ${network_state.exception}")
                    binding.progressBar.visibility = GONE
                }
                is NetworkState.LOADING -> {
                    Log.d(TAG, "getObservableData: Data is loading...")
                    binding.progressBar.visibility = VISIBLE

                }
            }
        }

    }

    private fun initRecyclerView() {
        mainAdapter = MainAdapter(this, emptyList())
        binding.usersRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mainAdapter
        }
    }

}