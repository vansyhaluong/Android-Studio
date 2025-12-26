package com.example.tuan1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers

import com.example.tuan1.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [User::class,DB_MonHoc::class, Diem::class, HocKy::class, ThoiKhoaBieu::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun monHocDao(): MonHocDao
    abstract fun diemDao(): DiemDao
    abstract fun hocKyDao(): HocKyDao
    abstract fun tkbDao(): TKBDao
    abstract fun bangDiemDao(): BangDiemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()// đồ án OK
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // ✅ INSERT DỮ LIỆU CỨNG BẰNG SQL (CHUẨN ROOM)
                            db.execSQL(
                                "INSERT INTO hocky (tenHocKy) VALUES ('Học kỳ 1')"
                            )
                            db.execSQL(
                                "INSERT INTO hocky (tenHocKy) VALUES ('Học kỳ 2')"
                            )

                            db.execSQL(
                                """
                                INSERT INTO users(maSV, password, role)
                                VALUES ('admin','123','admin')
                            """
                            )
                            db.execSQL(
                                """
                                INSERT INTO users(maSV, password, role)
                                VALUES ('24211tt3936','123','user')
                            """
                            )
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}