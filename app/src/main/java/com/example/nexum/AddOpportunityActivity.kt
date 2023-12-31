package com.example.nexum

import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nexum.databinding.ActivityAddEventBinding
import com.example.nexum.databinding.ActivityAddOpportunityBinding
import com.example.nexum.model.Opportunity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class AddOpportunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOpportunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddOpportunityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.submit.setOnClickListener {
            if(checkFields()) {
                val auth = Firebase.auth
                val opportunity=Opportunity(
                    auth.currentUser!!.uid,
                    binding.opportunityNameInput.text.toString().trim(),
                    binding.descriptionInput.text.toString().trim(),
                    binding.linkInput.text.toString().trim()
                )
                uploadOpportunity(opportunity)
            }
        }

    }
    private fun uploadOpportunity(opportunity: Opportunity)
    {
        binding.submit.isEnabled=false
        binding.submit.text="Adding Opportunities"
        binding.progressBar.show()
        val database = FirebaseDatabase.getInstance("https://nexum-c8155-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        val key = database.child("opportunities").push().key
        database.child("opportunities").child(key!!).setValue(opportunity).addOnSuccessListener {
            Toast.makeText(this,"Opportunities added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkFields():Boolean
    {
        var check:Boolean=true;

        if (binding.opportunityNameInput.text.toString().isEmpty()) {
            binding.opportunityNameInput.error = "This field is required"
            check = false
        }
        if (binding.descriptionInput.text.toString().isEmpty()) {
            binding.descriptionInput.error = "This field is required"
            check = false
        }
        if (binding.linkInput.text.toString().isEmpty()) {
            binding.linkInput.error = "This field is required"
            check = false
        }else if(!isFormLink(binding.linkInput.text.toString())) {
            binding.linkInput.error = "Enter a valid form link"
            check=false
        }
        /*else if(!isLink(binding.linkInput.text.toString()))
        {
            binding.linkInput.error = "Enter valid link"
            check = false
        }*/
        // after all validation return true.
        return check

    }

    fun isLink(link:String):Boolean
    {
        val pattern = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
        return pattern.containsMatchIn(link)
    }

    fun isFormLink(link:String):Boolean
    {
        val patternGoogleForm= Regex("^https:\\/\\/docs\\.google\\.com\\/forms\\/d\\/.*")
        val patternGoogleShortenedForm= Regex("^https:\\/\\/forms\\.gle\\/.*")
        val patternMicrosoftForm= Regex("^https:\\/\\/forms\\.office\\.com\\/(Pages\\/ResponsePage\\.aspx\\?id=|r\\/).*")
        return (patternGoogleForm.containsMatchIn(link) ||
            patternGoogleShortenedForm.containsMatchIn(link) ||
            patternMicrosoftForm.containsMatchIn(link)
        )
    }

}
