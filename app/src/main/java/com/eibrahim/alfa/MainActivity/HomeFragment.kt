package com.eibrahim.alfa.MainActivity

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
import com.eibrahim.alfa.AdapterRecycleViewPosts
import com.eibrahim.alfa.DataClasses.DataPosts
import com.eibrahim.alfa.DataClasses.ReadDataPosts
import com.eibrahim.alfa.DeclaredClasses.DeclareDataUsers
import com.eibrahim.alfa.R
import com.eibrahim.alfa.Sign.SigninActivity
import com.eibrahim.alfa.DataClasses.UserAdminData
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
    private lateinit var rv2: RecyclerView
    private lateinit var List_rv2 : ArrayList<ReadDataPosts>
    private lateinit var adapter2 : AdapterRecycleViewPosts
    private lateinit var uid : String
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var currentId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val Root = inflater.inflate(R.layout.fragment_home, container, false)

        firestore = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()
        currentId = auth.currentUser?.uid.toString()


        var intent = Intent(activity, SigninActivity::class.java)
        if (auth.currentUser == null){

            startActivity(intent)

            requireActivity().finish()
        }


        rv2 = Root.findViewById(R.id.recyclerview_posts)
        swipeRefreshLayout = Root.findViewById(R.id.fragment_home)
        rv2.setLayoutManager(LinearLayoutManager(requireContext()))
        rv2.setHasFixedSize(true)

        EventChangeLister()

        swipeRefreshLayout.setOnRefreshListener {

            EventChangeLister()

            swipeRefreshLayout.isRefreshing = false

        }

        return Root
    }



    private fun EventChangeLister() {
        firestoreDb = FirebaseFirestore.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        List_rv2 = arrayListOf()

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
                            val item = dc.document.toObject(ReadDataPosts::class.java)
                            if (!dc.document.id[0].equals('*')){
                                fetchUserDataAndUpdateItem(item)
                                val declareDataUsers = DeclareDataUsers()
                                declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
                                    override fun onDataDeclared(userAdminData: UserAdminData?) {

                                        if (userAdminData != null) {

                                            item.isBookmarked = userAdminData.postsBookmarks?.contains(item.postId.toString())

                                        }
                                    }
                                }, FirebaseAuth.getInstance().uid.toString())
                            }

                        }

                    }

                    adapter2 = AdapterRecycleViewPosts(requireContext(), List_rv2, "HomeFragment", requireActivity().supportFragmentManager)
                    rv2.adapter = adapter2

                }
            })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUserDataAndUpdateItem(item: ReadDataPosts, ) {

        if (item.userId?.id == null)
            return

        val declareDataUsers = DeclareDataUsers()
        declareDataUsers.declareData(object : DeclareDataUsers.OnDataDeclaredListener {
            override fun onDataDeclared(userAdminData: UserAdminData?) {
                if (userAdminData != null) {

                    item.userId?.name = userAdminData.name
                    item.userId?.userName = userAdminData.userName
                    item.userId?.imgAccount = userAdminData.imageUrl

                    List_rv2.add(item)
                    adapter2.notifyDataSetChanged()
                }else{

                }

            }
        }, item.userId?.id.toString())
    }

}
