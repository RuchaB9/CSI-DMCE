package com.example.csi_dmce.events


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.csi_dmce.R
import com.example.csi_dmce.attendance.AdminViewQR
import com.example.csi_dmce.attendance.AttendanceActivity
import com.example.csi_dmce.auth.CsiAuthWrapper
import com.example.csi_dmce.database.Event
import com.example.csi_dmce.database.EventWrapper
import com.example.csi_dmce.utils.Helpers
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.util.*


// TODO: Move the event card somewhere else, preferably in the dashboard.
class EventViewActivity: AppCompatActivity() {
//    private lateinit var tvEventMinPoster: ImageView
//    private lateinit var tvEventMinTitle: TextView
//    private lateinit var tvEventMinDate: TextView
//    private lateinit var tvEventMinTime: TextView
//    private lateinit var tvEventMinVenue: TextView

    private val REQUEST_CODE_EVENT_UPDATE = 102

    private lateinit var ivEventPoster: ImageView
    private lateinit var tvEventTitle: TextView
    private lateinit var tvEventMonth: TextView
    private lateinit var tvEventDay: TextView
    private lateinit var tvEventTime: TextView
    private lateinit var tvEventVenue: TextView
    private lateinit var tvEventDescription: TextView
    private lateinit var btnEventRegister: Button
    private lateinit var btnEventUpdate: Button
    private lateinit var btnEventDelete: Button
    private lateinit var btnEventAttendance: Button

    private var stateRegistered: Boolean = false

