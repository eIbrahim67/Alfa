package com.eibrahim.alfa.postFragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.alfa.R
import com.eibrahim.alfa.adapterClasses.AdapterRecyclerviewTags
import com.eibrahim.alfa.dataClasses.DataPosts
import com.eibrahim.alfa.dataClasses.UserId
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

var  newPostAdded : Boolean = false

class AddPostFragment : Fragment() {

    private lateinit var recyclerviewTags: RecyclerView
    private lateinit var uploadPhoto: RelativeLayout
    private lateinit var uploadBtn: TextView
    private lateinit var postDetails: EditText
    private lateinit var postTitle: EditText
    private lateinit var uri: Uri

    private lateinit var imageAddPostCollections: RelativeLayout
    private lateinit var imageAddPostClear: ImageView

    private lateinit var imageAddPostOne: LinearLayout
    private lateinit var imageAddPostOneV1: ShapeableImageView

    private var uriCleared = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val Root = inflater.inflate(R.layout.fragment_add_post, container, false)

        val add_new_tag: Button = Root.findViewById(R.id.add_new_tag)
        val add_tag_text: EditText = Root.findViewById(R.id.add_tag_text)

        uploadBtn = Root.findViewById(R.id.upload_btn)

        imageAddPostCollections = Root.findViewById(R.id.image_add_post_collections)
        imageAddPostClear = Root.findViewById(R.id.image_add_post_clear)

        imageAddPostOne = Root.findViewById(R.id.image_add_post_one)
        imageAddPostOneV1 = Root.findViewById(R.id.image_add_post_one_v1)

        uploadPhoto = Root.findViewById(R.id.upload_photo)
        postDetails = Root.findViewById(R.id.post_details)
        postTitle = Root.findViewById(R.id.post_title)

        val progressBar : ProgressBar = Root.findViewById(R.id.progressBar)

        val listOfData = ArrayList<String>()

        recyclerviewTags = Root.findViewById(R.id.recyclerview_tags)
        val adapterRvTags = AdapterRecyclerviewTags(
            listOfData, requireContext()
        )
        recyclerviewTags.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        add_new_tag.setOnClickListener {

            if (listOfData.size != 3 && add_tag_text.text.toString().isNotEmpty()) {
                listOfData.add("#" + add_tag_text.text.toString().trim())
                recyclerviewTags.adapter = adapterRvTags
                add_tag_text.setText("")
            } else {
                if (listOfData.size == 3)
                    Toast.makeText(
                        requireContext(),
                        "sorry, you can add only 3 tags",
                        Toast.LENGTH_SHORT
                    ).show()
                else
                    Toast.makeText(requireContext(), "add any tag", Toast.LENGTH_SHORT).show()
            }
        }

        imageAddPostClear.setOnClickListener {

            imageAddPostCollections.visibility = View.GONE
            imageAddPostOne.visibility = View.GONE

            uriCleared = 1
        }

        uploadBtn.setOnClickListener {

            if (
                (!::uri.isInitialized && uriCleared == 0)  && postTitle.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "You didn't add anything!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE


            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid.toString()
            val firestore = FirebaseFirestore.getInstance()
            val documentRecPosts = firestore.collection("posts").document()
            val documentRecUsers = firestore.collection("Users").document(uid)
            val documentRecPostsLikes = firestore.collection("postsLikes").document(documentRecPosts.id)

            val likesMap = HashMap<String, Any>()
            likesMap["likes"] = ArrayList<String>()

            documentRecPostsLikes.set(likesMap)
                .addOnSuccessListener { Log.d("Firestore", "Document added with ID: ") }
                .addOnFailureListener { e ->
                    Log.w(
                        "Firestore",
                        "Error adding document",
                        e
                    )
                }

            val dataPost = DataPosts()

            if (postTitle.text.toString().isNotEmpty() || postTitle.text.toString() != "") {
                dataPost.postText = postTitle.text.toString()
            } else {
                dataPost.postText = null
            }

            dataPost.time = System.currentTimeMillis()

            dataPost.userId= UserId(id = uid)

            dataPost.postId = documentRecPosts.id

            dataPost.imageUrl = null

            documentRecUsers.update("postsId", FieldValue.arrayUnion(dataPost.postId))
                .addOnSuccessListener {
                }
                .addOnFailureListener { e ->
                }

            documentRecPosts.set(dataPost)
                .addOnSuccessListener {

                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "upload texts failed", Toast.LENGTH_SHORT).show()
                }

            if (::uri.isInitialized && uriCleared == 0) {

                uploadImageToFirebase(uri, dataPost.postId.toString())

            }else {

                newPostAdded = true
                requireActivity().finish()

            }

        }

        uploadPhoto.setOnClickListener {

            imagePicker.launch(arrayOf("image/*"))


        }

        return Root
    }

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->

        if (uri != null) {
            this.uri = uri

            uriCleared = 0

            imageAddPostCollections.visibility = View.VISIBLE

            imageAddPostOne.visibility = View.VISIBLE

            imageAddPostOneV1.setImageURI(uri)

        }

    }

    private fun uploadImageToFirebase(uri: Uri, docId: String) {
        val fStorage = FirebaseStorage.getInstance()
        val storageReference = fStorage.reference

        val imageName = "${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child("images/$imageName")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                    updateImageUrlInFirestore(downloadUrl.toString(), docId)

                }
            }
            .addOnFailureListener { exception -> }
    }

    private fun updateImageUrlInFirestore(newImageUrl: String, docId : String) {
        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("posts").document(docId)
        val updates = hashMapOf<String, Any>()
        updates["imageUrl"] = newImageUrl

        usersCollection.update(updates)
            .addOnSuccessListener {

                Toast.makeText(requireContext(), "post uploaded Successfully.", Toast.LENGTH_SHORT).show()
                newPostAdded = true
                requireActivity().finish()

            }
            .addOnFailureListener { e ->

                Toast.makeText(requireContext(), "post uploaded Failed!", Toast.LENGTH_SHORT).show()
                newPostAdded = true
                requireActivity().finish()
            }
    }


}
