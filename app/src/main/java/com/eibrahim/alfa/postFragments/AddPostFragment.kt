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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eibrahim.alfa.R
import com.eibrahim.alfa.dataClasses.UserId
import com.eibrahim.alfa.adapterClasses.AdapterRecyclerviewTags
import com.eibrahim.alfa.dataClasses.DataPosts
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

var  newPostAdded : Boolean = false

class AddPostFragment : Fragment() {

    private lateinit var recyclerviewTags: RecyclerView
    private lateinit var uploadPhoto: RelativeLayout
    private lateinit var listOfPics: ArrayList<Uri>
    private lateinit var uploadBtn: TextView
    private lateinit var postDetails: EditText
    private lateinit var uri: Uri

    private lateinit var imageAddPostCollections: RelativeLayout
    private lateinit var imageAddPostClear: ImageView

    private lateinit var imageAddPostOne: LinearLayout
    private lateinit var imageAddPostOneV1: ShapeableImageView

    private lateinit var imageAddPostTwo: LinearLayout
    private lateinit var imageAddPostTwoV1: ShapeableImageView
    private lateinit var imageAddPostTwoV2: ShapeableImageView

    private lateinit var imageAddPostThree: LinearLayout
    private lateinit var imageAddPostThreeV1: ShapeableImageView
    private lateinit var imageAddPostThreeV2: ShapeableImageView
    private lateinit var imageAddPostThreeV3: ShapeableImageView

    private lateinit var imageAddPostFour: LinearLayout
    private lateinit var imageAddPostFourV1: ShapeableImageView
    private lateinit var imageAddPostFourV2: ShapeableImageView
    private lateinit var imageAddPostFourV3: ShapeableImageView
    private lateinit var imageAddPostFourV4: ShapeableImageView
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

        imageAddPostTwo = Root.findViewById(R.id.image_add_post_two)
        imageAddPostTwoV1 = Root.findViewById(R.id.image_add_post_two_v1)
        imageAddPostTwoV2 = Root.findViewById(R.id.image_add_post_two_v2)

        imageAddPostThree = Root.findViewById(R.id.image_add_post_three)
        imageAddPostThreeV1 = Root.findViewById(R.id.image_add_post_three_v1)
        imageAddPostThreeV2 = Root.findViewById(R.id.image_add_post_three_v2)
        imageAddPostThreeV3 = Root.findViewById(R.id.image_add_post_three_v3)

        imageAddPostFour = Root.findViewById(R.id.image_add_post_four)
        imageAddPostFourV1 = Root.findViewById(R.id.image_add_post_four_v1)
        imageAddPostFourV2 = Root.findViewById(R.id.image_add_post_four_v2)
        imageAddPostFourV3 = Root.findViewById(R.id.image_add_post_four_v3)
        imageAddPostFourV4 = Root.findViewById(R.id.image_add_post_four_v4)

        uploadPhoto = Root.findViewById(R.id.upload_photo)
        postDetails = Root.findViewById(R.id.post_details)

        val loading_image : ImageView = Root.findViewById(R.id.loading_image)

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

        listOfPics = ArrayList()

        imageAddPostClear.setOnClickListener {

            listOfPics = ArrayList()
            imageAddPostCollections.visibility = View.GONE
            imageAddPostOne.visibility = View.GONE
            imageAddPostTwo.visibility = View.GONE
            imageAddPostThree.visibility = View.GONE
            imageAddPostFour.visibility = View.GONE

            uriCleared = 1
        }

        uploadBtn.setOnClickListener {

            if (
                (!::uri.isInitialized && uriCleared == 0)  && postDetails.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "You didn't add anything!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loading_image.visibility = View.VISIBLE

            Glide.with(this)
                .asGif()
                .load(R.drawable.infinity_loading_white)
                .into(loading_image)

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

            if (postDetails.text.toString().isNotEmpty() || postDetails.text.toString() != "") {
                dataPost.postText = postDetails.text.toString()
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

            documentRecUsers.update("noPosts", FieldValue.increment(1))
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
            listOfPics.add(uri)
            this.uri = uri
        }

        when (listOfPics.size) {
            1 -> {

                imageAddPostCollections.visibility = View.VISIBLE

                imageAddPostOne.visibility = View.VISIBLE
                imageAddPostTwo.visibility = View.GONE
                imageAddPostThree.visibility = View.GONE
                imageAddPostFour.visibility = View.GONE

                imageAddPostOneV1.setImageURI(listOfPics[0])

            }

            2 -> {

                imageAddPostOne.visibility = View.GONE
                imageAddPostTwo.visibility = View.VISIBLE
                imageAddPostThree.visibility = View.GONE
                imageAddPostFour.visibility = View.GONE

                imageAddPostTwoV1.setImageURI(listOfPics[0])
                imageAddPostTwoV2.setImageURI(listOfPics[1])

            }

            3 -> {

                imageAddPostOne.visibility = View.GONE
                imageAddPostTwo.visibility = View.GONE
                imageAddPostThree.visibility = View.VISIBLE
                imageAddPostFour.visibility = View.GONE

                imageAddPostThreeV1.setImageURI(listOfPics[0])
                imageAddPostThreeV2.setImageURI(listOfPics[1])
                imageAddPostThreeV3.setImageURI(listOfPics[2])

            }

            4 -> {

                imageAddPostOne.visibility = View.GONE
                imageAddPostTwo.visibility = View.GONE
                imageAddPostThree.visibility = View.GONE
                imageAddPostFour.visibility = View.VISIBLE

                imageAddPostFourV1.setImageURI(listOfPics[0])
                imageAddPostFourV2.setImageURI(listOfPics[1])
                imageAddPostFourV3.setImageURI(listOfPics[2])
                imageAddPostFourV4.setImageURI(listOfPics[3])


            }
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
