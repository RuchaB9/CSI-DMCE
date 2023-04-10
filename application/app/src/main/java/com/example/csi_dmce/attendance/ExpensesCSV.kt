package com.example.csi_dmce.attendance

import android.content.Context
import com.example.csi_dmce.database.*
import com.example.csi_dmce.utils.CsvWriter
import kotlinx.coroutines.runBlocking
import java.util.*

class ExpensesCSV {
    companion object {
        fun writeExpensesData(ctx: Context, expenseID: String) {
            // The data that will be written to the CSV file.
            val csvData: MutableList<List<String>> = mutableListOf()

            val expenseObject: Expenses = runBlocking { ExpensesWrapper.getExpensesObject(expenseID)!! }

            val expenseData: List<String> = listOf(
                    expenseObject.associated_event!!,
                    expenseObject.date_of_event!!,
                    expenseObject.description_of_event!!,
                    expenseObject.total_cost!!,
                    expenseObject.upi_id!!
                )

            csvData.add(expenseData)


            val csvHeader: List<String> = listOf(
                "Event",
                "Event date",
                "Event description",
                "Total cost",
                "UPI ID"
            )

            CsvWriter.writeCsv(ctx, csvHeader, Collections.unmodifiableList(csvData))
        }
    }
}