package com.eibrahim.alfa.fragmentsShowrActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eibrahim.alfa.adapterClasses.AdapterRecycleViewPosts
import com.eibrahim.alfa.dataClasses.ReadDataPosts
import com.eibrahim.alfa.R
import com.eibrahim.alfa.dataClasses.UserAdminData
import com.eibrahim.alfa.declaredClasses.DeclareDataUsers
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class BookmarksFragment : Fragment() {

    private lateinit var firestore : FirebaseFirestore
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var rv: RecyclerView
    private lateinit var listDataRecyclerView : ArrayList<ReadDataPosts>
    private lateinit var adapter : AdapterRecycleViewPosts
    private lateinit var uid : String
    private lateinit var arrPosts : List<String>
    private lateinit var backBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val Root = inflater.inflate(R.layout.fragment_bookmarks, container, false)

        rv = Root.findViewById(R.id.RecyclerView_bookmarks)
        backBtn = Root.findViewById(R.id.back_bookmarks_btn)

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.setHasFixedSize(true)

        swipeRefreshLayout = Root.findViewById(R.id.fragment_Bookmarked_posts)

        eventChangeLister()

        backBtn.setOnClickListener {

            requireActivity().finish()

        }

        swipeRefreshLayout.setOnRefreshListener {

            eventChangeLister()
            swipeRefreshLayout.isRefreshing = false

        }

        return Root
    }


    private fun eventChangeLister() {
        firestore = FirebaseFirestore.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        listDataRecyclerView = arrayListOf()

        firestore.collection("Users").document(uid)
            .get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    arrPosts = documentSnapshot.get("postsBookmarks") as List<String>

                    val postFetchTasks = ArrayList<Task<DocumentSnapshot>>()

                    for (postId in arrPosts) {
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

        adapter = AdapterRecycleViewPosts(requireContext(), listDataRecyclerView, "ProfileFragment")
        rv.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
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
                listDataRecyclerView.add(item)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore Error", "Failed to fetch user data: ${e.message}")
            }
    }
}