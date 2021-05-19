package com.dai.sifaco.profiles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dai.sifaco.R
import com.dai.sifaco.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_politics.*

class PoliticsActivity : AppCompatActivity() {
    private var mDatabase: DatabaseReference?= null
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_politics)

        button_accept.setOnClickListener {

            mDatabase = FirebaseDatabase.getInstance().getReference()
            mDatabase!!.child("/users/$uid/verify").setValue("checked")

            toast("Usuario Verificado")

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        button_deny.setOnClickListener {

            toast("No Puede Hacer Uso de la Aplicacion")
            mAuth.signOut()
            finish()

        }
    }
}