package com.example.nexum.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nexum.OpportunityDetailsActivity
import com.example.nexum.R
import com.example.nexum.firebasefunctions.deleteEvent
import com.example.nexum.firebasefunctions.deleteOppo
import com.example.nexum.firebasefunctions.oppoFromMap
import com.example.nexum.firebasefunctions.userFromMap
import com.example.nexum.model.Opportunity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.sql.Date
import java.text.SimpleDateFormat


class OpportunityItemAdapter(val dataset:MutableList<Opportunity>): RecyclerView.Adapter<OpportunityItemAdapter.ItemViewHolder>()
{
    lateinit var context: Context
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val profilePicture: ShapeableImageView =view.findViewById(R.id.profilePicture)
        val username:TextView=view.findViewById(R.id.username)
        val applyButton:Button=view.findViewById(R.id.applyButton)
        val title:TextView=view.findViewById(R.id.title)
        val datePosted:TextView=view.findViewById(R.id.datePosted)
        val description:TextView=view.findViewById(R.id.description)
        val oppoCard:MaterialCardView=view.findViewById(R.id.oppoCard)
        init{

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        context=parent.context
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.opportunities_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        var database = FirebaseDatabase.getInstance("https://nexum-c8155-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val userMap=dataSnapshot.child(item.uid!!).value as Map<String,Any?>
                val user = userFromMap(userMap)
                holder.username.text = user.firstName + " " + user.lastName
                if(user.profilePicture!=null)
                    Glide.with(holder.itemView.context).load(user.profilePicture!!.toUri()).into(holder.profilePicture)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })

        holder.title.text=item.title
        holder.description.text=item.description
        holder.datePosted.text= SimpleDateFormat("dd/MM/yy hh:mm a").format(Date(item.datePosted))
        holder.applyButton.setOnClickListener {
            val browse = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
            holder.itemView.context.startActivity(browse)
        }
        holder.oppoCard.setOnLongClickListener {
            val auth= Firebase.auth
            if(auth.currentUser!!.uid==item.uid) {
                MaterialAlertDialogBuilder(holder.itemView.context)
                    .setTitle("Delete ${item.title}")
                    .setMessage("Are you sure you want to delete this post? All data related to ${item.title} will be lost permanently.")
                    .setNegativeButton("Cancel") { dialog, which ->

                    }
                    .setPositiveButton("Delete") { dialog, which ->
                        deleteOppo(item.key!!,context)
                        Toast.makeText(context,"Opportunity Deleted", Toast.LENGTH_SHORT).show()

                    }
                    .show()
            }
            return@setOnLongClickListener true
        }
        holder.oppoCard.setOnClickListener{
            val keyOpportunity = item.key
            val intent = Intent(holder.itemView.context, OpportunityDetailsActivity::class.java)
            intent.putExtra("key", keyOpportunity)
            holder.itemView.context.startActivity(intent)
        }

    }
    /**
     * Return the size of your dataset (invoked by the layout manager)
     */ override fun getItemCount() = dataset.size
}