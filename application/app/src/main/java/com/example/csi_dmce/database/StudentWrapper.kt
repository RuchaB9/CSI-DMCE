package com.example.csi_dmce.database

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

data class Student(
    @DocumentId
    var student_id          : String?                   = null,
    val academic_year       : String?                   = null,
    val department          : String?                   = null,
    val division            : String?                   = null,
    val email               : String?                   = null,
    var email_id_verified   : Boolean?                  = null,
    val name                : String?                   = null,
    val phone_number        : Long?                     = null,
    val roll_number         : Int?                      = null,
    var events              : MutableList<String?>?     = null,
    val is_superuser        : Boolean?                  = null,
    val avatar_extension    : String?                   = null,
)

class StudentWrapper {
    companion object {
        private val studentCollectionRef = FirebaseFirestore.getInstance().collection("users")
        private val storageRef = FirebaseStorage.getInstance().reference.child("users")

        private fun getStudentDocument(studentCollectionRef: CollectionReference, studentId: String): DocumentReference {
            return studentCollectionRef.document(studentId)
        }

        /**
         * Add a student to the database.
         *
         * @param student the deserialized Student instance.
         * @return void
         */
        suspend fun addStudent(student: Student): Void? {
            val studentDocumentRef = studentCollectionRef.document(student.student_id!!)
            return studentDocumentRef.set(student).await()
        }

        /**
         * Read a student from the database.
         *
         * @param studentId the student ID of the student to be read.
         * @return the deserialized Student instance.
         */
        suspend fun getStudent(studentId: String): Student? {
            val studentDocument = getStudentDocument(studentCollectionRef, studentId).get().await()
            Log.d("STUDENT", studentDocument.reference.path.toString())
            return studentDocument.toObject(Student::class.java)!!
        }

        /**
         * Update a student in the database.
         *
         * @param oldStudent the student whose data is to be updated.
         * @param newStudent the new student instance containing updated details.
         */
        suspend fun updateStudent(oldStudent: Student, newStudent: Student) {
            // If the student IDs are different, then we have to delete the old document,
            if (oldStudent.student_id != newStudent.student_id) {
                getStudentDocument(studentCollectionRef, oldStudent.student_id!!).delete()
            }

            // And a add a new one
            addStudent(newStudent)
        }

        /**
         * Delete a student from the database.
         *
         * @param student the deserialized Student instance to be deleted.
         */
        fun deleteStudent(student: Student) {
            getStudentDocument(studentCollectionRef, student.student_id!!).delete()
        }

        suspend fun getStudentByEmail(email: String): Student? {
            val studentDocuments = studentCollectionRef
                .get()
                .await()

            for (studentDocument in studentDocuments) {
                if (studentDocument.get("email") == email) {
                    return studentDocument.toObject(Student::class.java)
                }
            }
            return null
        }

        suspend fun setStudentEmailIdVerificationStatus(studentId: String, emailIdVerified: Boolean) {
            val studentDocument = getStudentDocument(studentCollectionRef, studentId).get().await()
            Log.d("DB_EMAIL_VERIF_WRAPPER", studentDocument.toString())
            val studentObject: Student = studentDocument.toObject(Student::class.java)!!
            studentObject.email_id_verified = emailIdVerified
            studentCollectionRef.document(studentId).set(studentObject)
        }

        /*
        Storage stuff below this
         */
        fun getStudentAvatarUrl(studentId: String, avatarExtension: String, callback: (String?) -> Unit) {
            storageRef
                .child("${studentId}/avatar.${avatarExtension}")
                .downloadUrl
                .addOnSuccessListener {
                    callback(it.toString())
                }.addOnFailureListener {
                    callback(null)
                }
        }
    }
}