package com.example.csi_dmce.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.csi_dmce.R

class AdminAttendanceView : AppCompatActivity() {

    //add this to events page

    private lateinit var view_qr : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_attendance)

        //val eventID =

        //val intent1 = Intent(applicationContext, AttendanceActivity::class.java)
        //intent1.putExtra("event_ID", eventID )

        view_qr = findViewById(R.id.view_qr)
        view_qr.setOnClickListener {
            val intent = Intent(this, AdminViewQR::class.java)
            startActivity(intent)
        }
    }

}