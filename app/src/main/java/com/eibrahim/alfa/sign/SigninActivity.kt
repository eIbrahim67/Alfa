package com.eibrahim.alfa.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.eibrahim.alfa.mainActivity.MainActivity
import com.eibrahim.alfa.R
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signinBtn : Button
    private lateinit var emailTxt : EditText
    private lateinit var passwordTxt : EditText
    private lateinit var signupBtn : TextView

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        val intent = Intent(this, MainActivity::class.java)
        if(auth.currentUser != null){
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        signinBtn = findViewById(R.id.btn_signin)
        emailTxt = findViewById(R.id.email_signin)
        passwordTxt = findViewById(R.id.pass_signin)
        signupBtn = findViewById(R.id.btn_signup2)

        auth = FirebaseAuth.getInstance()

        val intent = Intent(this, SignupActivity::class.java)
        signupBtn.setOnClickListener {

            startActivity(intent)
        }

        signinBtn.setOnClickListener {
            val email = emailTxt.text.toString()
            val password = passwordTxt.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Check from your input", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Check from your input", Toast.LENGTH_SHORT).show()
                }
            }
    }

}