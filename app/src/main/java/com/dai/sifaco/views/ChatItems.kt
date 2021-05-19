package com.dai.sifaco.views

import android.view.View
import com.dai.sifaco.R
import com.dai.sifaco.models.ChatMessage
import com.dai.sifaco.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder

import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatFromItem(val text: String, val user: User,val imgUrl:String) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val urlSend = imgUrl
            val uri = user.profileImageUrl

            viewHolder.itemView.time_from_row.text = SimpleDateFormat("hh:mm").format(Date())

            val targetImageView = viewHolder.itemView.imageview_chat_from_row
            val imageRecieveView = viewHolder.itemView.imageview_show_image_sended_from_row

            Picasso.get().load(uri).into(targetImageView)

            if (urlSend!="") {

                viewHolder.itemView.textview_from_row.text = text
                viewHolder.itemView.textview_from_row.visibility = View.GONE

                imageRecieveView.visibility = View.VISIBLE
                Picasso.get().load(urlSend).into(imageRecieveView)

                //Picasso.get().load(uri).into(targetImageView)

            }else{
                viewHolder.itemView.textview_from_row.text = text
                viewHolder.itemView.textview_from_row.visibility = View.VISIBLE

                imageRecieveView.visibility = View.GONE
                //Picasso.get().load(uri).into(targetImageView)
            }

        }

        override fun getLayout(): Int {
            return R.layout.chat_from_row
        }
    }

    class ChatToItem(val text: String, val user: User, val imgUrl: String) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.time_to_row.text = SimpleDateFormat("hh:mm a").format(Date())
            val urlSend = imgUrl
            val uri = user.profileImageUrl

            val targetImageView = viewHolder.itemView.imageview_chat_to_row
            val imageSendView = viewHolder.itemView.imageview_show_image_sended_to_row

            if(urlSend!="") {
                viewHolder.itemView.textview_to_row.text = text
                viewHolder.itemView.textview_to_row.visibility = View.GONE

                imageSendView.visibility = View.VISIBLE
                Picasso.get().load(urlSend).into(imageSendView)
                Picasso.get().load(uri).into(targetImageView)

            }
            else{
                viewHolder.itemView.textview_to_row.text = text
                viewHolder.itemView.textview_to_row.visibility = View.VISIBLE

                imageSendView.visibility = View.GONE
                Picasso.get().load(uri).into(targetImageView)
            }

        }

        override fun getLayout(): Int {
            return R.layout.chat_to_row
        }
    }