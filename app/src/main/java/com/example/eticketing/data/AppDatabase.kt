package com.example.eticketing.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Destination::class, Ticket::class, Category::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun destinationDao(): DestinationDao
    abstract fun ticketDao(): TicketDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN role TEXT NOT NULL DEFAULT 'user'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE users_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nama TEXT NOT NULL,
                        email TEXT NOT NULL,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL DEFAULT 'user'
                    )
                """.trimIndent())
                db.execSQL("INSERT INTO users_new SELECT * FROM users")
                db.execSQL("DROP TABLE users")
                db.execSQL("ALTER TABLE users_new RENAME TO users")
                db.execSQL("ALTER TABLE destinations ADD COLUMN pengelolaId INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        emoji TEXT NOT NULL DEFAULT '🏷️'
                    )
                """.trimIndent())
                // Seed kategori default
                db.execSQL("INSERT INTO categories (name, emoji) VALUES ('Pantai', '🏖️')")
                db.execSQL("INSERT INTO categories (name, emoji) VALUES ('Gunung', '⛰️')")
                db.execSQL("INSERT INTO categories (name, emoji) VALUES ('Kota', '🏙️')")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "etravel_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.userDao()?.register(
                                    User(
                                        nama = "Admin",
                                        email = "admin@etravel.com",
                                        password = "admin123",
                                        role = "admin"
                                    )
                                )
                                // Seed kategori default
                                INSTANCE?.categoryDao()?.insert(Category(name = "Pantai", emoji = "🏖️"))
                                INSTANCE?.categoryDao()?.insert(Category(name = "Gunung", emoji = "⛰️"))
                                INSTANCE?.categoryDao()?.insert(Category(name = "Kota", emoji = "🏙️"))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}