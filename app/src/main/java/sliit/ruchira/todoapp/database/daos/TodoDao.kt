package sliit.ruchira.todoapp.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import sliit.ruchira.todoapp.model.Todo

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo ORDER by id DESC")
    fun getAll():List<Todo>

    @Query("SELECT * FROM todo WHERE id=:id")
    fun get(id: Int):Todo

    @Insert
    fun insert(todo: Todo)

    @Delete
    fun delete(todo:Todo)

    @Query("UPDATE todo SET title=:title, description=:description, priority=:priority, deadline_date=:deadLineDate WHERE id=:id")
    fun update(id:Int,title:String,description:String,priority:String,deadLineDate:Long)

}