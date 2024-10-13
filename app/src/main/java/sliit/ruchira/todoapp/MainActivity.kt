package sliit.ruchira.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sliit.ruchira.todoapp.adapter.TodoAdapter
import sliit.ruchira.todoapp.database.TodoDatabase
import sliit.ruchira.todoapp.database.repositories.TodoRepository
import sliit.ruchira.todoapp.view_model.MainActivityViewModel

class MainActivity : AppCompatActivity(), TodoAdapter.OnItemEdit {

    private lateinit var toolbar: MaterialToolbar
    private var viewModel:MainActivityViewModel? = null
    private var repository: TodoRepository? = null

    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var addTodoItemBtn:FloatingActionButton

    private val addTodoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                //reload todos
                loadTodos()
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        repository = TodoRepository(TodoDatabase.getInstance(this))

        toolbar = findViewById(R.id.toolbar)
        todoRecyclerView = findViewById(R.id.todoRecyclerView)
        addTodoItemBtn = findViewById(R.id.addTodoItemBtn)

        toolbar.title = "Todo"
        todoRecyclerView.setHasFixedSize(true)
        todoRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)


        addTodoItemBtn.setOnClickListener{
            val intent = Intent(this@MainActivity,AddTodoActivity::class.java)
            addTodoLauncher.launch(intent)
        }

        viewModel!!.todoList.observe(this){
            val todoAdapter = TodoAdapter(this,it, viewModel!!)
            todoAdapter.setItemEditListener(this)
            todoRecyclerView.adapter = todoAdapter
        }

        loadTodos()

    }

    private fun loadTodos(){

        CoroutineScope(Dispatchers.IO).launch {

            val todos = repository?.getAllTodo()

            runOnUiThread {
                if(todos != null){
                    viewModel!!.setTodoList(todos)
                }
            }

        }

    }

    override fun onItemEdit(id: Int) {
        val intent = Intent(this@MainActivity,AddTodoActivity::class.java)
        intent.putExtra("id",id.toString())
        addTodoLauncher.launch(intent)
    }

}