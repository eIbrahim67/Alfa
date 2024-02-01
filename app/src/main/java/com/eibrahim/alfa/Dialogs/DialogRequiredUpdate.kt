package com.eibrahim.alfa.Dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.eibrahim.alfa.MainActivity.MainActivity
import com.eibrahim.alfa.R
import com.eibrahim.alfa.MainActivity.Uri
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DialogRequiredUpdate(

    private val mainActivity : MainActivity? = null

) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.dialog_required_update, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setElevation(20F)

        dialog?.window?.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT)


        val btn_discard_update_app : TextView = root.findViewById(R.id.btn_discard_update_app)
        val btn_update_app : TextView = root.findViewById(R.id.btn_update_app)

        val image_update : ImageView = root.findViewById(R.id.image_update)


        val rotateAnimation = RotateAnimation(
            0f,  // From degrees
            360f,  // To degrees
            Animation.RELATIVE_TO_SELF,  // Pivot point X
            0.5f,  // Pivot X coordinate (0.0 to 1.0)
            Animation.RELATIVE_TO_SELF,  // Pivot point Y
            0.5f   // Pivot Y coordinate (0.0 to 1.0)
        )

        // Set animation properties
        rotateAnimation.duration = 5000 // Animation duration in milliseconds
        rotateAnimation.repeatCount = Animation.INFINITE // Repeat the animation infinitely
        rotateAnimation.interpolator = LinearInterpolator() // Optional: Add an interpolator for smoother animation

        // Start the animation
        image_update.startAnimation(rotateAnimation)

        dialog?.setCancelable(false)

        btn_update_app.setOnClickListener {

            dismiss()

        }

        btn_discard_update_app.setOnClickListener {
            mainActivity?.finish()
        }

        return root
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val fStorage = FirebaseStorage.getInstance()
        val storageReference = fStorage.reference

        val imageName = "${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child("images/$imageName")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateImageUrlInFirestore(downloadUrl.toString())
                }
            }
            .addOnFailureListener {  }
    }

    private fun updateImageUrlInFirestore(newImageUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("Users").document(uid)
        val updates = hashMapOf<String, Any>()
        updates["imageUrl"] = newImageUrl

        usersCollection.update(updates)
            .addOnSuccessListener {

                Toast.makeText(requireContext(), "updated Successfully.", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener {

                Toast.makeText(requireContext(), "updated Failed!", Toast.LENGTH_SHORT).show()

            }
    }



}