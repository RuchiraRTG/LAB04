package sliit.ruchira.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sliit.ruchira.todoapp.database.daos.TodoDao
import sliit.ruchira.todoapp.model.Todo

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getDao():TodoDao

    companion object{

        @Volatile
        private var instance: TodoDatabase? = null

        fun getInstance(context:Context):TodoDatabase{
            synchronized(this){
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "database"
                ).build().also {
                    instance = it
                }
            }
        }

    }

}