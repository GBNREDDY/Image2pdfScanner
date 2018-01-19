package com.android.galleryexample

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.app.Dialog
import android.support.v7.widget.PopupMenu
import android.widget.Button


class ImageBitmapAdapter(private var context: MainActivity, private var inflater: LayoutInflater, private var bitmapMap: ArrayList<Bitmap>?) : RecyclerView.Adapter<ImageBitmapAdapter.ItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder? {
        val view = inflater.inflate(R.layout.itemholder, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return bitmapMap!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemHolder?, position: Int) {
        holder!!.image.setImageBitmap(bitmapMap!![position])
        holder.tv.text = "${position + 1}"
        holder.editimage.setOnClickListener {
            //creating a popup menu
            val popup = PopupMenu(context, holder.editimage)
            //inflating menu from xml resource
            popup.inflate(R.menu.itemmenu)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        context.update(position)
                    }
                    R.id.delete -> {
                        context.remove(position)
                    }

                }//handle menu1 click
                //handle menu2 click
                //handle menu3 click
                false
            }
            //displaying the popup
            popup.show()
        }

        holder.itemView.setOnClickListener {
            val view = inflater.inflate(R.layout.dialogue_item_view, null )
            val iv = view.findViewById<ImageView>(R.id.iv)
            iv.setImageBitmap(bitmapMap!![position])
            val ivback = view.findViewById<ImageView>(R.id.backimg)
            val ivedit = view.findViewById<ImageView>(R.id.editimg)
            val tvposition = view.findViewById<TextView>(R.id.tvposition)
            tvposition.text = "${position+1}"
            val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(view)
            dialog.show()
            ivback.setOnClickListener{
                dialog.dismiss()
            }

            ivedit.setOnClickListener{
                //creating a popup menu
                val popup = PopupMenu(context, ivedit)
                //inflating menu from xml resource
                popup.inflate(R.menu.itemmenu)
                //adding click listener
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit -> {
                            dialog.dismiss()
                            context.update(position)
                        }
                        R.id.delete -> {
                            dialog.dismiss()
                            context.remove(position)
                        }

                    }//handle menu1 click
                    //handle menu2 click
                    //handle menu3 click
                    false
                }
                //displaying the popup
                popup.show()
            }
        }
    }


    class ItemHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView = v.findViewById(R.id.bimg)
        var tv: TextView = v.findViewById(R.id.count)
        var editimage: ImageView = v.findViewById(R.id.editimg)

    }

}
