package com.teheft.storyapp.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.teheft.storyapp.R
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.pref.dataStore
import com.teheft.storyapp.databinding.ActivitySignupBinding
import com.teheft.storyapp.ui.MainViewModel
import com.teheft.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private val signupViewModel by viewModels<MainViewModel>{
            ViewModelFactory.getInstance(this, dataStore)
    }
    private lateinit var  binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            Log.d("button","button is clicked")
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                setSignUp(name, email, password)
            }else{
                binding.nameEditText.error = null
                binding.emailEditText.error = null
                binding.passwordEditText.error = null

                if (name.isEmpty()) {
                    binding.nameEditText.error = "Do Not let it empty"
                }
                if (email.isEmpty()) {
                    binding.emailEditText.error = "Do Not let it empty"
                }
                if (password.isEmpty()) {
                    binding.passwordEditText.error = "Do Not let it empty"
                }
            }
        }
    }
    private var itIsShown = false
    private fun setSignUp(name: String, email: String, password: String){
        signupViewModel.signUp(name, email, password).observe(this@SignupActivity){ result ->
            when(result){
                is Result.Error -> {
                    if(!itIsShown) {
                        binding.progressBar.visibility = View.GONE
                        AlertDialog.Builder(this@SignupActivity).apply {
                            setTitle("Signup Error")
                            setMessage(result.error.toString())
                            setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
                            create()
                            show()
                        }
                        itIsShown = true
                    }
                }
                is Result.Success -> {
                    if(!itIsShown){
                        binding.progressBar.visibility = View.GONE
                        Log.d("success", "login Success")
                        AlertDialog.Builder(this@SignupActivity).apply {
                            setTitle("Sign Up Berhasil")
                            setMessage("${result.data}")
                            setPositiveButton("Lanjut"){_,_ ->
                                finish()
                            }
                            create()
                            show()
                        }
                        itIsShown = true
                    }
                }
                is Result.Loading -> {
                    Log.d("Loading","Result still loading")
                    binding.progressBar.visibility = View.VISIBLE
                }

            }
            if (result is Result.Error || result is Result.Success) {
                itIsShown = false
            }

        }
    }
}