package com.example.learncompose.feature.experiment.data.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [User::class],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = MyAutoMigration::class)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "app_database"
//                )
//                    .addMigrations(MIGRATION_3_4)
//                    .addMigrations(MIGRATION_4_5)
//                    .fallbackToDestructiveMigration(false)
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 必须使用原生 SQL 执行，注意数据类型与 NOT NULL 限制
        db.execSQL("ALTER TABLE users ADD COLUMN location TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. 创建符合新结构要求的临时表 users_new（不包含 email 列）
        db.execSQL(
            """
            CREATE TABLE users_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                fullName TEXT NOT NULL,
                age INTEGER NOT NULL,
                location TEXT NOT NULL DEFAULT ''
            )
            """.trimIndent()
        )

        // 2. 将旧表数据复制到新表中（排除 email 列）
        db.execSQL(
            """
            INSERT INTO users_new (id, fullName, age, location)
            SELECT id, fullName, age, location FROM users
            """.trimIndent()
        )

        // 3. 删除旧表
        db.execSQL("DROP TABLE users")

        // 4. 将临时表重命名为原表名
        db.execSQL("ALTER TABLE users_new RENAME TO users")
    }
}

@RenameColumn(tableName = "users", fromColumnName = "name", toColumnName = "fullName")
class MyAutoMigration : AutoMigrationSpec