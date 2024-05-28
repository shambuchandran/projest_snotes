package com.example.myapplicationnote.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.example.myapplicationnote.R
import java.io.File


class ImageAdapter(var context: Context, private var addNoteImageList: MutableList<String>):RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
//    private var imagePaths: List<String> = listOf()

    class ImageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val imageViewEach: ImageView =itemView.findViewById(R.id.imageEachItemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageItemView= LayoutInflater.from(parent.context)
            .inflate(R.layout.image_eachitem_layout,parent,false)
        return ImageViewHolder(imageItemView)
    }

    override fun getItemCount(): Int {
        Log.d("size",addNoteImageList.size.toString())
        return addNoteImageList.size

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePathPosition=addNoteImageList[position]
        val file=File(imagePathPosition)
        val uri = Uri.fromFile(file)
        holder.imageViewEach.setImageURI(uri)
//        Glide.with(context).load(imagePathPosition).into(holder.imageViewEach)
        Log.d("onbind",imagePathPosition)

        holder.itemView.setOnClickListener{
            println("clicked")
        }
        holder.itemView.setOnLongClickListener {
            println("deleted")
            return@setOnLongClickListener true
        }

    }
//    fun setImagePath(paths: List<String>) {
//        addNoteImageList = paths
//        Log.d("image adapter", addNoteImageList.toString())
//        notifyDataSetChanged()
//    }
}