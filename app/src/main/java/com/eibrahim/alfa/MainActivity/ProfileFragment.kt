package com.eibrahim.alfa.MainActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eibrahim.alfa.AdapterRecycleViewPosts
import com.eibrahim.alfa.BottomSheets.BottomSheetEditUserImage
import com.eibrahim.alfa.BottomSheets.BottomSheetSettingsUser
import com.eibrahim.alfa.DataClasses.DataPosts
import com.eibrahim.alfa.DataClasses.ReadDataPosts
import com.eibrahim.alfa.DeclaredClasses.DeclareDataUsers
import com.eibrahim.alfa.DeclaredClasses.FormatNumber
import com.eibrahim.alfa.FragmentsShowrActivity.FragmentsViewerActivity
import com.eibrahim.alfa.PostFragments.newPostAdded
import com.eibrahim.alfa.R
import com.eibrahim.alfa.DataClasses.UserAdminData
import com.eibrahim.alfa.FragmentsShowrActivity.no_page
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso

public lateinit var Uri : Uri

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var fullName: TextView
    private lateinit var userName: TextView
    private lateinit var noPosts: TextView
    private lateinit var noFollowers: TextView
    private lateinit var noFollowing: TextView
    private lateinit var imgAccount: ImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var rv: RecyclerView
    private lateinit var List_rv : ArrayList<ReadDataPosts>
    private lateinit var adapter : AdapterRecycleViewPosts
    private lateinit var uid : String
    private lateinit var arrPosts : List<String>
    private lateinit var addPost_btn : Button
    private lateinit var edit_image_user : RelativeLayout
    private lateinit var btn_settings : RelativeLayout

    override fun onResume() {
        super.onResume()

        if (newPostAdded){

            EventChangeLister()
            newPostAdded = false
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val Root = inflater.inflate(R.layout.fragment_profile, container, false)

        val intent = Intent(activity, FragmentsViewerActivity::class.java)

        fullName = Root.findViewById(R.id.fullName)
        userName = Root.findViewById(R.id.userName)
        noPosts = Root.findViewById(R.id.noPosts)
        noFollowers = Root.findViewById(R.id.noFollowers)
        noFollowing = Root.findViewById(R.id.noFollowing)
        imgAccount = Root.findViewById(R.id.user_img_profile)
        swipeRefreshLayout = Root.findViewById(R.id.fragment_profile)
        rv = Root.findViewById(R.id.recyclerview_posts_profile)
        addPost_btn = Root.findViewById(R.id.add_post_btn)
        edit_image_user = Root.findViewById(R.id.edit_image_user)
        btn_settings = Root.findViewById(R.id.btn_settings)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        uid = auth.currentUser?.uid.toString()


        addPost_btn.setOnClickListener {

            startActivity(intent)
            no_page = 7
        }

        declareData()
        EventChangeLister()

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.setHasFixedSize(true)

        swipeRefreshLayout.setOnRefreshListener {
            declareData()
            EventChangeLister()
            swipeRefreshLayout.isRefreshing = false
        }

        btn_settings.setOnClickListener {

            val bottomSheetSettingsUser = BottomSheetSettingsUser(requireActivity())
            bottomSheetSettingsUser.show(requireActivity().supportFragmentManager, "Settings")

        }

        edit_image_user.setOnClickListener {

            var bottomSheetEditUserImage = BottomSheetEditUserImage(requireActivity())
            bottomSheetEditUserImage.show(requireActivity().supportFragmentManager, "Edit")

        }

        return Root

    }

    private fun declareData(){

        val declareDataUsers = DeclareDataUsers()
        declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
            override fun onDataDeclared(userAdminData: UserAdminData?) {
                // Now you can use userAdminData here
                if (userAdminData != null) {
                    noPosts.text = userAdminData.noPosts.toString()
                    fullName.text = userAdminData.name.toString()
                    userName.text = userAdminData.userName.toString()
                    noFollowers.text = FormatNumber.format(userAdminData.followers!!)
                    noFollowing.text = FormatNumber.format(userAdminData.following!!)
                    noPosts.text = FormatNumber.format(userAdminData.noPosts!!)
                    Picasso.get().load(userAdminData.imageUrl).into(imgAccount)
                }else{

                }

            }
        }, uid)

    }


    private fun EventChangeLister() {
        firestoreDb = FirebaseFirestore.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        List_rv = arrayListOf()

        firestoreDb.collection("Users").document(uid)
            .get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    arrPosts = documentSnapshot.get("postsId") as List<String>

                    val postFetchTasks = ArrayList<Task<DocumentSnapshot>>()
                    noPosts.text = arrPosts.size.toString()
                    for (postId in arrPosts) {
                        val postFetchTask = firestoreDb.collection("posts").document(postId).get()
                        postFetchTasks.add(postFetchTask)
                    }

                    Tasks.whenAllSuccess<DocumentSnapshot>(postFetchTasks)
                        .addOnSuccessListener { postSnapshots ->
                            val fetchedPosts = postSnapshots.mapNotNull { snapshot ->
                                snapshot.toObject<ReadDataPosts>()
                            }

                            fetchedPosts.sortedByDescending { it.time }
                                .forEach { post ->
                                    fetchUserDataAndUpdateItem(post)

                                    val declareDataUsers = DeclareDataUsers()
                                    declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
                                        override fun onDataDeclared(userAdminData: UserAdminData?) {

                                            if (userAdminData != null) {

                                                post.isBookmarked = userAdminData.postsBookmarks?.contains(post.postId.toString())

                                            }
                                        }
                                    }, FirebaseAuth.getInstance().uid.toString())
                                }
                        }
                }
            }

        adapter = AdapterRecycleViewPosts(requireContext(), List_rv, "ProfileFragment")
        rv.adapter = adapter
    }

    private fun fetchUserDataAndUpdateItem(item: ReadDataPosts) {
        if (item.userId?.id == null)
            return

        firestore.collection("Users").document(item.userId?.id ?: "")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject<UserAdminData>()
                    userData?.let {
                        item.userId?.name = it.name
                        item.userId?.userName = it.userName
                        item.userId?.imgAccount = it.imageUrl
                      //  item.postsBookmarks = userData.postsBookmarks
                    }
                }
                List_rv.add(item)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore Error", "Failed to fetch user data: ${e.message}")
            }
    }

}
