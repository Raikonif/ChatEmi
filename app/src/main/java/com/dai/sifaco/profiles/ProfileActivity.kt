package com.dai.sifaco.profiles

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.dai.sifaco.LatestMessagesActivity
import com.dai.sifaco.R
import com.dai.sifaco.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {


    companion object{
        val TAG = "ProfileActivity"
    }

    private var mDataBase: DatabaseReference? = null
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Perfil de Usuario"
        spinnerRank()
        spinnerForce()

        save_profile_button.setOnClickListener {

            uploadImageToFirebaseStorage()

            val refFirebase = FirebaseDatabase.getInstance().getReference("/users/$uid")

            val selectedForce = spinner_force.selectedItem.toString()
            refFirebase.child("ocupation").setValue(selectedForce)

            val selectedRank = spinner_ranks.selectedItem.toString()
            refFirebase.child("rank").setValue(selectedRank)

            toast("Usuario Actualizado")
            val intent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(intent)

        }

        selectphoto_button_profile.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data
            selectphoto_imageview_profile.setImageURI(selectedPhotoUri)
            selectphoto_button_profile.alpha = 0f

            //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            //selectphoto_imageview_profile.setImageBitmap(bitmap)


//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        //val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$uid/imgProfile")//filename

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        val mDatabase = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val username = edit_text_username.text.toString()
        //val rank = edit_text_rango.text.toString()
        //val ocupation = edit_text_ocupation.text.toString()


        mDatabase.child("username").setValue(username)
        //mDatabase.child("rank").setValue(rank)
        //mDatabase.child("ocupation").setValue(ocupation)
        mDatabase.child("profileImageUrl").setValue(profileImageUrl)



    }
    private fun spinnerRank(){
        val optionsRank = arrayListOf(/*"Selecciona un Rango",*/

            "Alférez",
            "Subteniente",
            "Teniente de Fragata",
            "Teniente",
            "Teniente de Navio",
            "Capitán",
            "Capitan de Corbeta",
            "Mayor",
            "Capitan de Fragata",
            "Teniente Coronel",
            "Capitan de Navío",
            "Coronel",
            "Contraalmirante",
            "General de Brigada Aérea",
            "General de Brigada",
            "Vicealmirante",
            "General de División Aérea",
            "General de División",
            "Almirante",
            "General de Fuerza Aerea",
            "General de Ejercito")

        val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, optionsRank)
        spinner_ranks.adapter = adapter

        //val refFirebase = FirebaseDatabase.getInstance().getReference("/users/$uid")

        spinner_ranks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val rankPositionOption = optionsRank[position]
                text_view_show_rank.text = rankPositionOption
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                    text_view_show_rank.text = "Selecciona Un Rango"
                    text_view_show_rank.visibility = View.VISIBLE
            }
        }

        //val selectedRank = spinner_ranks.selectedItem.toString()
        //if(selectedRank!=optionsRank[0]) {
        //    refFirebase.child("rank").setValue(selectedRank)
        //}else{
         //   toast("Selecciona un Rango")
        //}

    }
    private fun spinnerForce(){
        val optionsForce = arrayListOf(/*"Selecciona una Fuerza",*/

            "EJERCITO",
            "F.A.B.",
            "ARMADA")

        val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, optionsForce)
        spinner_force.adapter = adapter

        //val refFirebase = FirebaseDatabase.getInstance().getReference("/users/$uid")

        spinner_force.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val forcePositionOption = optionsForce[position]
                text_view_show_force.text = forcePositionOption

                /*if (forcePositionOption != optionsForce[0]){

                    //val refFirebase = FirebaseDatabase.getInstance().getReference("/users/$uid")
                    //refFirebase.child("ocupation").setValue(forcePositionOption)
                }

                else{

                }*/
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                text_view_show_force.text = "Selecciona Una Fuerza"
                text_view_show_force.visibility = View.VISIBLE
            }
        }

        //val selectedForce = spinner_force.selectedItem.toString()
        //if (selectedForce!=optionsForce[0]) {

          //  refFirebase.child("ocupation").setValue(selectedForce)
        //}else{
          //  toast("Selecciona una Fuerza")
       // }
    }
}