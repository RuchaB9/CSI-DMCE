package com.example.csi_dmce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.identity.AccessControlProfileId
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class event_recycler : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList: ArrayList<events>
    lateinit var imageId: Array<Int>
    lateinit var heading : Array<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_recycler)


        imageId = arrayOf(

            R.drawable.github_logo,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e,
            R.drawable.f,
            R.drawable.g

        )

        heading = arrayOf(
            "Git And Github",
            "Hactoberfest",
            "Web Development",
            "App Development",
            "Blockchain Technology",
            "Flutter and Dart",
            "Workshop"
        )
        newRecyclerView=findViewById(R.id.recylerview)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        newArrayList = arrayListOf<events>()
        getUserdata()

    }

    private fun getUserdata() {
        for (i in imageId.indices){

            val events = events(imageId[i],heading[i])
            newArrayList.add(events)

        }

        newRecyclerView.adapter = EventAdapter(newArrayList)
    }
}
