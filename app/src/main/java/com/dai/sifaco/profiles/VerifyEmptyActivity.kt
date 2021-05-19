package com.dai.sifaco.profiles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dai.sifaco.LatestMessagesActivity
import com.dai.sifaco.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class VerifyEmptyActivity: AppCompatActivity() {


    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private val mDatabaseVerify = FirebaseDatabase.getInstance().getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//verificar si el usuario esta conectado, o ha logeado previamente, si lo hizo va directamente al latestmessage activity, si no lo esta va al login


        verifyUserForPolitics()

    }

    private fun verifyUserForPolitics() {

        val checking = mDatabaseVerify.child("/users/$uid/verify").toString()

        if (checking=="checked") {

            Toast.makeText(this, "Usuario Correcto", Toast.LENGTH_SHORT).show()

            val goToMainActivity = Intent(this, LatestMessagesActivity::class.java)
            goToMainActivity.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(goToMainActivity)

        } else {

            toast("Usuario No Verificado")

            val goToPoliticsActivity = Intent(this, PoliticsActivity::class.java)
            goToPoliticsActivity.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(goToPoliticsActivity)


        }
    }

}
