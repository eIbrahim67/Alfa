package com.eibrahim.alfa.postFragments

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.eibrahim.alfa.declaredClasses.ImageDownloader
import com.eibrahim.alfa.R
import com.eibrahim.alfa.fragmentsShowrActivity.ShowedImageUrl
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import java.util.concurrent.Executors

class ShowImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val Root = inflater.inflate(R.layout.fragment_show_image, container, false)
        val photoView = Root.findViewById<PhotoView>(R.id.fullScreenImageView)
        val downloadImageBtn = Root.findViewById<RelativeLayout>(R.id.downloadImageBtn)
        val imageDownloader = ImageDownloader(requireContext())

        Picasso.get().load(ShowedImageUrl).into(photoView)

        // Declaring a Bitmap local
        var mImage: Bitmap?
        // Declaring and initializing an Executor and a Handler
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        downloadImageBtn.setOnClickListener {

            Toast.makeText(requireContext(), "Download started", Toast.LENGTH_SHORT).show()

            myExecutor.execute {
                mImage = imageDownloader.mLoad(ShowedImageUrl)

                myHandler.post {
                    //mImageView.setImageBitmap(mImage)
                    if(mImage!=null){
                        imageDownloader.mSaveMediaToStorage(mImage)
                    }
                }
            }

        }

        return Root
    }

}