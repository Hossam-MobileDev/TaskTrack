package com.androidTask.trackingapp.adapters

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.tasktrack.constants.TrackingUtility

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: Bitmap?) {

        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)

}
@BindingAdapter("customkm")
fun bindDistance(view: TextView, distanceInmeters: Int) {

    view.text = (distanceInmeters/1000).toString()

}
@BindingAdapter("customms")
fun bindTime(view: TextView, timeInMilliSeconds: Long) {

    view.text = TrackingUtility.getFormattedStopWatchTime(timeInMilliSeconds)

}