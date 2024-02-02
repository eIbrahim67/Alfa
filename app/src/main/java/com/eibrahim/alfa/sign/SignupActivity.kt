package com.eibrahim.alfa.sign

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eibrahim.alfa.R
import com.eibrahim.alfa.dataClasses.UserAdminData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class SignupActivity : AppCompatActivity() {

    private lateinit var signupBtn : Button
    private lateinit var fnameTxt : EditText
    private lateinit var usernameSignup : EditText
    private lateinit var emailTxt : EditText
    private lateinit var passwordTxt : EditText
    private lateinit var rePasswordTxt : EditText
    private lateinit var signinBtn : TextView
    private lateinit var genderMale : Button
    private lateinit var genderFemale : Button

    private lateinit var gender : String
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signupBtn = findViewById(R.id.btn_signup)
        fnameTxt = findViewById(R.id.name_signup)
        usernameSignup = findViewById(R.id.username_signup)
        emailTxt = findViewById(R.id.email_signup)
        passwordTxt = findViewById(R.id.pass_signup)
        rePasswordTxt = findViewById(R.id.repass_signup)
        signinBtn = findViewById(R.id.btn_signin2)
        genderMale = findViewById(R.id.gender_male)
        genderFemale = findViewById(R.id.gender_female)

        auth = FirebaseAuth.getInstance()

        signinBtn.setOnClickListener {
            finish()
        }

        genderMale.setOnClickListener {
            gender = "male"
        }

        genderFemale.setOnClickListener {
            gender = "female"
        }

        signupBtn.setOnClickListener {
            val email = emailTxt.text.toString().trim()
            val password = passwordTxt.text.toString().trim()

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
                emailTxt.setText(e.toString())
                Toast.makeText(
                    this, "Authentication failed: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
    private fun updateData(){

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser!!.uid

        val firestore = FirebaseFirestore.getInstance()
        val DocumentRef: DocumentReference = firestore.collection("Users").document(userId)
        val imageUrlTemp = "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Ffetrah.jpg?alt=media&token=cc1e74f2-233d-40d0-a3bf-a0d41b8efe51"

        val imageAccUrlTemp : String = if(gender == "male")
            "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Fmen.png?alt=media&token=9bcf379f-8a9e-45bf-b7c6-661d7cae18a3"
        else
            "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Fwomen.png?alt=media&token=c5bf2aa8-c2d6-4f2c-94f0-7c8ad1e34c3e"

        val userData = UserAdminData(

            0, 0, gender,
            imageAccUrlTemp,
            imageUrlTemp,
            fnameTxt.text.toString(), 0, 0, null,
            usernameSignup.text.toString(), null, null, emptyList(), emptyList()

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