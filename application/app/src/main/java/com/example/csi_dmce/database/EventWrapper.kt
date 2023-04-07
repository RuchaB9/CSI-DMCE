package com.example.csi_dmce.database

import android.annotation.SuppressLint
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import com.example.csi_dmce.R

import com.example.csi_dmce.utils.Helpers

data class Event (
    @DocumentId
    var eventId     : String?       = null,
    var title       : String?       = null,
    var venue       : String?       = null,
    var datetime    : Long?         = null,
    var description : String?       = null,
    var poster_url  : String?       = null,
    var attendees   : MutableList<String>? = null,
    var uuid        : String?       =null,
    var registrants : MutableList<String>? = null
)

class EventWrapper {
    companion object {
        private val eventsCollectionRef = FirebaseFirestore.getInstance().collection("events")

        private fun getEventDocument(eventCollectionRef: CollectionReference, eventId: String): DocumentReference {
            return eventCollectionRef.document(eventId)
        }

        /**
         * Adds an event into Firebase. While adding an event, you should provide the `eventId` as
         * null or an empty string. The wrapper creates an ID anyway.
         *
         * @param event the deserialized event
         * @return void
         */
        suspend fun addEvent(event: Event): Void? {
            event.eventId = Helpers.createEventId(event.title!!, event.datetime!!)
            val eventDocumentRef = eventsCollectionRef.document(event.eventId!!)
            return eventDocumentRef.set(event).await()
        }

        /**
         * Read an event from the database.
         *
         * @param eventId the ID of the event.
         * @return the deserialized `Event` data class
         */
        suspend fun getEvent(eventId: String): Event? {
            val eventDocument = getEventDocument(eventsCollectionRef, eventId).get().await()
            return eventDocument.toObject(Event::class.java)!!
        }

        /**
         * Update an event in the database.
         *
         * @param oldEvent the old event, which is to be updated.
         * @param newEvent the new event, which is to be stored.
         */
        suspend fun updateEvent(oldEvent: Event, newEvent: Event) {
            // If the event IDs are equal, then we have to delete the old document. This can
            // happen when and if the event's time or title is changed.
            if (newEvent.eventId != null && oldEvent.eventId == newEvent.eventId) {
                getEventDocument(eventsCollectionRef, oldEvent.eventId!!).delete()
            }

            // And a add a new event.
            addEvent(newEvent)
        }

        /**
         * Delete an event from the database.
         *
         * @param event the event to be deleted.
         */
        fun deleteEvent(event: Event) {
            getEventDocument(eventsCollectionRef, event.eventId!!).delete()
        }

        suspend fun getEventUUID(eventId: String) : String? {
            val eventDocument= eventsCollectionRef
                .document(eventId)
                .get()
                .await()
            return eventDocument.getString("uuid")
        }

    }
}