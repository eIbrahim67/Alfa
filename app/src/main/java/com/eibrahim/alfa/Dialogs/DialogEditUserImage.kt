package com.eibrahim.alfa.Dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.eibrahim.alfa.R
import com.eibrahim.alfa.MainActivity.Uri
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DialogEditUserImage : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.dialog_edit_user_image, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setElevation(20F)

        val show_update_image_user : ShapeableImageView = root.findViewById(R.id.show_update_image_user)
        val btn_update_image_user : TextView = root.findViewById(R.id.btn_update_image_user)
        val btn_discard_update_image_user : TextView = root.findViewById(R.id.btn_discard_update_image_user)
        val loading_update_image : ImageView = root.findViewById(R.id.loading_update_image)


        show_update_image_user.setImageURI(Uri)

        btn_update_image_user.setOnClickListener {

            loading_update_image.visibility = View.VISIBLE

            Glide.with(this)
                .asGif()
                .load(R.drawable.infinity_loading_black)
                .into(loading_update_image)

            uploadImageToFirebase(Uri)

        }

        btn_discard_update_image_user.setOnClickListener {
            dismiss()
        }

        return root
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val fStorage = FirebaseStorage.getInstance()
        val storageReference = fStorage.reference

        val imageName = "${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child("images/$imageName")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateImageUrlInFirestore(downloadUrl.toString())
                }
            }
            .addOnFailureListener { exception -> }
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