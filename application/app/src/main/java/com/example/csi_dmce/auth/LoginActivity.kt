package com.example.csi_dmce.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.csi_dmce.MainActivity
import com.example.csi_dmce.R
import com.example.csi_dmce.database.StudentAuthWrapper
import com.example.csi_dmce.utils.Helpers
import kotlinx.coroutines.runBlocking


class LoginActivity: AppCompatActivity() {
    private lateinit var etLoginEmail     : EditText
    private lateinit var etLoginPassword  : EditText
    private lateinit var tvForgotPassword : TextView
    private lateinit var btnLogin          : Button
    private lateinit var tvNoAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etLoginEmail      = findViewById(R.id.event_name)
        etLoginPassword   = findViewById(R.id.edit_text_login_password)
        etLoginEmail.setText("amit@gmail.com")
        etLoginPassword.setText("root")

        btnLogin = findViewById(R.id.button_login)
        btnLogin.setOnClickListener {
            val email: String = etLoginEmail.text.toString()
            val passwordHash: String = Helpers.getSha256Hash(etLoginPassword.text.toString())
            runBlocking {
                StudentAuthWrapper.checkStudentCredentials(email, passwordHash) {
                    it?.let {
                        runBlocking {
                            Log.d("DB_AUTH", "YEAH RUNNING")
                            CsiAuthWrapper.setAuthToken(student = it, context = applicationContext)

                            val sharedPref = getSharedPreferences(
                                "csi_shared_prefs", Context.MODE_PRIVATE)
                            if (sharedPref.getBoolean("firstTime", true)) {
                                sharedPref.edit().putBoolean("firstTime", false).apply()
                            }
                            
                            val mIntent = Intent(applicationContext, MainActivity::class.java)
                            finishAffinity()
                            startActivity(mIntent)
                        }
                    } ?: run {
                        Toast.makeText(applicationContext, "Invalid credentials!", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

        tvForgotPassword = findViewById(R.id.text_view_forgot_password)
        tvForgotPassword.setOnClickListener{
            val intent = Intent(applicationContext, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        tvNoAccount = findViewById(R.id.text_view_new_account)
        tvNoAccount.setOnClickListener{
            val intent = Intent(applicationContext, RegistrationActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}

