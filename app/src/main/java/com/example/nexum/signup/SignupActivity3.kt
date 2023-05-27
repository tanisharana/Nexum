package com.example.nexum.signup
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.nexum.MainActivity
import com.example.nexum.R
import com.example.nexum.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern



class SignupActivity3 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup3)

        val signupButton:Button=findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            signup()
        }
    }
    private fun signup()
    {
        val firstName = intent.getStringExtra("firstname").toString()
        val lastName = intent.getStringExtra("lastname").toString()
        val email = intent.getStringExtra("emailName").toString()
        val password:EditText=findViewById(R.id.passwordInput)
        val confirmPassword:EditText=findViewById(R.id.confirmPasswordInput)
        val p: Pattern = Pattern.compile("^[a-z]+([2][0-9])(ucse|uari|ucam|umee|uece|ueee)[0-9][0-9][0-9]@mahindrauniversity.edu.in\$")
        val m: Matcher = p.matcher(email)

        val emailCheck: Boolean = m.matches()


        auth = Firebase.auth
        database = FirebaseDatabase.getInstance("https://nexum-c8155-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        if (!emailCheck) {
            Toast.makeText(
                this, "Only provide Mahindra email.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(
                this, "Passwords do not match.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email!!,password.text.toString()).addOnCompleteListener(this) {

            if (it.isSuccessful) {
                Toast.makeText(
                    this, "Authentication successful.",
                    Toast.LENGTH_SHORT
                ).show()

                writeNewUser(auth.currentUser!!.uid, firstName!! , lastName!!, email = email)


            } else {
                Toast.makeText(
                    this, it.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun writeNewUser(uid:String,firstName:String,lastName:String,email:String) {
        val user = User(firstName,lastName,email)

        fun generateProfilePictureAndUpload() {
            val baseColor = Color.WHITE
            val red = (baseColor.red + (0..256).random()) / 2
            val green = (baseColor.green + (0..256).random()) / 2
            val blue = (baseColor.blue + (0..256).random()) / 2
            val color = Color.rgb(red, green, blue)

            val bmp: Bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            val canvas = Canvas(bmp);
            canvas.drawColor(color)

            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val storageRef = Firebase.storage

            val ref = storageRef.reference.child("images/$email/profilePicture")
            val uploadTask = ref.putBytes(data)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    user.profilePicture = downloadUri.toString()
                    Toast.makeText(baseContext, user.profilePicture, Toast.LENGTH_SHORT).show()
                    database.child("users").child(uid).setValue(user).addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        }
        generateProfilePictureAndUpload()
    }
}

