package com.example.myapplicationnote.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnote.R
import java.io.File


class ImageAdapter(var context: Context, private var addNoteImageList: MutableList<String>):RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
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
        Log.d("onbind",imagePathPosition)
        holder.itemView.setOnClickListener{
            println("clicked")
            //future updates
        }
        holder.itemView.setOnLongClickListener {
            if (File(addNoteImageList[position]).exists()){
                AlertDialog.Builder(it.context).apply {
                    setTitle("Delete Image!")
                    setMessage("Do you want to delete this Image?")
                    setPositiveButton("Delete"){_,_ ->
                        if (File(addNoteImageList[position]).delete()){
                            val adapterPosition = holder.adapterPosition
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                addNoteImageList.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                                notifyItemRangeChanged(adapterPosition, addNoteImageList.size)
                                Toast.makeText(it.context, "Image deleted", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(it.context, "Failed to delete Image", Toast.LENGTH_SHORT).show()
                        }
                    }
                    setNegativeButton("Cancel",null)
                }.create().show()
            }
            return@setOnLongClickListener true
        }

    }
}