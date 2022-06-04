package com.codingwithrufat.userapplication.presentation.ui.user_detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.codingwithrufat.userapplication.R
import com.codingwithrufat.userapplication.databinding.ActivityUserDetailBinding
import com.codingwithrufat.userapplication.util.convertBadgeParametersToString
import com.codingwithrufat.userapplication.util.convertUTCToDateFormat

class UserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUserItems()
        clickedBackButton()
    }

    /**
     * get the values of item which clicked in MainAdapter with intent
     */
    private fun setUserItems(){
        Glide.with(this).load(intent.getStringExtra("profile_image")).placeholder(R.drawable.ic_person).circleCrop().into(binding.profileImage)
        binding.txtUsername.text = intent.getStringExtra("username")
        binding.txtReputation.text = "${intent.getIntExtra("reputation", 0)}"
        binding.txtBadges.text = convertBadgeParametersToString("Gold", intent.getIntExtra("gold", 0)) + "," +
                convertBadgeParametersToString("Silver", intent.getIntExtra("silver", 0)) + "," +
                convertBadgeParametersToString("Bronze", intent.getIntExtra("bronze", 0))
        binding.txtLocation.text = intent.getStringExtra("location")
        binding.txtCreationDate.text = "${intent.getIntExtra("creation_date", 0).convertUTCToDateFormat()}"
    }


    private fun clickedBackButton() {

        binding.icBackArrow.setOnClickListener {
            finish() // finish the activity after clicked back button
        }

    }



}