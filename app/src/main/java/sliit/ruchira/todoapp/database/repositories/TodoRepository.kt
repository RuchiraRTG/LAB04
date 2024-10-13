package sliit.ruchira.todoapp.database.repositories

import sliit.ruchira.todoapp.database.TodoDatabase
import sliit.ruchira.todoapp.model.Todo

class TodoRepository(private val database:TodoDatabase) {

    fun get(id:Int) = database.getDao().get(id)
    suspend fun update(id:Int,title:String,description:String,priority:String,deadLineDate:Long) = database.getDao().update(id,title,description,priority,deadLineDate)
    suspend fun insert(todo:Todo) = database.getDao().insert(todo)
    suspend fun delete(todo:Todo) = database.getDao().delete(todo)
    fun getAllTodo():List<Todo> = database.getDao().getAll()

}