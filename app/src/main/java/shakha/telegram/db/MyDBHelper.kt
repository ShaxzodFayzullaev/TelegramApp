package shakha.telegram.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.BoringLayout
import shakha.telegram.models.Profile

import java.util.*
import kotlin.collections.ArrayList

class MyDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    MyDbInterface {

    companion object {
        const val DB_NAME = "db_name"
        const val DB_VERSION = 1

        const val DB_TABLE = "Info_Table"
        const val INFO_ID = "id"
        const val INFO_NAME = "name"
        const val INFO_INFO = "info"
        const val INFO_IMG = "img"
        const val INFO_DATE = "date"
        const val ASC = "ASC"
        const val DESC = "DESC"
        const val key_name = "$INFO_NAME"
        const val key_data = "$INFO_DATE"

    }

    @SuppressLint("Range")
    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "create table $DB_TABLE($INFO_ID integer not null primary key autoincrement unique , $INFO_NAME text not null , $INFO_INFO text not null , $INFO_IMG text not null , $INFO_DATE text not null )"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DB_TABLE")
        onCreate(db)
    }

    override fun addPro(profile: Profile) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INFO_NAME, profile.name)
        contentValues.put(INFO_INFO, profile.info)
        contentValues.put(INFO_IMG, profile.img)
        contentValues.put(INFO_DATE, profile.date)
        database.insert(DB_TABLE, null, contentValues)
        database.close()
    }

    @SuppressLint("Recycle", "SimpleDateFormat")
    override fun getAllPro(): ArrayList<Profile> {
        val list = ArrayList<Profile>()
        val query = "select * from $DB_TABLE"
        val database = this.readableDatabase
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Profile(
                        id = cursor.getInt(0),
                        name = cursor.getString(1),
                        info = cursor.getString(2),
                        img = cursor.getString(3),
                        date = cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        return list
    }

    override fun editPro(profile: Profile) :Boolean {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INFO_ID, profile.id)
        contentValues.put(INFO_NAME, profile.name)
        contentValues.put(INFO_INFO, profile.info)
        contentValues.put(INFO_IMG, profile.img)
        contentValues.put(INFO_DATE, profile.date)
       val inn= database.update(DB_TABLE, contentValues, "$INFO_ID=?", arrayOf(profile.id.toString()))
        database.close()
        return inn>0
    }

    override fun deletePro(profile: Profile) : Boolean {
        val database = this.writableDatabase
        var salom =database.delete(DB_TABLE, "$INFO_ID=?", arrayOf(profile.id.toString()))
        database.close()
        return salom>0
    }

    @SuppressLint("Range")
    fun getItems(order: String, date: String): List<Profile> {
        val itemsList: ArrayList<Profile> = ArrayList<Profile>()
        val db = this.readableDatabase
        val query = "SELECT  * FROM $DB_TABLE ORDER BY $date $order "
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            return ArrayList()
        }

        var id: Int
        var name: String
        var date: String
        var info: String
        var img: String
        if (cursor.moveToNext()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(INFO_ID))
                name = cursor.getString(cursor.getColumnIndex(INFO_NAME))
                info = cursor.getString(cursor.getColumnIndex(INFO_INFO))
                img = cursor.getString(cursor.getColumnIndex(INFO_IMG))
                date = cursor.getString(cursor.getColumnIndex(INFO_DATE))
                val item = Profile(id = id, name = name, info = info, img = img, date = date)
                itemsList.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return itemsList
    }

//    @SuppressLint("Range")
//    fun getItemsByDateDesc(): ArrayList<Profile> {
//        val itemList = ArrayList<Profile>()
//        val db = this.readableDatabase
//        val selectQuery = "SELECT * FROM $DB_TABLE ORDER BY $INFO_DATE DESC "
//        val cursor = db.rawQuery(selectQuery, null)
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getInt(cursor.getColumnIndex(INFO_ID))
//                val name = cursor.getString(cursor.getColumnIndex(INFO_NAME))
//                val date = cursor.getString(cursor.getColumnIndex(INFO_DATE))
//                val image = cursor.getString(cursor.getColumnIndex(INFO_IMG))
//                val info = cursor.getString(cursor.getColumnIndex(INFO_INFO))
//                val item = Profile(id, name, info, image, date)
//                itemList.add(item)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return itemList
//    }
//
//    @SuppressLint("Range")
//    fun getItemsByNameUP(): List<Profile> {
//        val items = ArrayList<Profile>()
//        val db = writableDatabase
//        val query = "SELECT * FROM $DB_TABLE ORDER BY $INFO_NAME ASC"
//        val cursor = db.rawQuery(query, null)
//        while (cursor.moveToNext()) {
//            val id = cursor.getLong(cursor.getColumnIndex(INFO_ID))
//            val name = cursor.getString(cursor.getColumnIndex(INFO_NAME))
//            val info = cursor.getString(cursor.getColumnIndex(INFO_INFO))
//            val img = cursor.getString(cursor.getColumnIndex(INFO_IMG))
//            val date = cursor.getString(cursor.getColumnIndex(INFO_DATE))
//            val item = Profile(null, name, info, img, date)
//            items.add(item)
//        }
//        cursor.close()
//        return items
//    }
//    @SuppressLint("Range")
//    fun getItemsByNameDown():List<Profile> {
//        val itemList = ArrayList<Profile>()
//        val db = this.readableDatabase
//        val selectQuery = "SELECT * FROM $DB_TABLE ORDER BY $INFO_NAME DESC"
//        val cursor = db.rawQuery(selectQuery, null)
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getInt(cursor.getColumnIndex(INFO_ID))
//                val name = cursor.getString(cursor.getColumnIndex(INFO_NAME))
//                val date = cursor.getString(cursor.getColumnIndex(INFO_DATE))
//                val image = cursor.getString(cursor.getColumnIndex(INFO_IMG))
//                val info = cursor.getString(cursor.getColumnIndex(INFO_INFO))
//                val item = Profile(id, name, info, image, date)
//                itemList.add(item)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return itemList
//    }

}