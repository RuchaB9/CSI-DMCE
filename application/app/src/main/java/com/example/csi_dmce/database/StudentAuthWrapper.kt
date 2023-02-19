package com.example.csi_dmce.database

import android.util.Log
import com.example.csi_dmce.utils.Helpers
import com.google.firebase.firestore.*
import com.google.rpc.Help
import kotlinx.coroutines.tasks.await
import java.time.Instant.now
import java.util.*
import kotlin.collections.HashMap

data class StudentAuth(
    @DocumentId
    val email: String?                              = null,
    var password_hash: String?                      = null,
    var email_verification: HashMap<String, Any>?   = null,
    val forgot_password: HashMap<String, Any>?      = null
)

class StudentAuthWrapper {
    companion object {
        private val authCollectionRef = FirebaseFirestore.getInstance().collection("auth")
        suspend fun getByEmail(email: String): StudentAuth? {
            val studentDocument = authCollectionRef
                .document(email)
                .get()
                .await()

            return studentDocument.toObject(StudentAuth::class.java)
        }

        suspend fun addStudentAuth(studentAuth: StudentAuth, otp: String): String {
            val creationDate: Date = Date()
            val expiryDate: Date = Date(creationDate.time + Helpers.DAY_IN_MS)

            val emailVerificationMap = hashMapOf<String, Any>()
            emailVerificationMap["creation_timestamp"] = Helpers.generateUnixTimestampFromDate(creationDate)
            emailVerificationMap["expiry_timestamp"] = Helpers.generateUnixTimestampFromDate(expiryDate)
            emailVerificationMap["otp"] = otp
            emailVerificationMap["verification_status"] = "unverified"


            studentAuth.email_verification = emailVerificationMap
            authCollectionRef
                .document(studentAuth.email!!)
                .set(studentAuth)
                .await()

            return emailVerificationMap["otp"].toString()
        }

        /**
         * Given an email and a SHA-256 password hash, check if the credentials match with the
         * ones in the database.
         *
         * @param email the email of the student.
         * @param passwordHash the SHA-256 of the password entered by the user.
         * @return a `Student` object if the student authenticates with valid credentials.
         */
        suspend fun checkStudentCredentials(email: String, passwordHash: String, callback: (Student?) -> Unit) {
            val student: StudentAuth? = getByEmail(email)
            if (student?.password_hash == passwordHash) {
                return callback(StudentWrapper.getStudentByEmail(email))
            }

            return callback(null)
        }
        suspend fun EmailVerificationWrapper(emailId: String): HashMap<String, Any> {
            val studentAuthRef: DocumentSnapshot? = authCollectionRef
                .document(emailId)
                .get().await()

            val studentAuthObject = studentAuthRef!!.toObject(StudentAuth::class.java)!!
            return studentAuthObject.email_verification!!
        }

        suspend fun setEmailVerificationStatus(emailId: String, verificationStatus: String) {
            val studentAuthRef: DocumentSnapshot? = authCollectionRef
                .document(emailId)
                .get().await()

            val studentAuthObject = studentAuthRef!!.toObject(StudentAuth::class.java)!!
            studentAuthObject.email_verification?.set("verification_status", verificationStatus)
            authCollectionRef.document(emailId).set(studentAuthObject).await()
        }

        suspend fun forgotPasswordWrapper(emailId: String): HashMap<String, Any> {
            val studentAuthRef: DocumentSnapshot? = authCollectionRef
                .document(emailId)
                .get().await()

            val studentAuthObject = studentAuthRef!!.toObject(StudentAuth::class.java)!!
            return studentAuthObject.forgot_password!!
        }

        suspend fun setPasswordWrapper(emailId: String, passwordHash: String) {
            val studentAuthRef: DocumentSnapshot? = authCollectionRef
                .document(emailId)
                .get().await()

            val studentAuthObject = studentAuthRef!!.toObject(StudentAuth::class.java)!!
            studentAuthObject.password_hash = passwordHash
            authCollectionRef.document(emailId).set(studentAuthObject).await()
        }
    }
}