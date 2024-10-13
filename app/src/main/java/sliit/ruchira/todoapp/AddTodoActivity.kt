package sliit.ruchira.todoapp

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sliit.ruchira.todoapp.database.TodoDatabase
import sliit.ruchira.todoapp.database.repositories.TodoRepository
import sliit.ruchira.todoapp.model.Todo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddTodoActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var todoAddTitleInput:TextInputLayout
    private lateinit var todoAddDescInput:TextInputLayout
    private lateinit var todoAddPriorityInput:TextInputLayout
    private lateinit var todoAddDeadLineInput:TextInputLayout
    private lateinit var addTodoBtn: Button

    private var itemId:String? = null
    private var repository:TodoRepository? = null
    private lateinit var autoCompleteTextView: MaterialAutoCompleteTextView
    private lateinit var autoCompleteTextDeadLine:MaterialAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        //get update id
        val extras = intent.extras
        itemId = extras?.getString("id")

        repository = TodoRepository(TodoDatabase.getInstance(this))

        toolbar = findViewById(R.id.toolbar)
        todoAddTitleInput = findViewById(R.id.todoAddTitleInput)
        todoAddDescInput = findViewById(R.id.todoAddDescInput)
        todoAddPriorityInput = findViewById(R.id.todoAddPriorityInput)
        todoAddDeadLineInput = findViewById(R.id.todoAddDeadLineInput)
        addTodoBtn = findViewById(R.id.addTodoBtn)

        autoCompleteTextView = todoAddPriorityInput.editText!! as MaterialAutoCompleteTextView
        autoCompleteTextDeadLine = todoAddDeadLineInput.editText!! as MaterialAutoCompleteTextView

        toolbar.title = "Add Todo"
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener {
            finish()
        }

        initializePriority()

        addTodoBtn.setOnClickListener {
            addIem()
        }

        //Already focusing, when click edit text open date picker
        autoCompleteTextDeadLine.setOnClickListener {
            showDeadLinePicker()
        }

        if(itemId != null){
            loadDetails(itemId!!)
        }

    }

    private fun loadDetails(id:String){

        CoroutineScope(Dispatchers.IO).launch {

            val todoItem = repository!!.get(id.toInt())

            //convert millis to date
            var date:String? = null
            if(todoItem.deadlineDate != (0).toLong()){
                val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
                date = simpleDateFormat.format(Date(todoItem.deadlineDate))
            }

            runOnUiThread {
                //load details in fields
                todoAddTitleInput.editText?.setText(todoItem.title)
                todoAddDescInput.editText?.setText(todoItem.description)
                autoCompleteTextView.setText(todoItem.priority,false)
                if(date != null){
                    autoCompleteTextDeadLine.setText(date)
                }
            }

        }

    }

    private fun initializePriority(){

        val priorityList = mutableListOf<String>()
        priorityList.add("Very Low")
        priorityList.add("Low")
        priorityList.add("Normal")
        priorityList.add("High")
        priorityList.add("Very High")

        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,priorityList)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setText(priorityList[2],false)

    }

    private fun addIem(){

        val title = todoAddTitleInput.editText?.text.toString()
        val description = todoAddDescInput.editText?.text.toString()
        val priorityValue = autoCompleteTextView.text.toString()
        val deadlineDate = autoCompleteTextDeadLine.text.toString()

        //validate
        //validate title
        if(title == ""){
            todoAddTitleInput.error = "Please enter title"
            return
        }else{
            todoAddTitleInput.error = null
        }

        //validate deadline date
        var millis:Long = 0
        if(deadlineDate != ""){
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
            val date = simpleDateFormat.parse(deadlineDate)
            if(date != null){
                millis = date.time
            }
        }

        addTodoBtn.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            //check id is not null
            if(itemId == null){
                //insert data in table
                repository?.insert(Todo(title,description,priorityValue,millis,System.currentTimeMillis()))
            }else{
                //update data in table
                repository?.update(itemId!!.toInt(),title,description,priorityValue,millis)
            }
            //success inserted
            runOnUiThread {
                addTodoBtn.isEnabled = true
                //finish activity
                val intent = Intent()
                setResult(RESULT_OK,intent)
                finish()
            }
        }

    }

    private fun showDeadLinePicker(){

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this,DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            val dateString = generateDateString(i,i2,i3)
            autoCompleteTextDeadLine.setText(dateString)
        },year,month,day).show()

    }

    private fun generateDateString(year:Int,month:Int,date:Int):String{

        val y:String = year.toString()
        var m:String = (month+1).toString()
        var d:String = date.toString()

        //handle 1 length month
        if(m.length == 1){
            m = "0$m"
        }
        //handle 1 length date
        if(d.length == 1){
            d = "0$d"
        }

        //make date string
        val dateString:String = "$y/$m/$d"

        return dateString
    }

}