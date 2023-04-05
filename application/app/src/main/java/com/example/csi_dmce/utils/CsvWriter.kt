package com.example.csi_dmce.utils

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.csi_dmce.R
import com.example.csi_dmce.database.EventWrapper
import com.example.csi_dmce.database.StudentWrapper
import kotlinx.coroutines.runBlocking
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.*

class CsvWriter {
    companion object {
        fun writeCsv(headerData: List<String>, csvData: List<List<String>>) {
            val fileObject =  File(Environment.getExternalStorageDirectory(), "attendance.csv")
            if (fileObject.exists()) {
                fileObject.delete()
            }

            val fileCreated: Boolean = fileObject.createNewFile()
            if (!fileCreated) {
                throw IOException("Could not create CSV file!")
            }

            val writer: BufferedWriter = fileObject.bufferedWriter()
            val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader(*headerData.toTypedArray())
            )

            csvPrinter.printRecords(csvData)

            csvPrinter.flush()
            csvPrinter.close()
        }
    }
}