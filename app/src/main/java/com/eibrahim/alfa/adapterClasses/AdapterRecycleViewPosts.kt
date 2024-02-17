package com.eibrahim.alfa.adapterClasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.alfa.bottomSheets.BottomSheetSharePost
import com.eibrahim.alfa.dataClasses.ReadDataPosts
import com.eibrahim.alfa.fragmentsShowrActivity.FragmentsViewerActivity
import com.eibrahim.alfa.fragmentsShowrActivity.ShowedImageUrl
import com.eibrahim.alfa.fragmentsShowrActivity.no_page
import com.eibrahim.alfa.R
import com.eibrahim.alfa.fragmentsShowrActivity.ShowedUserAccount
import com.eibrahim.alfa.fragmentsShowrActivity.myAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AdapterRecycleViewPosts(
    private val context: Context,
    private val postsDataList: MutableList<ReadDataPosts> = mutableListOf(),
    private val nameOfFragment: String,
    private val fragmentManager: FragmentManager? = null
) : RecyclerView.Adapter<AdapterRecycleViewPosts.RecyclerViewPosts>() {

    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var postCurrentId : String
    private lateinit var currentUserId : String



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewPosts {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_posts, parent, false)
        return RecyclerViewPosts(view)
    }

    override fun getItemCount(): Int {
        return postsDataList.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerViewPosts, position: Int) {

        val post = postsDataList[position]
        val time = post.time?.let { convertTime(it) }
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid.toString()

        postCurrentId = post.postId.toString()

        holder.likeNum.text = post.noLikes.toString()

        if(post.isLoved!!)
            holder.likeBtn.setImageResource(R.drawable.loved)
        if(post.isBookmarked!!)
            holder.bookmarkBtn.setImageResource(R.drawable.bookmarked)

        if(post.postText == null)
            holder.postText.visibility = View.GONE

        if(post.imageUrl == null)
            holder.postImg.visibility = View.GONE

        holder.userName.text = post.userId?.userName.toString()

        val tempImage = "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Ffetrah.jpg?alt=media&token=3f776698-48e5-4ed8-897f-f42cbefffa27"

        if(post.userId?.imgAccount != null)
            Picasso.get()
                .load(post.userId?.imgAccount)
                .placeholder(R.drawable.loading_image_light)
                .into(holder.imagePup)
        else
            Picasso.get()
                .load(tempImage)
                .placeholder(R.drawable.loading_image_light)
                .into(holder.imagePup)

        holder.namePup.text = post.userId?.name.toString()

        holder.timeText.text = time.toString()

        holder.postText.text = post.postText.toString()

        Picasso.get()
            .load(post.imageUrl)
            .placeholder(R.drawable.loading_image_light)
            .into(holder.postImg)

        val intent = Intent(context, FragmentsViewerActivity::class.java)

        holder.itemView.setOnClickListener {

            no_page = 8
            context.startActivity(intent)

        }

        holder.postImg.setOnClickListener {

            no_page = 6
            ShowedImageUrl =  post.imageUrl.toString()
            context.startActivity(intent)

        }

        holder.likeBtn.setOnClickListener {

            firestore = FirebaseFirestore.getInstance()
            currentUserId = auth.currentUser?.uid.toString()
            postCurrentId = post.postId.toString()

            val documentRefPosts = firestore.collection("postsLikes").document(postCurrentId)
            val likeNumText: String
            if(post.isLoved!!){
                holder.likeBtn.setImageResource(R.drawable.love)
                likeNumText = (holder.likeNum.text.toString().toInt() - 1).toString()
                holder.likeNum.text = likeNumText
                post.isLoved = false
                documentRefPosts.update("likes", FieldValue.arrayRemove(currentUserId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {

                    }

            }else{
                holder.likeBtn.setImageResource(R.drawable.loved)
                likeNumText  = (holder.likeNum.text.toString().toInt() + 1).toString()
                holder.likeNum.text = likeNumText
                post.isLoved = true
                documentRefPosts.update("likes", FieldValue.arrayUnion(currentUserId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                    }
            }

        }

        holder.bookmarkBtn.setOnClickListener {

            firestore = FirebaseFirestore.getInstance()
            currentUserId = auth.currentUser?.uid.toString()
            postCurrentId = post.postId.toString()

            val documentRefUser = firestore.collection("Users").document(currentUserId)

            if(post.isBookmarked!!){

                holder.bookmarkBtn.setImageResource(R.drawable.bookmark)
                post.isBookmarked = false
                documentRefUser.update("postsBookmarks", FieldValue.arrayRemove(postCurrentId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                    }

            }else{
                holder.bookmarkBtn.setImageResource(R.drawable.bookmarked)
                post.isBookmarked = true
                documentRefUser.update("postsBookmarks", FieldValue.arrayUnion(postCurrentId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                    }

            }

        }

        /*if (context is Activity)
        val contextActivityName = context::class.java.simpleName*/

        holder.postEdit.setOnClickListener {


            val popupMenu = PopupMenu(context, holder.postEdit, Gravity.END, 0,
                R.style.rounded_popup_background
            )
            when (nameOfFragment) {
                "HomeFragment" -> {

                    popupMenu.inflate(R.menu.menu_pop_post_followed)
                    popupMenu.setOnMenuItemClickListener {item: MenuItem ->
                        when(item.itemId){

                            R.id.unFollowBtnMenuPop ->{
                                makeText(context, "unFollowBtnMenuPop", Toast.LENGTH_SHORT).show()
                                true
                            }

                            R.id.blockBtnMenuPop ->{
                                makeText(context, "blockBtnMenuPop", Toast.LENGTH_SHORT).show()
                                true
                            }

                            R.id.ReportPostBtnMenuPop ->{
                                makeText(context, "ReportPostBtnMenuPop", Toast.LENGTH_SHORT).show()
                                true
                            }

                            R.id.ReportPageBtnMenuPop ->{
                                makeText(context, "ReportPageBtnMenuPop", Toast.LENGTH_SHORT).show()
                                true
                            }

                            else ->{
                                makeText(context, "unSupported", Toast.LENGTH_SHORT).show()
                                true
                            }

                        }

                    }

                }
                "ProfileFragment" -> {
                    popupMenu.inflate(R.menu.menu_pop_my_post)
                    popupMenu.setOnMenuItemClickListener {item: MenuItem ->
                        when(item.itemId){

                            R.id.hidePostBtnMenuPop ->{

                                firestore = FirebaseFirestore.getInstance()
                                auth = FirebaseAuth.getInstance()
                                currentUserId = auth.currentUser!!.uid
                                val docRef = firestore.collection("posts").document(postCurrentId)

                                docRef.get()
                                    .addOnSuccessListener {

                                        val data = it.data

                                        if (data != null) {
                                            firestore.collection("posts").document("*$postCurrentId").set(data)
                                                .addOnSuccessListener {

                                                    docRef.delete()
                                                    makeText(context, "updated Successfully.", Toast.LENGTH_SHORT).show()

                                                    val adapterPosition = holder.adapterPosition

                                                    if (adapterPosition != RecyclerView.NO_POSITION) {
                                                        postsDataList.removeAt(adapterPosition)
                                                        notifyItemRemoved(adapterPosition)
                                                    }

                                                }
                                                .addOnFailureListener {

                                                    makeText(context, "updated failed!", Toast.LENGTH_SHORT).show()

                                                }
                                        }
                                    }

                                true
                            }

                            R.id.deletePostBtnMenuPop ->{

                                postCurrentId = post.postId.toString()
                                firestore = FirebaseFirestore.getInstance()
                                val documentRefPost = firestore.collection("posts").document(postCurrentId)
                                val documentRefUser = firestore.collection("Users").document(currentUserId)
                                val documentRecPostsLikes = firestore.collection("postsLikes").document(postCurrentId)

                                documentRecPostsLikes.delete()

                                documentRefPost.delete()
                                    .addOnSuccessListener{

                                        documentRefUser.update("postsId", FieldValue.arrayRemove(postCurrentId))

                                        val adapterPosition = holder.adapterPosition

                                        if (adapterPosition != RecyclerView.NO_POSITION) {
                                            postsDataList.removeAt(adapterPosition)
                                            notifyItemRemoved(adapterPosition)
                                        }

                                        makeText(context, "Post deleted Successfully.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener{
                                        makeText(context, "Post deleted Failed!", Toast.LENGTH_SHORT).show()

                                    }

                                true
                            }

                            else ->{
                                makeText(context, "unSupported", Toast.LENGTH_SHORT).show()
                                true
                            }

                        }

                    }
                }
                else -> {
                    popupMenu.inflate(R.menu.menu_pop_post)
                    popupMenu.setOnMenuItemClickListener {

                        makeText(context, "unExpected error!", Toast.LENGTH_SHORT).show()
                        true

                    }
                }
            }
            popupMenu.show()

        }

        holder.imagePup.setOnClickListener {

            no_page = 0
            ShowedUserAccount =  post.userId?.id.toString()
            myAccount = false
            context.startActivity(intent)

        }

        holder.shareBtn.setOnClickListener {

            if (fragmentManager != null){

                val bottomSheetSharePost = BottomSheetSharePost()

                bottomSheetSharePost.show(fragmentManager, "Share")

            }
        }
    }

    private fun convertTime(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()

        val elapsedMillis = currentTimeMillis - timestamp

        return if (elapsedMillis < DateUtils.MINUTE_IN_MILLIS) {
            "just now"
        } else if (elapsedMillis < DateUtils.HOUR_IN_MILLIS) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
            "$minutes min ago"
        } else if (elapsedMillis < DateUtils.DAY_IN_MILLIS) {
            val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
            "$hours h ago"
        } else if (elapsedMillis > DateUtils.DAY_IN_MILLIS && elapsedMillis < 2 * DateUtils.DAY_IN_MILLIS) {
            "Yesterday"
        } else {
            val dateFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
            dateFormat.format(Date(timestamp))
        }

    }

    class RecyclerViewPosts(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePup: TextView = itemView.findViewById(R.id.pup_name)
        val imagePup: ImageView = itemView.findViewById(R.id.pup_img)
        val userName: TextView = itemView.findViewById(R.id.pup_username)
        val postImg: ImageView = itemView.findViewById(R.id.post_img)
        val postText: TextView = itemView.findViewById(R.id.post_text)
        val timeText: TextView = itemView.findViewById(R.id.time_text)
        //val editPup: ImageView = itemView.findViewById(R.id.pup_edit)
        //val dislikeBtn: ImageView = itemView.findViewById(R.id.dislike_btn)
        val commentBtn: ImageView = itemView.findViewById(R.id.comment_btn)
        val likeBtn: ImageView = itemView.findViewById(R.id.like_btn)
        val bookmarkBtn: ImageView = itemView.findViewById(R.id.bookmark_btn)
        val shareBtn: ImageView = itemView.findViewById(R.id.share_btn)
        val likeNum: TextView = itemView.findViewById(R.id.like_num)
        val postEdit: ImageView = itemView.findViewById(R.id.post_edit)


    }

}





