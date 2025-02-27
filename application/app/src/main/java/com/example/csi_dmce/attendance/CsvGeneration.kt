package com.example.csi_dmce.attendance
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.csi_dmce.R

class CsvGeneration: AppCompatActivity() {

    val ctx = this

    private lateinit var attendanceCard : CardView
    private lateinit var expenseCard : CardView
    private lateinit var registrantCard : CardView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.csv_generation)

        attendanceCard = findViewById(R.id.card_view_attendance_report)
        expenseCard = findViewById(R.id.card_view_expenses_report)
        registrantCard = findViewById(R.id.card_view_registrations_report)

        val popupView = layoutInflater.inflate(R.layout.csv_popup, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        val editText = popupView.findViewById<EditText>(R.id.eventID)
        val submitButton = popupView.findViewById<Button>(R.id.submit_button)

        attendanceCard.setOnClickListener {
            popupWindow.showAtLocation(attendanceCard, Gravity.CENTER, 0, 0)

            submitButton.setOnClickListener {
                val eventForCsv = editText.text.toString()
                AttendanceExportService.writeAttendanceData(ctx, eventForCsv)
            }
        }

        expenseCard.setOnClickListener{
            ExpensesCSV.writeExpensesData(ctx)
        }

        registrantCard.setOnClickListener{
            popupWindow.showAtLocation(registrantCard, Gravity.CENTER, 0, 0)


            submitButton.setOnClickListener {
                val eventForCsv = editText.text.toString()
                RegistrantsCsv.writeRegistrantsData(ctx, eventForCsv)
            }
        }


    }
}