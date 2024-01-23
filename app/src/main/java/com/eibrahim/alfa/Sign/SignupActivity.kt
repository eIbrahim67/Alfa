package com.eibrahim.alfa.Sign

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eibrahim.alfa.R
import com.eibrahim.alfa.DataClasses.UserAdminData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class SignupActivity : AppCompatActivity() {

    private lateinit var signup_btn : Button
    private lateinit var fname_txt : EditText
    private lateinit var username_signup : EditText
    private lateinit var email_txt : EditText
    private lateinit var password_txt : EditText
    private lateinit var rePassword_txt : EditText
    private lateinit var signin_btn : TextView
    private lateinit var gender_male : Button
    private lateinit var gender_female : Button

    private lateinit var gender : String
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signup_btn = findViewById(R.id.btn_signup)
        fname_txt = findViewById(R.id.name_signup)
        username_signup = findViewById(R.id.username_signup)
        email_txt = findViewById(R.id.email_signup)
        password_txt = findViewById(R.id.pass_signup)
        rePassword_txt = findViewById(R.id.repass_signup)
        signin_btn = findViewById(R.id.btn_signin2)
        gender_male = findViewById(R.id.gender_male)
        gender_female = findViewById(R.id.gender_female)

        auth = FirebaseAuth.getInstance()

        signin_btn.setOnClickListener {
            finish()
        }

        gender_male.setOnClickListener {
            gender = "male"
        }

        gender_female.setOnClickListener {
            gender = "female"
        }

        signup_btn.setOnClickListener {
            val email = email_txt.text.toString().trim()
            val password = password_txt.text.toString().trim()

            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            updateData()

                            Toast.makeText(
                                this, "Authentication Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this, "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }catch (e : Exception){
                email_txt.setText(e.toString())
                Toast.makeText(
                    this, "Authentication failed: " + e,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
    private fun updateData(){

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid.toString()

        val firestore = FirebaseFirestore.getInstance()
        val DocumentRef: DocumentReference = firestore.collection("Users").document(userId)
        val imageUrlTemp = "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Ffetrah.jpg?alt=media&token=cc1e74f2-233d-40d0-a3bf-a0d41b8efe51"
        val imageAccUrlTemp : String

        if (gender == "male")
            imageAccUrlTemp = "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Fmen.png?alt=media&token=9bcf379f-8a9e-45bf-b7c6-661d7cae18a3"
        else
            imageAccUrlTemp = "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Fwomen.png?alt=media&token=c5bf2aa8-c2d6-4f2c-94f0-7c8ad1e34c3e"

        val userData = UserAdminData(

            0, 0, gender,
            imageAccUrlTemp,
            imageUrlTemp,
            fname_txt.text.toString(), 0, 0, null,
            username_signup.text.toString(), null, null, emptyList(), emptyList()

        )

        DocumentRef.set(userData)
            .addOnSuccessListener { Log.d("Firestore", "Document added with ID: ") }
            .addOnFailureListener { e ->
                Log.w(
                    "Firestore",
                    "Error adding document",
                    e
                )
            }

    }
}