package com.example.csi_dmce.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.*

class CsvWriter {
    companion object {
        @RequiresApi(Build.VERSION_CODES.R)
        fun writeCsv(ctx: Context, headerData: List<String>, csvData: List<List<String>>) {
            val fileObject =  File(Environment.getExternalStorageDirectory(), "attendance.csv")
            Log.d("CSV_WRITER", fileObject.absolutePath)
            if (fileObject.exists()) {
                Log.d("CSV_WRITER", "FILE_EXISTS")
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