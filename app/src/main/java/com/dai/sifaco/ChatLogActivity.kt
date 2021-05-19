package com.dai.sifaco

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dai.sifaco.models.ChatMessage
import com.dai.sifaco.models.User
import com.dai.sifaco.profiles.ProfileActivity
import com.dai.sifaco.views.ChatFromItem
import com.dai.sifaco.views.ChatToItem
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    private var selectedPhotoUri: Uri ?= null
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

//    setupDummyData()
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            val message = edittext_chat_log.text.toString()
            if (message == "") {

                toast("Escribe un mensaje primero")

            } else {

                performSendMessage()

            }

        }

        imageview_send_image.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
// onrequestpermissionresult override fun
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.data!=null) {
            // proceed and check what the selected image was....
            Log.d(ProfileActivity.TAG, "Photo was selected")

            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Image Sending")
            loadingBar.show()

            selectedPhotoUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(selectedPhotoUri!!)
//end requestpermissionresult
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    val fromId = FirebaseAuth.getInstance().uid ?: ""
                    val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                    val toId = user!!.uid


                    val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

                    val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

                    val chatMessage = ChatMessage(reference.key!!, "", fromId, toId, System.currentTimeMillis() / 1000, url)

                    reference.setValue(chatMessage)
                        .addOnSuccessListener {
                            Log.d(TAG, "Saved our chat message: ${reference.key}")

                            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                        }

                    toReference.setValue(chatMessage)

                    val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                    latestMessageRef.setValue(chatMessage)

                    val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                    latestMessageToRef.setValue(chatMessage)

                    loadingBar.dismiss()
                }
            }
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    val currentUser = LatestMessagesActivity.currentUser ?: return
                    if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {

                        adapter.add(ChatFromItem(chatMessage.text, toUser!!, chatMessage.url))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, currentUser, chatMessage.url))
                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun performSendMessage() {

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user!!.uid

        val text = edittext_chat_log.text.toString()

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000,"")

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}