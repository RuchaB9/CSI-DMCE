package com.example.csi_dmce.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class Helpers {
    companion object {
        /**
         * Returns a SHA-256 Hash of a given string.
         *
         * @param plaintext the string to be hashed.
         * @return the SHA-256 hash of `plaintext` in hexadecimal representation.
         */
        fun getSha256Hash(plaintext: String): String {
            val shaInstancce = MessageDigest.getInstance("SHA-256")
            val bigInt = BigInteger(1, shaInstancce.digest(plaintext.toByteArray(Charsets.UTF_8)))
            return String.format("%032x", bigInt)
        }

        /**
         * Creates an Event ID from an event title and its date.
         * The title is shortened for the ID. If the title contains one word, then we taker alternate
         * characters. If the title has multiple words, we consider the first two letters of every
         * word in the title.
         *
         *
         * @param title the title of the event
         * @param eventTimestamp the unix timestamp of the event.
         * @return the event ID as a String, which has the format `title-timestamp`.
         */
        fun createEventId(eventTitle: String, eventTimestamp: Long): String {
            val tokens = eventTitle.split(" ")
            return if (tokens.size == 1) {
                val titleToken = eventTitle.filterIndexed { index, _ ->
                    index % 2 == 0
                }
                "$titleToken-$eventTimestamp"
            } else {
                val titleToken = tokens.joinToString("") { it.take(2) }
                "$titleToken-$eventTimestamp"
            }
        }

        /**
         * Generate a UNIX timestamp of a given `Date` instance.
         *
         * @param date the formatted date
         * @return the UNIX timestamp
         */
        fun generateUnixTimestampFromDate(date: Date): Long {
            return (date.time / 1000)
        }

        fun getAcademicYear(admission_year: Int): String {
            return when (Calendar.getInstance().get(Calendar.YEAR) - admission_year) {
                4 -> "BE"
                3 -> "TE"
                2 -> "SE"
                1 -> "FE"
                else -> "UNKNOWN"
            }
        }


    }
}