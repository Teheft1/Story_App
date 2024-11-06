package com.teheft.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.teheft.storyapp.R
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.pref.dataStore
import com.teheft.storyapp.databinding.ActivityDetailBinding
import com.teheft.storyapp.utils.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("STORY_ID")

        detailViewModel.detailStory(id.toString()).observe(this){result ->
            if (result != null){
                when(result){
                    is Result.Loading -> {
                        binding.progressBar3.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.progressBar3.visibility = View.GONE
                        AlertDialog.Builder(this@DetailActivity).apply {
                            setTitle("Error Fetch Data")
                            setMessage(result.error.toString())
                            setPositiveButton("Tutup"){dialog,_ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Success -> {
                        binding.progressBar3.visibility = View.GONE
                        val data = result.data
                        Glide
                            .with(this)
                            .load(data.photoUrl)
                            .placeholder(R.drawable.place_holder_24)
                            .into(binding.ivPhoto)

                        binding.tvName.text = data.name
                        binding.tvDescription.text = data.description
                    }
                }
            }
        }
    }
}