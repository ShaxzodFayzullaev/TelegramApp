package shakha.telegram.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import shakha.telegram.models.Profile

class MyDBHelper(contex: Context) : SQLiteOpenHelper(contex, DB_NAME, null, DB_VERSION),
    MyDbInterface {

    companion object {
        val DB_NAME = "db_name"
        val DB_VERSION = 1

        val INFO_TABLE = "info_table"
        val INFO_ID = "id"
        val INFO_NAME = "name"
        val INFO_INFO = "info"
        val INFO_IMG = "img"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "create table $INFO_TABLE($INFO_ID integer not null primary key autoincrement unique , $INFO_NAME text not null , $INFO_INFO text not null ,$INFO_IMG text not null)"
        db?.execSQL(query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    override fun addPro(profile: Profile) {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INFO_NAME, profile.name)
        contentValues.put(INFO_INFO, profile.info)
        contentValues.put(INFO_IMG, profile.img)
        database.insert(INFO_TABLE, null, contentValues)
    }

    override fun getAllPro(): ArrayList<Profile> {
        val list = ArrayList<Profile>()
        val query = "select * from $INFO_TABLE"
        val database = readableDatabase
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Profile(
                        id = cursor.getInt(0),
                        name = cursor.getString(1),
                        info = cursor.getString(2),
                        img = cursor.getString(3)
                    )
                )
            } while (cursor.moveToNext())
        }
        return list
    }

    override fun editPro(profile: Profile) {
        val database = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INFO_INFO, profile.info)
        contentValues.put(INFO_IMG, profile.img)
        contentValues.put(INFO_NAME, profile.name)
        contentValues.put(INFO_ID, profile.id)
        database.update(INFO_TABLE, contentValues, "$INFO_ID=?", arrayOf(profile.id.toString()))
    }

    override fun deletePro(profile: Profile) {
        val database = writableDatabase
        database.delete(INFO_TABLE, "$INFO_ID=?", arrayOf(profile.id.toString()))
        database.close()
    }
}