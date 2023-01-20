package com.example.csi_dmce


import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Insets.add
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets.add
import com.applandeo.materialcalendarview.CalendarView.Companion.ONE_DAY_PICKER
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.applandeo.materialcalendarview.utils.calendar
import com.example.csi_dmce.databinding.CsiCalendarBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class CSICalendar : AppCompatActivity() ,OnSelectDateListener, OnDayClickListener {

    private lateinit var binding: CsiCalendarBinding
    val fire_db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.csi_calendar)
        binding = CsiCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewEvents.setOnClickListener { openDatePicker() }
        binding.calendarView.setOnDayClickListener(this)

        createNotificationChannel()


    }

    private fun createNotificationChannel() {
        val notif_name = "Notification channel"
        val desc = "Description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelID, notif_name, importance)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
//find a way to display notifcation a day before the event

    }

    private fun openDatePicker() {
        DatePickerBuilder(this, this)
            .pickerType(ONE_DAY_PICKER)
            .headerColor(R.color.black)
            .todayLabelColor(R.color.lblue)
            .selectionColor(R.color.blue)
            .dialogButtonsColor(com.applandeo.materialcalendarview.R.color.material_blue_grey_800)
            .build()
            .show()
    }


    override fun onDayClick(eventDay: EventDay) {

        fire_db.collection("csi-dmce-c6f11").document("events")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val timestamp: Timestamp = document.getTimestamp("title-unix-timestamp")!!
                    val appDate = timestamp.toDate()
                    appDate.add(EventDay(calendar, R.drawable.dot))
                } else {
                    return@addOnSuccessListener
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error while fetching document:", exception)

            }

    }

    override fun onSelect(calendar: List<Calendar>) {
        // val intent = Intent(this, ::class.java)
        //direct to the event page for that date


    }






    }


    





