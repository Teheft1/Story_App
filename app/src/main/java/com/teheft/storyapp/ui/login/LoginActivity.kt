package com.teheft.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.teheft.storyapp.MainActivity
import com.teheft.storyapp.R
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.pref.dataStore
import com.teheft.storyapp.databinding.ActivityLoginBinding
import com.teheft.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var itIsShown = false
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginViewModel.login(email, password).observe(this@LoginActivity){result ->
                when(result){
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        if(!itIsShown){
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Signup Error")
                                setMessage(result.error.toString())
                                setPositiveButton("Tutup"){dialog,_ -> dialog.dismiss()}
                                create()
                                show()
                            }
                            itIsShown = true
                        }
                    }
                    is Result.Loading -> {binding.progressBar.visibility = View.VISIBLE}
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if(!itIsShown){
                            AlertDialog.Builder(this@LoginActivity).apply{
                                setTitle("Login Berhasil")
                                setMessage(result.data)
                                setPositiveButton("Lanjut"){_,_ ->
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                            itIsShown = true
                        }
                    }
                }
                if (result is Result.Error || result is Result.Success) {
                    itIsShown = false
                }
            }
        }
    }
}