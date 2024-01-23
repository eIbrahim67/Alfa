package com.eibrahim.alfa.PostFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eibrahim.alfa.AdapterRecycleViewPosts
import com.eibrahim.alfa.DeclaredClasses.DeclareDataUsers
import com.eibrahim.alfa.R
import com.eibrahim.alfa.Sign.SigninActivity
import com.eibrahim.alfa.DataClasses.UserAdminData
import com.eibrahim.alfa.DataClasses.DataPosts
import com.eibrahim.alfa.DataClasses.ReadDataPosts
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso

public var ShowedPostId : String = ""
public var ShowedPostData : ReadDataPosts = ReadDataPosts()

class ShowPostFragment : Fragment() {

    private lateinit var firestore : FirebaseFirestore
    private lateinit var recyclerview_comments_show_post: RecyclerView
    private lateinit var listsOfComments : ArrayList<ReadDataPosts>
    private lateinit var adapterComments : AdapterRecycleViewPosts
    private lateinit var uid : String
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var currentId : String
    private lateinit var arrComments : List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_show_post, container, false)

        var intent = Intent(activity, SigninActivity::class.java)
        startActivity(intent)

        firestore = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()
        currentId = auth.currentUser?.uid.toString()

        val namePup: TextView = root.findViewById(R.id.pup_name_show_post)
        val imagePup: ShapeableImageView = root.findViewById(R.id.pup_img_show_post)
        //val userName: TextView = root.findViewById(R.id.user_name_pup_show_post)
        val postImg: ShapeableImageView = root.findViewById(R.id.post_img_show_post)
        val postText: TextView = root.findViewById(R.id.post_text_show_post)
        val timeText: TextView = root.findViewById(R.id.time_text_show_post)
        //val editPup: ImageView = root.findViewById(R.id.pup_edit_show_post)
        //val dislikeBtn: ImageView = root.findViewById(R.id.dislike_btn_show_post)
        val commentBtn: ImageView = root.findViewById(R.id.comment_btn_show_post)
        val likeBtn: ImageView = root.findViewById(R.id.like_btn_show_post)
        val bookmarkBtn: ImageView = root.findViewById(R.id.bookmark_btn_show_post)
        val shareBtn: ImageView = root.findViewById(R.id.share_btn_show_post)
        val like_num: TextView = root.findViewById(R.id.like_num_show_post)
        val post_edit: ImageView = root.findViewById(R.id.post_edit_show_post)

        //if(likesBool)
            //likeBtn.setImageResource(R.drawable.loved)
        //if(bookmarksSavedBool)
            //bookmarkBtn.setImageResource(R.drawable.bookmarked)

        //if(post.postText == null)
            //postText.visibility = View.GONE

        //if(post.imageUrl == null)
            //holder.postImg.visibility = View.GONE

        //holder.userName.text = item.userId?.userName.toString()


        Picasso.get().load(ShowedPostData.userId?.imgAccount).into(imagePup)

        namePup.text = ShowedPostData.userId?.name.toString()

        timeText.text = ShowedPostData.time.toString()

        postText.text = ShowedPostData.postText.toString()
        Picasso.get().load(ShowedPostData.imageUrl).into(postImg)

        val likesArr = ShowedPostData.likes?.toMutableList() ?: mutableListOf()

        like_num.setText(likesArr.size.toString())



        recyclerview_comments_show_post = root.findViewById(R.id.recyclerview_comments_show_post)
        swipeRefreshLayout = root.findViewById(R.id.fragment_show_post)
        recyclerview_comments_show_post.setLayoutManager(LinearLayoutManager(requireContext()))
        recyclerview_comments_show_post.setHasFixedSize(true)

        EventChangeLister()

        swipeRefreshLayout.setOnRefreshListener {

            EventChangeLister()

            swipeRefreshLayout.isRefreshing = false

        }

        return root
    }

    private fun EventChangeLister() {

        firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        listsOfComments = arrayListOf()

        firestore.collection("posts").document(ShowedPostId)
            .get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    arrComments = documentSnapshot.get("comments") as List<String>

                    val postFetchTasks = ArrayList<Task<DocumentSnapshot>>()
                    for (postId in arrComments) {
                        val postFetchTask = firestore.collection("posts").document(postId).get()
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

        adapterComments = AdapterRecycleViewPosts(requireContext() ,listsOfComments, "ShowPostFragment", requireActivity().supportFragmentManager)
        recyclerview_comments_show_post.adapter = adapterComments
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUserDataAndUpdateItem(item: ReadDataPosts) {

        if (item.userId?.id == null)
            return

        val declareDataUsers = DeclareDataUsers()
        declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
            override fun onDataDeclared(userAdminData: UserAdminData?) {
                if (userAdminData != null) {

                    item.userId?.name = userAdminData.name
                    item.userId?.userName = userAdminData.userName
                    item.userId?.imgAccount = userAdminData.imageUrl
                    listsOfComments.add(item)
                    adapterComments.notifyDataSetChanged()
                }else{

                }

            }
        }, item.userId?.id.toString())
    }

}