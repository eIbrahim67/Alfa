package com.eibrahim.alfa.FragmentsShowrActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.eibrahim.alfa.DeclaredClasses.DeclareDataUsers
import com.eibrahim.alfa.R
import com.eibrahim.alfa.DataClasses.UserAdminData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class EditPersonalDataFragment : Fragment() {

    private lateinit var nameUserEdit: EditText
    private lateinit var fullNameEdit: EditText
    private lateinit var specialistEdit: EditText
    private lateinit var websiteEdit: EditText
    private lateinit var whatsappEdit: EditText
    private lateinit var backBtn: ImageView
    private lateinit var saveButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var fStorage : FirebaseStorage
    private lateinit var userAdminData: UserAdminData
    private lateinit var uid : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val Root = inflater.inflate(R.layout.fragment_edit_personal_data, container, false)
        try {

        nameUserEdit = Root.findViewById(R.id.name_user_edit)
        fullNameEdit = Root.findViewById(R.id.full_name_edit)
        specialistEdit = Root.findViewById(R.id.specialist_edit)
        websiteEdit = Root.findViewById(R.id.website_edit)
        whatsappEdit = Root.findViewById(R.id.whatsapp_edit)
        saveButton = Root.findViewById(R.id.save_btn)
        backBtn = Root.findViewById(R.id.back_personal_btn)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        firestore = FirebaseFirestore.getInstance()
        fStorage = FirebaseStorage.getInstance()
        val usersCollection = firestore.collection("Users").document(uid)

        saveButton.setOnClickListener {

            val updates = hashMapOf<String, Any>()

            if (nameUserEdit.text.toString().isNotEmpty()) {
                updates["userName"] = nameUserEdit.text.toString()
            }
            if (fullNameEdit.text.toString().isNotEmpty()) {
                updates["name"] = fullNameEdit.text.toString()
            }
            if (specialistEdit.text.toString().isNotEmpty()) {
                updates["specialist"] = specialistEdit.text.toString()
            }
            if (websiteEdit.text.toString().isNotEmpty()) {
                updates["webSite"] = websiteEdit.text.toString()
            }
            if (whatsappEdit.text.toString().isNotEmpty()) {
                updates["whatsApp"] = whatsappEdit.text.toString()
            }


            usersCollection.update(updates)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Update successful", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Update failed", Toast.LENGTH_LONG).show()
                }

            requireActivity().finish()
        }

        backBtn.setOnClickListener {

            requireActivity().finish()

        }

        if(nameUserEdit.hint == "null"){
            val declareDataUsers = DeclareDataUsers()
            declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
                override fun onDataDeclared(userAdminData: UserAdminData?) {
                    // Now you can use userAdminData here
                    if (userAdminData != null) {
                        nameUserEdit.hint = userAdminData.userName.toString()
                        fullNameEdit.hint = userAdminData.name.toString()
                        specialistEdit.hint = userAdminData.specialist.toString()
                        websiteEdit.hint = userAdminData.webSite.toString()
                        whatsappEdit.hint = userAdminData.whatsApp.toString()
                    }else{

                    }

                }
            }, uid)

        }

    }catch (e : java.lang.Exception){
        Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT)
    }
        return Root
    }



}