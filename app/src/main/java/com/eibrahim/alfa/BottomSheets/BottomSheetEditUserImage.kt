package com.eibrahim.alfa.BottomSheets


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.eibrahim.alfa.DeclaredClasses.DeclareDataUsers
import com.eibrahim.alfa.FragmentsShowrActivity.FragmentsViewerActivity
import com.eibrahim.alfa.R
import com.eibrahim.alfa.FragmentsShowrActivity.ShowedImageUrl
import com.eibrahim.alfa.MainActivity.Uri
import com.eibrahim.alfa.DataClasses.UserAdminData
import com.eibrahim.alfa.FragmentsShowrActivity.no_page
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.FragmentActivity
import com.eibrahim.alfa.Dialogs.DialogEditUserImage

class BottomSheetEditUserImage(private var requireActivity: FragmentActivity? = null)
    : BottomSheetDialogFragment() {

        private lateinit var uid : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.bottom_sheet_edit_user_image, container, false)

        val changeImage : LinearLayout = root.findViewById(R.id.changeImage)
        val deleteImage : LinearLayout = root.findViewById(R.id.deleteImage)
        val showImage : LinearLayout = root.findViewById(R.id.showImage)

        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val intent = Intent(context, FragmentsViewerActivity::class.java)
        val declareDataUsers = DeclareDataUsers()

        declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
            override fun onDataDeclared(userAdminData: UserAdminData?) {

                if (userAdminData != null) {

                    showImage.setOnClickListener {

                        no_page = 6
                        ShowedImageUrl = userAdminData.imageUrl.toString()
                        startActivity(intent)
                        dismiss()
                    }

                    deleteImage.setOnClickListener {

                        deleteImageUrlInFirestore(userAdminData.gender as String)

                    }

                }else{

                }

            }
        }, uid)

        changeImage.setOnClickListener {

            imagePicker.launch(arrayOf("image/*"))

        }

        return root
    }


    private fun deleteImageUrlInFirestore(gender : String) {
        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("Users").document(uid)
        val updates = hashMapOf<String, Any>()

        val imageAccUrlTemp =
            if (gender == "male")
                "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Fmen.png?alt=media&token=9bcf379f-8a9e-45bf-b7c6-661d7cae18a3"
            else
                "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Fwomen.png?alt=media&token=c5bf2aa8-c2d6-4f2c-94f0-7c8ad1e34c3e"

        updates["imageUrl"] = imageAccUrlTemp

        usersCollection.update(updates)
            .addOnSuccessListener {

                Toast.makeText(requireContext(), "updated Successfully.", Toast.LENGTH_SHORT).show()

                dismiss()
            }
            .addOnFailureListener {

                Toast.makeText(requireContext(), "updated Failed!", Toast.LENGTH_SHORT).show()
                dismiss()
            }
    }

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
        ActivityResultCallback { uri ->
            if (uri != null) {
                Uri = uri
                dismiss()
                val dialogEditUserImage = DialogEditUserImage()
                dialogEditUserImage.show(requireActivity!!.supportFragmentManager, "")
            }
        }
    )

}