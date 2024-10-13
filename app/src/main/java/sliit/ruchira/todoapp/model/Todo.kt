package sliit.ruchira.todoapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(@ColumnInfo(name = "title") val title:String,
                @ColumnInfo(name = "description") val description:String,
                @ColumnInfo(name = "priority") val priority:String,
                @ColumnInfo(name = "deadline_date") val deadlineDate:Long,
                @ColumnInfo(name = "time") val time:Long){

    @PrimaryKey(autoGenerate = true)
    var id:Int? = null

}