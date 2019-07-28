package guepardoapps.mynote.database.note

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import guepardoapps.mynote.model.Note

// Helpful
// https://developer.android.com/training/data-storage/sqlite
// https://www.techotopia.com/index.php/A_Kotlin_Android_SQLite_Database_Tutorial
// https://github.com/cbeust/kotlin-android-example/blob/master/app/src/main/kotlin/com/beust/example/DbHelper.kt

internal class DbNote(context: Context)
    : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {

    override fun onCreate(database: SQLiteDatabase) {
        val createTable = (
                "CREATE TABLE IF NOT EXISTS $DatabaseTable"
                        + "("
                        + "$ColumnId TEXT PRIMARY KEY,"
                        + "$ColumnTitle TEXT NOT NULL,"
                        + "$ColumnContent TEXT NOT NULL,"
                        + "$ColumnYear  INTEGER,"
                        + "$ColumnMonth  INTEGER,"
                        + "$ColumnHour  INTEGER,"
                        + "$ColumnDay  INTEGER,"
                        + "$ColumnMinute  INTEGER,"
                        + "$ColumnSecond  INTEGER"
                        + ")")
        database.execSQL(createTable)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) = onUpgrade(database, oldVersion, newVersion)

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS $DatabaseTable")
        onCreate(database)
    }

    fun add(note: Note): Long = this.writableDatabase
            .insert(DatabaseTable, null, ContentValues()
                    .apply {
                        put(ColumnId, note.id)
                        put(ColumnTitle, note.title)
                        put(ColumnContent, note.content)
                        put(ColumnYear, note.year)
                        put(ColumnMonth, note.month)
                        put(ColumnDay, note.day)
                        put(ColumnHour, note.hour)
                        put(ColumnMinute, note.minute)
                        put(ColumnSecond, note.second)
                    })

    fun delete(id: String): Int = this.writableDatabase.delete(DatabaseTable, "$ColumnId LIKE ?", arrayOf(id))

    fun get(): MutableList<Note> {
        val cursor = this.readableDatabase.query(
                DatabaseTable, arrayOf(ColumnId, ColumnTitle, ColumnContent, ColumnYear, ColumnMonth, ColumnDay, ColumnHour, ColumnMinute, ColumnSecond),
                null, null, null, null, "$ColumnId ASC")

        val list = mutableListOf<Note>()
        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow(ColumnId))

                val title = getString(getColumnIndexOrThrow(ColumnTitle))
                val content = getString(getColumnIndexOrThrow(ColumnContent))

                val year = getInt(getColumnIndexOrThrow(ColumnYear))
                val month = getInt(getColumnIndexOrThrow(ColumnMonth))
                val day = getInt(getColumnIndexOrThrow(ColumnDay))

                val hour = getInt(getColumnIndexOrThrow(ColumnHour))
                val minute = getInt(getColumnIndexOrThrow(ColumnMinute))
                val second = getInt(getColumnIndexOrThrow(ColumnSecond))

                list.add(Note(id, title, content, year, month, day, hour, minute, second))
            }
        }

        return list
    }

    fun update(note: Note): Int = this.writableDatabase
            .update(DatabaseTable, ContentValues()
                    .apply {
                        put(ColumnTitle, note.title)
                        put(ColumnContent, note.content)
                        put(ColumnYear, note.year)
                        put(ColumnMonth, note.month)
                        put(ColumnDay, note.day)
                        put(ColumnHour, note.hour)
                        put(ColumnMinute, note.minute)
                        put(ColumnSecond, note.second)
                    }, "$ColumnId LIKE ?", arrayOf(note.id))

    companion object {
        private const val DatabaseVersion = 1
        private const val DatabaseName = "guepardoapps-mynote-note-2.db"
        private const val DatabaseTable = "noteTable"

        private const val ColumnId = "_id"
        private const val ColumnTitle = "_title"
        private const val ColumnContent = "_content"
        private const val ColumnYear = "_year"
        private const val ColumnMonth = "_month"
        private const val ColumnDay = "_day"
        private const val ColumnHour = "_hour"
        private const val ColumnMinute = "_minute"
        private const val ColumnSecond = "_second"
    }
}