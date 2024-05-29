package com.example.checklisttest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.checklisttest.cashtest.ItemTestActivity
import com.example.checklisttest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.checkListTestBtn.setOnClickListener {
            val intent = Intent(this, CashbookActivity::class.java)
            startActivity(intent)
        }

        binding.test1.setOnClickListener {
            val intent = Intent(this, ItemTestActivity::class.java)
            startActivity(intent)
        }

    }
}