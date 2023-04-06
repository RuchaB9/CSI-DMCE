package com.example.csi_dmce.attendance

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.csi_dmce.database.Event
import com.example.csi_dmce.database.EventWrapper
import com.example.csi_dmce.database.Student
import com.example.csi_dmce.database.StudentWrapper
import com.example.csi_dmce.utils.CsvWriter
import kotlinx.coroutines.runBlocking
import java.util.*

class AttendanceExportService {
    companion object {
        fun writeAttendanceData(ctx: Context, eventId: String) {
            // The data that will be written to the CSV file.
            val csvData: MutableList<List<String>> = mutableListOf()

            val eventObject: Event = runBlocking { EventWrapper.getEvent(eventId)!! }
            val attendees: MutableList<String>  = eventObject.attendees!!

            for (attendee in attendees) {
                val attendeeObject: Student = runBlocking { StudentWrapper.getStudent(attendee)!! }
                val attendeeData: List<String> = listOf(
                    attendeeObject.name!!,
                    attendeeObject.academic_year!!,
                    attendeeObject.department!!,
                    attendeeObject.division!!,
                    attendeeObject.roll_number.toString()
                )

                csvData.add(attendeeData)
            }

            val csvHeader: List<String> = listOf(
                "Name",
                "Academic Year",
                "Department",
                "Division",
                "Roll number"
            )

            CsvWriter.writeCsv(ctx, csvHeader, Collections.unmodifiableList(csvData))
        }
    }
}