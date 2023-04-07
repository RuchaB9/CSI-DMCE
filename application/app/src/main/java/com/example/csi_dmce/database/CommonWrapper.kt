package com.example.csi_dmce.database

import kotlinx.coroutines.tasks.await

/**
 * Database wrappers which are common to all modules, utility wise.
 */
class CommonWrapper {
    companion object {
        /**
         * Add an attendee to an event. This is included in the common wrappers since we need the
         * `student_id` in the event's attendees array and the `event_id` in the student's events
         * array, thus making this bidirectional.
         *
         * @param event the event, which the student has registered for.
         * @param student the student, who is registering for the event.
         */
        suspend fun addAttendee(event: Event, student: Student) {
            if (event.attendees == null) {
                event.attendees = mutableListOf(student.student_id!!)
            } else {
                event.attendees!!.add(student.student_id!!)
            }

            if (student.events == null) {
                student.events = mutableListOf(event.eventId)
            } else {
                student.events!!.add(event.eventId!!)
            }

            StudentWrapper.updateStudent(student, student)
            EventWrapper.updateEvent(event, event)
        }

        suspend fun addRegistrant(event: Event, studentId : String) {
            if (event.registrants == null) {
                event.registrants = mutableListOf(studentId)
            } else {
                event.registrants!!.add(studentId)
            }
            EventWrapper.updateEvent(event, event)
        }
    }
}