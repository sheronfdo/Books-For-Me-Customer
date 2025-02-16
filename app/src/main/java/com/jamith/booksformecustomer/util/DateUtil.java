package com.jamith.booksformecustomer.util;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DateUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    /**
     * Get the current timestamp in Firestore's Timestamp format.
     *
     * @return Firestore Timestamp for the current time.
     */
    public static Timestamp getCurrentFirestoreTimestamp() {
        return Timestamp.now();
    }

    /**
     * Convert Firestore Timestamp to a java.util.Date object.
     *
     * @return java.util.Date representation from Firebase TimeStamp.
     */
    public static Date fromFirestoreTimestamp() {

        return getCurrentFirestoreTimestamp().toDate();
    }

    /**
     * Convert Firestore Timestamp to a java.util.Date object.
     *
     * @param firestoreTimestamp Firestore Timestamp.
     * @return java.util.Date representation.
     */
    public static Date fromFirestoreTimestamp(Timestamp firestoreTimestamp) {
        if (firestoreTimestamp == null) {
            return null;
        }
        return firestoreTimestamp.toDate();
    }

    /**
     * Convert a java.util.Date object to Firestore's Timestamp.
     *
     * @param date java.util.Date object.
     * @return Firestore Timestamp representation.
     */
    public static Timestamp toFirestoreTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date);
    }

    /**
     * Format a given date to the default ISO 8601 format.
     *
     * @param date java.util.Date object.
     * @return String formatted date.
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }

    /**
     * Parse a date string in the default format into a java.util.Date object.
     *
     * @param dateString String representation of the date.
     * @return java.util.Date object or null if parsing fails.
     */
    public static Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the current date as a formatted string.
     *
     * @return String representation of the current date.
     */
    public static String getCurrentDateFormatted() {
        return formatDate(new Date());
    }


    /**
     * Convert a Firestore Timestamp to java.time.Instant.
     *
     * @param firestoreTimestamp Firestore Timestamp.
     * @return Instant representation.
     */
    public static Instant toInstant(Timestamp firestoreTimestamp) {
        return firestoreTimestamp != null ? firestoreTimestamp.toDate().toInstant() : null;
    }


}
