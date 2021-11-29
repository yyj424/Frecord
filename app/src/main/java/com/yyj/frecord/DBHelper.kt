package com.yyj.frecord

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "Frecord.db"
        const val DB_VERSION = 1

        const val REC_TABLE = "Record"
        const val REC_COL_ID = "_id"
        const val REC_COL_SCORE = "score"
        const val REC_COL_TITLE = "title"
        const val REC_COL_CONTENT = "content"
        const val REC_COL_DATE = "date"
        const val REC_COL_LOCK = "lock"
        const val REC_COL_SIMPLE = "simple"
        const val REC_COL_WHR = "whr"
        const val REC_COL_WHAT = "what"
        const val REC_COL_FEELING = "feeling"
        const val REC_COL_WHY = "why"

        const val MSG_TABLE = "Message"
        const val MSG_COL_ID = "_id"
        const val MSG_COL_CONTENT = "content"
        const val MSG_COL_DATE = "date"
        const val MSG_COL_REQ = "req"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var createTable = "CREATE TABLE $REC_TABLE " +
                "($REC_COL_ID INTEGER PRIMARY KEY, " +
                "$REC_COL_SCORE INTEGER, " +
                "$REC_COL_TITLE TEXT, " +
                "$REC_COL_CONTENT TEXT, " +
                "$REC_COL_DATE INTEGER, " +
                "$REC_COL_LOCK INTEGER, " +
                "$REC_COL_SIMPLE INTEGER, " +
                "$REC_COL_WHR TEXT, " +
                "$REC_COL_WHAT TEXT, " +
                "$REC_COL_FEELING TEXT, " +
                "$REC_COL_WHY TEXT);"
        db?.execSQL(createTable)

        createTable = "CREATE TABLE $MSG_TABLE " +
                "($MSG_COL_ID INTEGER PRIMARY KEY, " +
                "$MSG_COL_CONTENT TEXT, " +
                "$MSG_COL_DATE INTEGER, " +
                "$MSG_COL_REQ INTEGER);"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $REC_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $MSG_TABLE")
        onCreate(db)
    }
}