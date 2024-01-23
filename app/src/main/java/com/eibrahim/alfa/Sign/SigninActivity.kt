package com.eibrahim.alfa.Sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.eibrahim.alfa.MainActivity.MainActivity
import com.eibrahim.alfa.R
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signin_btn : Button
    private lateinit var email_txt : EditText
    private lateinit var password_txt : EditText
    private lateinit var signup_btn : TextView

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        var intent = Intent(this, MainActivity::class.java)
        if(auth.currentUser != null){
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        signin_btn = findViewById(R.id.btn_signin)
        email_txt = findViewById(R.id.email_signin)
        password_txt = findViewById(R.id.pass_signin)
        signup_btn = findViewById(R.id.btn_signup2)

        auth = FirebaseAuth.getInstance()

        val intent = Intent(this, SignupActivity::class.java)
        signup_btn.setOnClickListener {

            startActivity(intent)
        }

        signin_btn.setOnClickListener {
            val email = email_txt.text.toString()
            val password = password_txt.text.toString()

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
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Check from your input", Toast.LENGTH_SHORT).show()
                }
            }
    }

}