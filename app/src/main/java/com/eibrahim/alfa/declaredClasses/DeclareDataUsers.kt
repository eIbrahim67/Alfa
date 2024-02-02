package com.eibrahim.alfa.declaredClasses

import com.eibrahim.alfa.dataClasses.UserAdminData
import com.google.firebase.firestore.FirebaseFirestore

class DeclareDataUsers {
    private var onDataDeclaredListener: OnDataDeclaredListener? = null
    private var userAdminData : UserAdminData? = null

    interface OnDataDeclaredListener {
        fun onDataDeclared(userAdminData: UserAdminData?)
    }

    fun declareData(listener: OnDataDeclaredListener, uid : String) {
        onDataDeclaredListener = listener

        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("Users")
        usersCollection.document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data
                    userAdminData = if (data != null) {
                        UserAdminData(
                            name = data["name"] as? String,
                            userName = data["userName"] as? String,
                            followers = data["followers"] as? Long,
                            following = data["following"] as? Long,
                            imageUrl = data["imageUrl"] as? String,
                            imageBackUrl = data["imageBackUrl"] as? String,
                            noPosts = data["noPosts"] as? Long,
                            specialist = data["specialist"] as? String,
                            webSite = data["webSite"] as? String,
                            gender = data["gender"] as? String,
                            whatsApp = data["whatsApp"] as? String,
                            points = data["points"] as? Long,
                            postsId = data["postsId"] as? List<String>,
                            postsBookmarks = data["postsBookmarks"] as? List<String>
                        )
                    } else {
                        UserAdminData()
                    }

                    onDataDeclaredListener?.onDataDeclared(userAdminData)
                }
            }
    }
}

/*

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

*/
