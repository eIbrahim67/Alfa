package com.eibrahim.alfa.mainActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eibrahim.alfa.adapterClasses.AdapterRecycleViewPosts
import com.eibrahim.alfa.dataClasses.ReadDataPosts
import com.eibrahim.alfa.declaredClasses.DeclareDataUsers
import com.eibrahim.alfa.R
import com.eibrahim.alfa.adapterClasses.AdapterRecyclerviewStories
import com.eibrahim.alfa.sign.SigninActivity
import com.eibrahim.alfa.dataClasses.UserAdminData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class HomeFragment : Fragment() {

    private lateinit var firestore : FirebaseFirestore
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var listDataRecyclerView : ArrayList<ReadDataPosts>
    private lateinit var adapter : AdapterRecycleViewPosts
    private lateinit var uid : String
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var currentId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        firestore = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()
        currentId = auth.currentUser?.uid.toString()


        val intent = Intent(activity, SigninActivity::class.java)
        if (auth.currentUser == null){

            startActivity(intent)

            requireActivity().finish()
        }

        val dataList : List<String> = listOf("1","1","1","1","1","1","1")

        val recyclerViewStories :RecyclerView = root.findViewById(R.id.recyclerview_stories)

        val adapterStories = AdapterRecyclerviewStories(dataList, requireContext())

        recyclerViewStories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerViewStories.adapter = adapterStories

        recyclerView = root.findViewById(R.id.recyclerview_posts)
        swipeRefreshLayout = root.findViewById(R.id.fragment_home)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        eventChangeLister()

        swipeRefreshLayout.setOnRefreshListener {

            eventChangeLister()

            swipeRefreshLayout.isRefreshing = false

        }

        return root
    }



    private fun eventChangeLister() {
        firestoreDb = FirebaseFirestore.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        listDataRecyclerView = arrayListOf()

        firestoreDb.collection("posts")
            .limit(20)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val post = dc.document.toObject(ReadDataPosts::class.java)
                            if (dc.document.id[0] != '*'){
                                fetchUserDataAndUpdateItem(post)
                                val declareDataUsers = DeclareDataUsers()
                                declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
                                    override fun onDataDeclared(userAdminData: UserAdminData?) {

                                        if (userAdminData != null) {
                                            post.isBookmarked = userAdminData.postsBookmarks?.contains(post.postId.toString())

                                        }
                                    }
                                }, FirebaseAuth.getInstance().uid.toString())

                                firestore.collection("postsLikes").document(post.postId.toString())
                                    .get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            val data = documentSnapshot.data
                                            if (data != null) {
                                                val likesArray = data["likes"] as? ArrayList<String>
                                                likesArray?.let {
                                                    post.isLoved = likesArray.contains(FirebaseAuth.getInstance().uid.toString())
                                                    post.noLikes = likesArray.size.toLong()
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }


                    adapter = AdapterRecycleViewPosts(requireContext(), listDataRecyclerView, "HomeFragment", requireActivity().supportFragmentManager)
                    recyclerView.adapter = adapter

                }
            })

    }

    private fun fetchUserDataAndUpdateItem(item: ReadDataPosts ) {

        if (item.userId?.id == null)
            return

        val declareDataUsers = DeclareDataUsers()
        declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
            override fun onDataDeclared(userAdminData: UserAdminData?) {
                if (userAdminData != null) {

                    item.userId?.name = userAdminData.name
                    item.userId?.userName = userAdminData.userName
                    item.userId?.imgAccount = userAdminData.imageUrl

                    listDataRecyclerView.add(item)
                    adapter.notifyDataSetChanged()
                }
            }
        }, item.userId?.id.toString())
    }

}
