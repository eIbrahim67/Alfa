package com.eibrahim.alfa

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
import com.eibrahim.alfa.BottomSheets.BottomSheetSharePost
import com.eibrahim.alfa.DataClasses.DataPosts
import com.eibrahim.alfa.DataClasses.ReadDataPosts
import com.eibrahim.alfa.FragmentsShowrActivity.FragmentsViewerActivity
import com.eibrahim.alfa.FragmentsShowrActivity.ShowedImageUrl
import com.eibrahim.alfa.FragmentsShowrActivity.no_page
import com.eibrahim.alfa.PostFragments.ShowedPostData
import com.eibrahim.alfa.PostFragments.ShowedPostId
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
) : RecyclerView.Adapter<AdapterRecycleViewPosts.rv2_VH>() {

    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var postCurrentId : String
    private lateinit var id : String
    private lateinit var currentUserId : String



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rv2_VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_posts, parent, false)
        return rv2_VH(view)
    }

    override fun getItemCount(): Int {
        return postsDataList.size;
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: rv2_VH, position: Int) {

        var post = postsDataList[position]
        val time = post.time?.let { convertTime(it) }

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid.toString()

        id = post.userId?.id.toString()
        postCurrentId = post.postId.toString()

        var likesBool = post.likes?.contains(currentUserId) == true
        //var dislikesBool = item.dislikes?.contains(currentId) == true

        //if(dislikesBool)
        //holder.dislikeBtn.setImageResource(R.drawable.disliked)
        if(likesBool)
            holder.likeBtn.setImageResource(R.drawable.loved)
        if(post.isBookmarked!!)
            holder.bookmarkBtn.setImageResource(R.drawable.bookmarked)

        if(post.postText == null)
            holder.postText.visibility = View.GONE

        if(post.imageUrl == null)
            holder.postImg.visibility = View.GONE

        holder.userName.text = post.userId?.userName.toString()

        if(post.userId?.imgAccount != null)
            Picasso.get().load(post.userId?.imgAccount).placeholder(R.drawable.infinity_loading_white).into(holder.imagePup)
        else
            Picasso.get().load(
                "https://firebasestorage.googleapis.com/v0/b/alfa-ed1e3.appspot.com/o/images%2Ffetrah.jpg?alt=media&token=3f776698-48e5-4ed8-897f-f42cbefffa27"
            ).placeholder(R.drawable.infinity_loading_white).into(holder.imagePup)

        holder.namePup.text = post.userId?.name.toString()

        holder.timeText.text = time.toString()

        holder.postText.text = post.postText.toString()

        Picasso.get().load(post.imageUrl).placeholder(R.drawable.infinity_loading_white).into(holder.postImg)

        val likesArr = post.likes?.toMutableList() ?: mutableListOf()

        holder.like_num.setText(likesArr.size.toString())

        var intent = Intent(context, FragmentsViewerActivity::class.java)
        holder.itemView.setOnClickListener {

            no_page = 8
            ShowedPostId =  post.postId.toString()
            ShowedPostData = post
            context.startActivity(intent)

        }

        holder.postImg.setOnClickListener {

            no_page = 6
            ShowedImageUrl =  post.imageUrl.toString()
            context.startActivity(intent)

        }

        holder.likeBtn.setOnClickListener {

            currentUserId = auth.currentUser?.uid.toString()
            id = post.userId?.id.toString()
            postCurrentId = post.postId.toString()
            var likedBool = likesArr.contains(currentUserId) == true

            firestore = FirebaseFirestore.getInstance()

            val documentRefPosts = firestore.collection("posts").document(postCurrentId)

            if(likedBool){

                holder.likeBtn.setImageResource(R.drawable.love)
                likesArr.remove(currentUserId)
                documentRefPosts.update("likes", FieldValue.arrayRemove(currentUserId))
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->

                    }

            }else{
                holder.likeBtn.setImageResource(R.drawable.loved)
                likesArr.add(currentUserId)
                documentRefPosts.update("likes", FieldValue.arrayUnion(currentUserId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                    }

            }

            holder.like_num.setText(likesArr.size.toString())

        }

        /*
        holder.dislikeBtn.setOnClickListener {

            id = item.userId?.id.toString()
            dislikesBool = item.dislikes?.contains(id) == true
            postCurrentId = item.postId.toString()

            if (dislikesBool == true)
                return@setOnClickListener

            holder.likeBtn.setImageResource(R.drawable.like)
            holder.dislikeBtn.setImageResource(R.drawable.disliked)

            val updatedLikes = item.likes?.toMutableList() ?: mutableListOf()
            updatedLikes.remove(id)
            item.likes = updatedLikes

            val updatedDislikes = item.dislikes?.toMutableList() ?: mutableListOf()
            updatedDislikes.add(id)
            item.dislikes = updatedDislikes


            updatelikes(item.likes ?: emptyList(), item.dislikes ?: emptyList())

        }
*/

        holder.bookmarkBtn.setOnClickListener {
            currentUserId = auth.currentUser?.uid.toString()
            id = post.userId?.id.toString()
            postCurrentId = post.postId.toString()

            firestore = FirebaseFirestore.getInstance()

            val documentRefUser = firestore.collection("Users").document(currentUserId)

            if(post.isBookmarked!!){

                holder.bookmarkBtn.setImageResource(R.drawable.bookmark)
                post.isBookmarked = false
                documentRefUser.update("postsBookmarks", FieldValue.arrayRemove(postCurrentId))
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->

                    }

            }else{
                holder.bookmarkBtn.setImageResource(R.drawable.bookmarked)
                post.isBookmarked = true
                documentRefUser.update("postsBookmarks", FieldValue.arrayUnion(postCurrentId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                    }

            }

        }

        /*if (context is Activity)
        val contextActivityName = context::class.java.simpleName*/

        holder.post_edit.setOnClickListener {


            val popupMenu = PopupMenu(context, holder.post_edit, Gravity.END, 0, R.style.rounded_popup_background)
            if (nameOfFragment.equals("HomeFragment")){

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

            }else if(nameOfFragment.equals("ProfileFragment")){
                popupMenu.inflate(R.menu.menu_pop_my_post)
                popupMenu.setOnMenuItemClickListener {item: MenuItem ->
                    when(item.itemId){

                        R.id.hidePostBtnMenuPop ->{

                            firestore = FirebaseFirestore.getInstance()
                            auth = FirebaseAuth.getInstance()
                            currentUserId = auth.currentUser!!.uid
                            var docRef = firestore.collection("posts").document(postCurrentId)

                            docRef.get()
                                .addOnSuccessListener {

                                    var data = it.data

                                    if (data != null) {
                                        firestore.collection("posts").document("*" + postCurrentId).set(data)
                                            .addOnSuccessListener {

                                                docRef.delete()
                                                Toast.makeText(context, "updated Successfully.", Toast.LENGTH_SHORT).show()

                                                val adapterPosition = holder.adapterPosition

                                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                                    postsDataList.removeAt(adapterPosition)
                                                    notifyItemRemoved(adapterPosition)
                                                }

                                            }
                                            .addOnFailureListener {

                                                Toast.makeText(context, "updated failed!", Toast.LENGTH_SHORT).show()

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
                            documentRefPost.delete()
                                .addOnSuccessListener{

                                    documentRefUser.update("noPosts", FieldValue.increment(-1))
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
            }else{
                popupMenu.inflate(R.menu.menu_pop_post)
                popupMenu.setOnMenuItemClickListener {item: MenuItem ->

                    makeText(context, "unExpected error!", Toast.LENGTH_SHORT).show()
                    true

                }
            }
            popupMenu.show()

        }

        holder.commentBtn.setOnClickListener {

        }

        holder.shareBtn.setOnClickListener {

            if (fragmentManager != null){

                var bottomSheetSharePost = BottomSheetSharePost()

                bottomSheetSharePost.show(fragmentManager, "Share")

            }
        }
    }

    private fun convertTime(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()

        val elapsedMillis = currentTimeMillis - timestamp

        if (elapsedMillis < DateUtils.MINUTE_IN_MILLIS) {
            return "just now"
        } else if (elapsedMillis < DateUtils.HOUR_IN_MILLIS) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
            return "$minutes min ago"
        } else if (elapsedMillis < DateUtils.DAY_IN_MILLIS) {
            val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
            return "$hours h ago"
        } else if (elapsedMillis > DateUtils.DAY_IN_MILLIS && elapsedMillis < 2 * DateUtils.DAY_IN_MILLIS) {
            val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
            return "Yesterday"
        } else {
            val dateFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
            return dateFormat.format(Date(timestamp))
        }

    }

    class rv2_VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        val like_num: TextView = itemView.findViewById(R.id.like_num)
        val post_edit: ImageView = itemView.findViewById(R.id.post_edit)


    }

}





