package org.wit.hillfortapp.models.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.hillfortapp.helpers.readImageFromPath
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.HillfortStore
import java.io.ByteArrayOutputStream
import java.io.File

class HillfortFireStore(val context: Context) : HillfortStore, AnkoLogger {

    val hillforts = ArrayList<HillfortModel>()
    val favouriteHillforts = ArrayList<String>()

    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference

    override fun findAllHillforts(): ArrayList<HillfortModel>? {
        return hillforts
    }

    override fun findOneHillfort(hillfortID: Int): HillfortModel? {
        return hillforts.find { p -> p.id == hillfortID }
    }

    override fun findHillfortsByName(name: String): ArrayList<HillfortModel>? {
        val foundHillforts: ArrayList<HillfortModel>? = arrayListOf()
        hillforts.forEach {
            if (it.name.toLowerCase().contains(name.toLowerCase())) {
                info("FOUND $name")
                foundHillforts?.add(it)
            }
        }
        info("RETURNING HILLFORTS:")
        info(foundHillforts)
        return foundHillforts
    }

    override fun createHillfort(hillfort: HillfortModel) {
        val key = db.child("users").child(userId).child("hillforts").push().key

        key?.let {
            hillfort.fbId = key
            hillforts.add(hillfort)
            db.child("users").child(userId).child("hillforts").child(key).setValue(hillfort)
            updateImage(hillfort)
        }
    }

    override fun updateHillfort(hillfort: HillfortModel) {
        val foundHillfort: HillfortModel? = hillforts.find { p -> p.fbId == hillfort.fbId }
        if (foundHillfort != null) {
            foundHillfort.name = hillfort.name
            foundHillfort.description = hillfort.description
            foundHillfort.images = hillfort.images
            foundHillfort.location = hillfort.location
            foundHillfort.dateVisited = hillfort.dateVisited
            foundHillfort.visited = hillfort.visited
            foundHillfort.notes = hillfort.notes
            foundHillfort.rating = hillfort.rating
            foundHillfort.isFavourite = hillfort.isFavourite
        }
        db.child("users").child(userId).child("hillforts").child(hillfort.fbId).setValue(hillfort)
        updateImage(hillfort)
        hillforts[hillforts.indexOf(hillfort)] = hillfort
    }

    override fun deleteHillfort(hillfort: HillfortModel) {
        db.child("users").child(userId).child("hillforts").child(hillfort.fbId).removeValue()
        // remove images
        deleteHillfortImages(hillfort.fbId)

        // remove from user favourites
        db.child("users").child(userId).child("favourites").child(hillfort.fbId).removeValue()

        hillforts.remove(hillfort)
    }

    override fun deleteAllHillforts() {
        db.child("users").child(userId).child("hillforts").removeValue()
        db.child("users").child(userId).child("favourites").removeValue()
        deleteUserImages()
        hillforts.clear()
    }

    private fun updateImage(hillfort: HillfortModel) {

        hillfort.images.forEach { image ->
            val fileName = File(image.uri)
            val imageName = fileName.name
            val imageRef = st.child("$userId/${hillfort.fbId}/$imageName")
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, image.uri)

            bitmap?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener { failure ->
                    println(failure.message)
                }.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        image.uri = uri.toString()
                        db.child("users").child(userId).child("hillforts").child(hillfort.fbId)
                            .setValue(hillfort)
                    }
                }
            }
        }
    }

    override fun toggleFavourite(hillfort: HillfortModel) {
        if (!hillfort.isFavourite) {
            db.child("users").child(userId).child("favourites").child(hillfort.fbId).removeValue()
        } else {
            val key = db.child("users").child(userId).child("favourites").push()
            key.let {
                db.child("users").child(userId).child("favourites").child(hillfort.fbId)
                    .setValue(hillfort.name)
            }
        }
    }

    override fun findOneFavourite(hillfort: HillfortModel): Boolean {
        return favouriteHillforts.contains(hillfort.fbId)
    }

    fun fetchHillforts(hillfortsReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.mapNotNullTo(hillforts) {
                    it.getValue<HillfortModel>(HillfortModel::class.java)
                }
                hillfortsReady()
            }
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference
        st = FirebaseStorage.getInstance().reference
        hillforts.clear()
        db.child("users").child(userId).child("hillforts")
            .addListenerForSingleValueEvent(valueEventListener)
    }

    fun fetchFavourites() {
        db.child("users").child(userId).child("favourites")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Error fetching favourites: $error")
                }

                override fun onDataChange(favourites: DataSnapshot) {
                    favourites.children.forEach {
                        favouriteHillforts.add(it.key.toString())
                    }
                }
            })
    }

    override fun sortedByFavourite(): List<HillfortModel>? {
        return hillforts.sortedWith(compareBy { it.isFavourite }).asReversed()
    }

    override fun sortByRating(): List<HillfortModel>? {
        return hillforts.sortedWith(compareBy { it.rating }).asReversed()
    }

    override fun sortByVisit(): List<HillfortModel>? {
        return hillforts.sortedWith(compareBy { it.visited }).asReversed()
    }

    override fun logout() {
        hillforts.clear()
    }

    private fun deleteUserImages() {
        hillforts.forEach {
            val images = st.child(userId).child(it.fbId)
            images.listAll().addOnSuccessListener { folder ->
                folder.items.forEach { item ->
                    item.delete()
                }
            }.addOnFailureListener { exception ->
                info("Error deleting photos: $exception")
            }
        }
    }

    private fun deleteHillfortImages(hillfortFBID: String) {
        val images = st.child(userId).child(hillfortFBID)
        images.listAll().addOnSuccessListener { folder ->
            folder.items.forEach { item ->
                item.delete()
            }
        }.addOnFailureListener { exception ->
            info("Error deleting photos: $exception")
        }
    }

    override fun deleteUser(user: FirebaseUser) {
        // delete hillfort related information
        db.child("users").child(userId).removeValue()
        deleteUserImages()
        deleteAllHillforts()
        
        user.delete()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "Error deleting user: " + task.exception)
                }
            }
    }
}