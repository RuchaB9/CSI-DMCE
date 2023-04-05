package com.example.csi_dmce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.csi_dmce.R
import com.example.csi_dmce.utils.CsvWriter
import com.example.csi_dmce.auth.CsiAuthWrapper
import com.example.csi_dmce.events.EventPageActivity
import com.example.csi_dmce.profile.Profile


class Dashboard: AppCompatActivity() {
    private lateinit var btn_profile: Button
    private lateinit var btn_events: Button
    private lateinit var btn_logout: Button
    private lateinit var btnCsv: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        btn_profile = findViewById(R.id.btn_dashboard_profile)
        btn_profile.setOnClickListener {
            val eventIntent = Intent(this, Profile::class.java)
            startActivity(eventIntent)
        }
        btnCsv = findViewById(R.id.testwrappers)
        btnCsv.setOnClickListener {
            val eventIntent = Intent(this, CsvWriter::class.java)
            startActivity(eventIntent)
        }

        btn_events = findViewById(R.id.btn_dashboard_events)
        btn_events.setOnClickListener {
            val eventIntent = Intent(this, EventPageActivity::class.java)
            startActivity(eventIntent)
        }

        btn_logout = findViewById(R.id.btn_dashboard_logout)
        btn_logout.setOnClickListener {
            CsiAuthWrapper.deleteAuthToken(applicationContext)
            val intent = Intent(applicationContext, WelcomeActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }
}