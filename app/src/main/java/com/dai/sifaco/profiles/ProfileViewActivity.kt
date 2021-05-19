package com.dai.sifaco.profiles

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dai.sifaco.LatestMessagesActivity
import com.dai.sifaco.R
import com.dai.sifaco.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_view.*

class ProfileViewActivity : AppCompatActivity() {

    private var mDataBase: DatabaseReference? = null
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)

        supportActionBar?.title = "Perfil de Usuario"
        getValuesProfile()

        button_back_to_latest_message.setOnClickListener {
            intent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(intent)
        }

        button_edit_profile.setOnClickListener {
            intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


    }
    private fun getValuesProfile(){

        mDataBase = FirebaseDatabase.getInstance().getReference()
        mDataBase!!.child("users/$uid").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val usuario = snapshot.child("username").getValue().toString()
                    val rango = snapshot.child("rank").getValue().toString()
                    val ocupacion = snapshot.child("ocupation").getValue().toString()

                    val user = snapshot.getValue(User::class.java)

                    Picasso.get().load(user?.profileImageUrl).into(imageview_profile)
                    textview_username.text = usuario.toUpperCase()
                    textview_rank.text = rango.toUpperCase()
                    textview_ocupation.text = ocupacion.toUpperCase()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}