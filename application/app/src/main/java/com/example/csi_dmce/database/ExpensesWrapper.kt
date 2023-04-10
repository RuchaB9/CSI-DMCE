package com.example.csi_dmce.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

data class Expenses(
    @DocumentId
    var associated_event           :String?    =null,
    var date_of_event              :String?    =null,
    var description_of_event       :String?    =null,
    var total_cost                 :String?    =null,
    var upi_id                     :String?    =null
)

class ExpensesWrapper {
    companion object{
        private val expenseCollectionRef = FirebaseFirestore.getInstance().collection("expenses")

        private fun getExpensesDocument(expenseCollectionRef: CollectionReference, expenseID: String): DocumentReference {
            return expenseCollectionRef.document(expenseID)
        }

        suspend fun getExpensesObject(expenseID: String): Expenses? {
            val expenseDocuments = expenseCollectionRef.get().await()
            for(expenseDocument in expenseDocuments){
                return expenseDocument.toObject(Expenses::class.java)
            }
            return null
        }
    }
}