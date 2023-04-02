package com.example.csi_dmce.attendance

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.csi_dmce.R
import com.example.csi_dmce.database.EventWrapper
import com.example.csi_dmce.database.StudentWrapper
import kotlinx.coroutines.runBlocking
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.*

class AttendanceCsv: AppCompatActivity() {

    private lateinit var generateCsv: Button
    private lateinit var eventName : EditText

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.generate_csv)

        generateCsv = findViewById(R.id.generateCSV)

        eventName = findViewById(R.id.event_name)

        generateCsv.setOnClickListener {

            try {

                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED
                )
                val CREATE_FILE = 1

                fun createFile(pickerInitialUri: Uri) {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/csv"
                        putExtra(Intent.EXTRA_TITLE, "attendance.csv")

                        putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                    }
                    startActivityForResult(intent, CREATE_FILE)
                }


                val sm =  getSystemService(Context.STORAGE_SERVICE) as StorageManager
                intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
                val startDir = "Documents"
                // get system root uri
                var uriroot = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
                var scheme = uriroot.toString()
                Log.d("Debug", "INITIAL_URI scheme: $scheme")
                //change uri to Documents folder
                scheme = scheme.replace("/root/", "/document/")
                scheme += "%3A$startDir"
                uriroot = Uri.parse(scheme)
                // give changed uri to Intent
                intent.putExtra("android.provider.extra.INITIAL_URI", uriroot)
                Log.d("Debug", "uri: $uriroot")

                createFile(uriroot)

                val eventForCsv = eventName.text.toString()
                val newFile =  File("/Internal storage/Documents/attendance.csv")
                val writer = newFile.bufferedWriter()
                val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("Name", "Academic Year", "Department", "Division", "Roll no."))
                runBlocking {
                    val eventAttendees = EventWrapper.getEvent(eventForCsv)?.attendees
                    for(studentAttendee in eventAttendees!!){
                        val studentCsv = StudentWrapper.getStudent(studentAttendee)
                        val row=listOf(
                            studentCsv!!.name,
                            studentCsv.academic_year,
                            studentCsv.department,
                            studentCsv.division,
                            studentCsv.roll_number
                        )
                        csvPrinter.printRecords(row)

                        //alternate method (skip apache library)

               /**         newFile.bufferedWriter().use { out ->
                            out.write("$row , \n")
                        } **/

                    }
                }
                csvPrinter.flush()
                csvPrinter.close()


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}