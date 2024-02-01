package com.eibrahim.alfa.PostFragments

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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eibrahim.alfa.R
import com.eibrahim.alfa.DataClasses.UserId
import com.eibrahim.alfa.AdapterClasses.AdapterRecyclerviewTags
import com.eibrahim.alfa.DataClasses.DataPosts
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

public var  newPostAdded : Boolean = false

class AddPostFragment : Fragment() {

    private lateinit var recyclerview_tags: RecyclerView
    private lateinit var upload_photo: RelativeLayout
    private lateinit var listOfPics: ArrayList<Uri>
    private lateinit var uploadBtn: TextView
    private lateinit var post_details: EditText
    private lateinit var uri: Uri

    private lateinit var image_add_post_collections: RelativeLayout
    private lateinit var image_add_post_clear: ImageView

    private lateinit var image_add_post_one: LinearLayout
    private lateinit var image_add_post_one_v1: ShapeableImageView

    private lateinit var image_add_post_two: LinearLayout
    private lateinit var image_add_post_two_v1: ShapeableImageView
    private lateinit var image_add_post_two_v2: ShapeableImageView

    private lateinit var image_add_post_three: LinearLayout
    private lateinit var image_add_post_three_v1: ShapeableImageView
    private lateinit var image_add_post_three_v2: ShapeableImageView
    private lateinit var image_add_post_three_v3: ShapeableImageView

    private lateinit var image_add_post_four: LinearLayout
    private lateinit var image_add_post_four_v1: ShapeableImageView
    private lateinit var image_add_post_four_v2: ShapeableImageView
    private lateinit var image_add_post_four_v3: ShapeableImageView
    private lateinit var image_add_post_four_v4: ShapeableImageView
    private var uriCleared = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val Root = inflater.inflate(R.layout.fragment_add_post, container, false)

        val add_new_tag: Button = Root.findViewById(R.id.add_new_tag)
        val add_tag_text: EditText = Root.findViewById(R.id.add_tag_text)

        uploadBtn = Root.findViewById(R.id.upload_btn)

        image_add_post_collections = Root.findViewById(R.id.image_add_post_collections)
        image_add_post_clear = Root.findViewById(R.id.image_add_post_clear)

        image_add_post_one = Root.findViewById(R.id.image_add_post_one)
        image_add_post_one_v1 = Root.findViewById(R.id.image_add_post_one_v1)

        image_add_post_two = Root.findViewById(R.id.image_add_post_two)
        image_add_post_two_v1 = Root.findViewById(R.id.image_add_post_two_v1)
        image_add_post_two_v2 = Root.findViewById(R.id.image_add_post_two_v2)

        image_add_post_three = Root.findViewById(R.id.image_add_post_three)
        image_add_post_three_v1 = Root.findViewById(R.id.image_add_post_three_v1)
        image_add_post_three_v2 = Root.findViewById(R.id.image_add_post_three_v2)
        image_add_post_three_v3 = Root.findViewById(R.id.image_add_post_three_v3)

        image_add_post_four = Root.findViewById(R.id.image_add_post_four)
        image_add_post_four_v1 = Root.findViewById(R.id.image_add_post_four_v1)
        image_add_post_four_v2 = Root.findViewById(R.id.image_add_post_four_v2)
        image_add_post_four_v3 = Root.findViewById(R.id.image_add_post_four_v3)
        image_add_post_four_v4 = Root.findViewById(R.id.image_add_post_four_v4)

        upload_photo = Root.findViewById(R.id.upload_photo)
        post_details = Root.findViewById(R.id.post_details)

        val loading_image : ImageView = Root.findViewById(R.id.loading_image)

        var listOfData = ArrayList<String>()

        recyclerview_tags = Root.findViewById(R.id.recyclerview_tags)
        val adapterRvTags = AdapterRecyclerviewTags(
            listOfData, requireContext()
        )
        recyclerview_tags.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        add_new_tag.setOnClickListener {

            if (listOfData.size != 3 && !add_tag_text.text.toString().isEmpty()) {
                listOfData.add("#" + add_tag_text.text.toString().trim())
                recyclerview_tags.adapter = adapterRvTags
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

        listOfPics = ArrayList<Uri>()

        image_add_post_clear.setOnClickListener {

            listOfPics = ArrayList<Uri>()
            image_add_post_collections.visibility = View.GONE
            image_add_post_one.visibility = View.GONE
            image_add_post_two.visibility = View.GONE
            image_add_post_three.visibility = View.GONE
            image_add_post_four.visibility = View.GONE

            uriCleared = 1
        }

        uploadBtn.setOnClickListener {

            if (
                (!::uri.isInitialized && uriCleared == 0)  && post_details.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "You didn't add anything!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loading_image.visibility = View.VISIBLE

            Glide.with(this)
                .asGif()
                .load(R.drawable.infinity_loading_white)
                .into(loading_image)

            var auth = FirebaseAuth.getInstance()
            var uid = auth.currentUser?.uid.toString()
            var firestore = FirebaseFirestore.getInstance()
            val documentRecPosts = firestore.collection("posts").document()
            val documentRecUsers = firestore.collection("Users").document(uid)
            val documentRecPostsLikes = firestore.collection("postsLikes").document(documentRecPosts.id.toString())

            var likesMap = HashMap<String, Any>()
            likesMap.put("likes", ArrayList<String>())

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

            if (post_details.text.toString().isNotEmpty() || post_details.text.toString() != "") {
                dataPost.postText = post_details.text.toString()
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

        upload_photo.setOnClickListener {

            imagePicker.launch(arrayOf("image/*"))

        }

        return Root
    }

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
        ActivityResultCallback { uri ->

            if (uri != null) {
                listOfPics.add(uri)
                this.uri = uri
            }

            if (listOfPics.size == 1) {

                image_add_post_collections.visibility = View.VISIBLE

                image_add_post_one.visibility = View.VISIBLE
                image_add_post_two.visibility = View.GONE
                image_add_post_three.visibility = View.GONE
                image_add_post_four.visibility = View.GONE

                image_add_post_one_v1.setImageURI(listOfPics[0])

            }
            else if (listOfPics.size == 2) {

                image_add_post_one.visibility = View.GONE
                image_add_post_two.visibility = View.VISIBLE
                image_add_post_three.visibility = View.GONE
                image_add_post_four.visibility = View.GONE

                image_add_post_two_v1.setImageURI(listOfPics[0])
                image_add_post_two_v2.setImageURI(listOfPics[1])

            }
            else if (listOfPics.size == 3) {

                image_add_post_one.visibility = View.GONE
                image_add_post_two.visibility = View.GONE
                image_add_post_three.visibility = View.VISIBLE
                image_add_post_four.visibility = View.GONE

                image_add_post_three_v1.setImageURI(listOfPics[0])
                image_add_post_three_v2.setImageURI(listOfPics[1])
                image_add_post_three_v3.setImageURI(listOfPics[2])

            }
            else if (listOfPics.size == 4) {

                image_add_post_one.visibility = View.GONE
                image_add_post_two.visibility = View.GONE
                image_add_post_three.visibility = View.GONE
                image_add_post_four.visibility = View.VISIBLE

                image_add_post_four_v1.setImageURI(listOfPics[0])
                image_add_post_four_v2.setImageURI(listOfPics[1])
                image_add_post_four_v3.setImageURI(listOfPics[2])
                image_add_post_four_v4.setImageURI(listOfPics[3])


            }

        }
    )

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
