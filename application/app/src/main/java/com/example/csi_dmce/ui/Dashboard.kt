package com.example.csi_dmce.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.csi_dmce.R
import com.example.csi_dmce.auth.CsiAuthWrapper
import com.example.csi_dmce.events.EventPageActivity
import com.example.csi_dmce.profile.Profile


class Dashboard: AppCompatActivity() {
    private lateinit var btn_profile: Button
    private lateinit var btn_events: Button
    private lateinit var btn_logout: Button
    private lateinit var btnCsv: Button


    @RequiresApi(Build.VERSION_CODES.R)
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
            val packageName = packageName
            val pm = packageManager

            if (pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName) == PackageManager.PERMISSION_GRANTED) {
                // The MANAGE_EXTERNAL_STORAGE permission has already been granted.
                // Your app can access all files on the external storage.
                Log.d("CSI_PERMS", "ALREADY GRANTED")
            } else {
                Log.d("CSI_PERMS", "REQUESTING")
                // The MANAGE_EXTERNAL_STORAGE permission has not been granted.
                // Your app needs to request this permission.
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }



            // AttendanceExportService.writeAttendanceData(applicationContext, "CSCT-1678280388")
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