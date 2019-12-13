package org.wit.hillfortapp.views.hillfort

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.android.synthetic.main.activity_hillfort.*
import kotlinx.android.synthetic.main.content_hillfort_fab.*
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.HillfortModel
import org.wit.hillfortapp.models.ImageModel
import org.wit.hillfortapp.models.Location
import org.wit.hillfortapp.models.NoteModel
import org.wit.hillfortapp.views.BaseView

class HillfortView : BaseView(),
    NoteListener, AnkoLogger {

    private lateinit var presenter: HillfortPresenter
    private var location = Location()
    private var isFabOpen = false

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_hillfort, content_frame)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        presenter = initPresenter(HillfortPresenter(this)) as HillfortPresenter

        with(hillfortMapView) {
            onCreate(null)
            getMapAsync {
                MapsInitializer.initialize(applicationContext)
            }
        }

        hillfortDateVisited.setOnClickListener {
            showDatePickerDialog()
        }

//        hillfortFavouriteFAB.setOnClickListener {
//            presenter.doFavourite()
//        }
//
//        hillfortCancelFAB.setOnClickListener {
//            finish()
//        }

        hillfortMoreFAB.setOnClickListener {

            val fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_menu_open)
            val fabClose = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_menu_close)

            if (isFabOpen) {
                fabTextFavourite.visibility = View.INVISIBLE
                fabTextDelete.visibility = View.INVISIBLE
                fabTextShare.visibility = View.INVISIBLE

                fabMoreFavourite.startAnimation(fabClose)
                fabMoreDelete.startAnimation(fabClose)
                fabMoreShare.startAnimation(fabClose)

                fabMoreFavourite.isClickable = false
                fabMoreDelete.isClickable = false
                fabMoreShare.isClickable = false

                isFabOpen = false
            } else {
                fabTextFavourite.visibility = View.VISIBLE
                fabTextDelete.visibility = View.VISIBLE
                fabTextShare.visibility = View.VISIBLE

                fabMoreFavourite.startAnimation(fabOpen)
                fabMoreDelete.startAnimation(fabOpen)
                fabMoreShare.startAnimation(fabOpen)

                fabMoreFavourite.isClickable = true
                fabMoreDelete.isClickable = true
                fabMoreShare.isClickable = true

                isFabOpen = true
            }

        }


        hillfortSaveFAB.setOnClickListener {
            if (listOf(
                    hillfortName.text.toString(),
                    hillfortDescription.text.toString(),
                    hillfortDateVisited.text.toString()
                ).contains("")
            ) {
                toast("Please fill out all fields")
            } else {
                val tempHillfort = HillfortModel()
                tempHillfort.name = hillfortName.text.toString().trim()
                tempHillfort.description = hillfortDescription.text.toString().trim()
                tempHillfort.visited = hillfortVisited.isChecked
                tempHillfort.dateVisited = hillfortDateVisited.text.toString().trim()
                tempHillfort.location = location
                tempHillfort.rating = hillfortRatingBar.rating.toInt()
                info(hillfortRatingBar.rating)

                presenter.doAddOrSave(tempHillfort)
            }
        }

        hillfortAddNoteBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_note, null)
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Enter note details: ")
            builder.setView(mDialogView)

            val dialog: AlertDialog = builder.create()
            dialog.show()

            val addBtn = dialog.findViewById(R.id.noteDialogAddBtn) as Button
            val cancelBtn = dialog.findViewById(R.id.noteDialogCancelBtn) as Button
            val noteTitle = dialog.findViewById(R.id.noteDialogTitle) as? EditText
            val noteContent = dialog.findViewById(R.id.noteDialogContent) as? EditText

            addBtn.setOnClickListener {

                if (listOf(noteTitle!!.text.toString(), noteContent!!.text.toString())
                        .contains("")
                ) {
                    toast("Please fill out all fields!")
                } else {
                    presenter.doAddNote(noteTitle.text.toString(), noteContent.text.toString())
                    dialog.dismiss()
                }
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        hillfortChooseImageBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("This will reset the existing images, continue?")
            builder.setPositiveButton("YES") { dialog, _ ->
                presenter.doSelectImage()
                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        hillfortLocationBtn.setOnClickListener {
            presenter.doSetLocation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_hillfort, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.popupCancel -> {
                finish()
            }

            R.id.popupDelete -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete this Hillfort?")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    presenter.doDelete()
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            R.id.popupNext -> {
                presenter.doNext()
            }

            R.id.popupPrevious -> {
                presenter.doPrevious()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            presenter.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNoteClick(noteModel: NoteModel) {
        presenter.doClickNote(noteModel)
    }

    // hillfortMapView methods
    public override fun onResume() {
        hillfortMapView.onResume()
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
        hillfortMapView.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        hillfortMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        hillfortMapView.onLowMemory()
    }

    override fun showHillfort(hillfort: HillfortModel) {
        hillfortName.setText(hillfort.name)
        hillfortDescription.setText(hillfort.description)
        hillfortVisited.isChecked = hillfort.visited
        hillfortDateVisited.setText(hillfort.dateVisited)
        hillfortRatingBar.rating = hillfort.rating.toFloat()

        showNotes(hillfort.notes)
        showImages(hillfort.images)

        val latLng = LatLng(hillfort.location.lat, hillfort.location.lng)
        hillfortMapView.getMapAsync {
            setMapLocation(it, latLng)
        }
    }

    // Credit: https://tutorial.eyehunts.com/android/android-date-picker-dialog-example-kotlin/
    @TargetApi(Build.VERSION_CODES.N)
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                hillfortDateVisited.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            },
            year, month, day
        )
        dpd.show()
    }


    // source: https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
    private fun setMapLocation(map: GoogleMap, location: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5f))
        with(map) {
            addMarker(
                MarkerOptions().position(
                    location
                )
            )
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun showNotes(notes: ArrayList<NoteModel>?) {

        val layoutManager = LinearLayoutManager(this)
        val recyclerNotes = findViewById<RecyclerView>(R.id.recyclerNotes)
        recyclerNotes.layoutManager = layoutManager
        if (notes != null) {
            recyclerNotes.adapter =
                HillfortNotesAdapter(notes, this)
            recyclerNotes.adapter?.notifyDataSetChanged()

            val swipeHandler = object : HillfortNoteDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = HillfortNotesAdapter(notes, this@HillfortView)
                    adapter.removeAt(viewHolder.adapterPosition)
                    (recyclerNotes.adapter as HillfortNotesAdapter).notifyDataSetChanged()
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(recyclerNotes)
        }
    }


     override fun showImages(images: ArrayList<ImageModel>) {
        val imageViewPager = findViewById<ViewPager>(R.id.viewPager)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dotsIndicator)
        if (images != null) {
            imageViewPager.adapter =
                HillfortImageAdapter(
                    images,
                    this
                )
            dotsIndicator.setViewPager(imageViewPager)
            imageViewPager.adapter?.notifyDataSetChanged()
        }
    }

    override fun showUpdatedMap(latLng: LatLng) {
        hillfortMapView.getMapAsync { it.clear() }
        hillfortMapView.getMapAsync {
            setMapLocation(it, latLng)
        }
    }
}