    /**
     * 1. When the activity starts, pop up the card.
     * 2. When clicked on 'View more details', take me to the event details page.
     *  - This should load all the event data from DB.
     *
     * 3. When clicked on register, transition and switch the button state to registered.
     *  - Create an attendance entry in the DB: collection=attendance.
     *  - Create an attendance entry in the DB: collection=user
     *  - Create an attendance entry in the DB: collection=event
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

         val eventId: String = intent.getStringExtra("event_id").toString()

        val eventObject: Event = runBlocking { EventWrapper.getEvent(eventId)!! }

        val eventDateTime: Date = Helpers.generateDateFromUnixTimestamp(eventObject.datetime!!)

        tvEventTitle = findViewById(R.id.text_view_event_title)
        tvEventTitle.text = eventObject.title

        tvEventVenue = findViewById(R.id.text_view_event_venue)
        tvEventVenue.text = eventObject.venue

        tvEventTime = findViewById(R.id.text_view_event_time)
        tvEventTime.text = Helpers.eventTimeFormat.format(eventDateTime).toString()

        tvEventMonth = findViewById(R.id.text_view_event_month)
        tvEventMonth.text = Helpers.eventMonthFormat.format(eventDateTime).toString()

        tvEventDay = findViewById(R.id.text_view_event_day)
        tvEventDay.text = Helpers.eventDayFormat.format(eventDateTime).toString()

        tvEventDescription = findViewById(R.id.text_view_event_description)
        tvEventDescription.text = eventObject.description

        ivEventPoster = findViewById(R.id.image_view_event_poster)

        val eventPosterUrl = runBlocking {
            EventWrapper.getPosterUrl(eventObject.eventId!!, eventObject.poster_extension!!)
        }

        Glide.with(this)
            .setDefaultRequestOptions(RequestOptions())
            .load(eventPosterUrl)
            .into(ivEventPoster)


        ivEventPoster.setOnClickListener{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.component_image_scale_popup)
            val ivFullScale = dialog.findViewById<ImageView>(R.id.image_view_fullscale)
            ivFullScale.setImageDrawable(ivEventPoster.drawable)

            dialog.show()
            dialog.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
        }

        btnEventAttendance = findViewById(R.id.button_event_attendance)
        val currentDateTime = Date()
        if (currentDateTime > eventDateTime) {
//            btnEventAttendance.isClickable = false
//            btnEventAttendance.setBackgroundColor(Color.parseColor("#808080"))
        } else {
            btnEventAttendance.setOnClickListener {
                Log.d("ATTENDANCE", "CLICKED")
                // TODO: Add attendance activity here.
                val sIntent = if (CsiAuthWrapper.getRoleFromToken(applicationContext).isAdmin()) {
                    Intent(applicationContext, AdminViewQR::class.java)
                        .putExtra("event_id", eventObject.eventId)
                } else {
                    Intent(applicationContext, AttendanceActivity::class.java)
                        .putExtra("student_id", CsiAuthWrapper.getStudentId(this))
                        .putExtra("event_id", eventObject.eventId)
                }

                startActivity(sIntent)
            }
        }

        if (CsiAuthWrapper.getRoleFromToken(applicationContext).isAdmin()) {
            btnEventUpdate = findViewById(R.id.button_event_update)
            btnEventUpdate.visibility = View.VISIBLE
            btnEventUpdate.setOnClickListener {
                val intent = Intent(this, EventUpsertActivity::class.java)
                intent.putExtra("update_event", Gson().toJson(eventObject))
                startActivityForResult(intent, REQUEST_CODE_EVENT_UPDATE)
            }

            btnEventDelete = findViewById(R.id.button_event_delete)
            btnEventDelete.visibility = View.VISIBLE
            btnEventDelete.setOnClickListener {
                val dialog = Dialog(this, R.style.Theme_CSIDMCE)
                dialog.setContentView(R.layout.component_event_delete_popup)

                val etDeleteEventName = dialog.findViewById<EditText>(R.id.edit_text_event_delete_name)
                etDeleteEventName.setHint(eventObject.title)

                val tvDeleteEventTitle = dialog.findViewById<TextView>(R.id.text_view_event_delete_title)
                tvDeleteEventTitle.setText(eventObject.title)

                val btnDeletePositive = dialog.findViewById<Button>(R.id.button_delete_positive)
                btnDeletePositive.setOnClickListener {
                    if (etDeleteEventName.text.toString() != eventObject.title) {
                        Toast.makeText(this, "Please enter the event's title", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    EventWrapper.deleteEvent(eventObject)
                    Toast.makeText(this, "Deleted event successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                    setResult(Activity.RESULT_OK)
                    finish()
                }

                val btnDeleteNegative = dialog.findViewById<Button>(R.id.button_delete_negative)
                btnDeleteNegative.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
            }

        } else {
            btnEventRegister = findViewById(R.id.button_event_register)
            btnEventRegister.visibility = View.VISIBLE
            btnEventRegister.setOnClickListener {
                val dialog = BottomSheetDialog(this)
                if (!stateRegistered) {
                    dialog.setContentView(R.layout.component_event_register_popup)
                } else {
                    dialog.setContentView(R.layout.component_event_unregister_popup)
                }
                dialog.setCancelable(true)
                dialog.findViewById<Button>(R.id.event_dialog_confirm_button)?.setOnClickListener {
                    if (!stateRegistered) {
                        btnEventRegister.text = "Registered"
                        btnEventRegister.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.green)));
                        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_event_check_mark)
                        btnEventRegister.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                        stateRegistered = true
                    } else {
                        btnEventRegister.text = "Register"
                        btnEventRegister.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.csi_primary_accent)));
                        btnEventRegister.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                        stateRegistered = false
                    }

                    if (!stateRegistered) {
                        runBlocking { EventWrapper.unregisterStudent(eventObject, CsiAuthWrapper.getStudentId(applicationContext)) }
                    } else {
                        runBlocking { EventWrapper.registerStudent(eventObject, CsiAuthWrapper.getStudentId(applicationContext)) }
                    }


                    dialog.dismiss()
                }
                dialog.findViewById<Button>(R.id.event_dialog_cancel_button)?.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        intent.putExtra("REFRESH", true)
        finish()
//        if (requestCode == REQUEST_CODE_EVENT_UPDATE && resultCode == RESULT_OK && data != null && data.data != null) {
//            val intent = Intent(applicationContext, EventViewActivity::class.java)
//            startActivity(intent)
//        }
    }
}