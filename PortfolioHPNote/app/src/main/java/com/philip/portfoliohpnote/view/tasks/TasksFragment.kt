package com.philip.portfoliohpnote.view.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.data.account.AccountRepository
import com.philip.portfoliohpnote.data.tasks.Task
import com.philip.portfoliohpnote.data.tasks.source.Injection
import com.philip.portfoliohpnote.util.setupActionBar
import com.philip.portfoliohpnote.util.showSnackBar
import com.philip.portfoliohpnote.view.taskadd.AddEditTaskActivity
import com.philip.portfoliohpnote.view.taskdetail.TaskDetailActivity
import com.philip.portfoliohpnote.view.tasks.presenter.TasksContract
import com.philip.portfoliohpnote.view.tasks.presenter.TasksPresenter
import kotlinx.android.synthetic.main.tasks_frag.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class TasksFragment : Fragment(), TasksContract.View {


    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    private var loginUserEmail: String? = null

    private lateinit var presenter: TasksPresenter

    override var isActive: Boolean = false
        get() = isAdded

    private lateinit var noTasksView: View
    private lateinit var noTaskIcon: ImageView
    private lateinit var noTaskMainView: TextView
    private lateinit var noTaskAddView: TextView
    private lateinit var tasksView: LinearLayout
    private lateinit var filteringLabelView: TextView

    /**
     * Listener for clicks on tasks in the ListView.
     */
    internal var itemListener: TaskItemListener = object : TaskItemListener {
        override fun onTaskClick(clickedTask: Task) {
            presenter.openTaskDetails(clickedTask)
        }

        override fun onCompleteTaskClick(completedTask: Task) {
            loginUserEmail?.let { presenter.completeTask(it, completedTask) }

        }

        override fun onActivateTaskClick(activatedTask: Task) {
            loginUserEmail?.let { presenter.activateTask(it, activatedTask) }
        }
    }

    private val listAdapter = TasksAdapter(ArrayList(0), itemListener)

    override fun onResume() {
        super.onResume()
        //presenter.start()
        loginUserEmail?.let { presenter.loadTasks(it, false) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tasks_frag, container, false)

        /*
         * Set up the toolbar.
         * Activity 와 Fragment 에서 Toolbar 적용하기
         * [Reference] https://www.youtube.com/watch?v=eWHSUHQJlkQ
         */
//        val toolbar = view.findViewById<Toolbar>(R.id.toolbar_tasks)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar_tasks)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setupActionBar(R.id.toolbar_tasks) {
            setDisplayHomeAsUpEnabled(false) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        }

        // Set up tasks view
        with(view) {
            val listView = findViewById<ListView>(R.id.tasks_list).apply { adapter = listAdapter }

            // Set up progress indicator
            findViewById<ScrollChildSwipeRefreshLayout>(R.id.refresh_layout).apply {
                setColorSchemeColors(
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                    ContextCompat.getColor(requireContext(), R.color.colorAccent),
                    ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
                )
                // Set the scrolling view in the custom SwipeRefreshLayout.
                scrollUpChild = listView
                setOnRefreshListener { loginUserEmail?.let { presenter.loadTasks(it, false) } }

            }

            filteringLabelView = findViewById(R.id.filteringLabel)
            tasksView = findViewById(R.id.tasksLL)

            // Set up  no tasks view
            noTasksView = findViewById(R.id.noTasks)
            noTaskIcon = findViewById(R.id.noTasksIcon)
            noTaskMainView = findViewById(R.id.noTasksMain)
            noTaskAddView = (findViewById<TextView>(R.id.noTasksAdd)).also {
                it.setOnClickListener { showAddTask() }
            }
        }

        // Set up floating action button
        fab_add_task.setImageResource(R.drawable.ic_add)
        fab_add_task.setOnClickListener {
            presenter.addNewTask()
        }

        // Create the presenter
        presenter = TasksPresenter(
            AccountRepository(this@TasksFragment.context!!),
            Injection.provideTasksRepository(this@TasksFragment.context!!),
            this@TasksFragment).apply {
            // Load previously saved state, if available.
            if (savedInstanceState != null) {
                currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY)
                        as TasksFilterType
            }
        }



        // show logined user Info
        presenter.getLoginUserInfo(object : AccountDataSource.LoadAccountCallback {
            override fun onAccountLoaded(account: JSONObject) {

                // just check
                val getAccount: JSONObject = account
                activity?.let {
//                    Toast.makeText(activity, "account : $account", Toast.LENGTH_LONG).show()
//                    Toast.makeText(activity, "getAccount : $getAccount", Toast.LENGTH_LONG).show()
                }

                loginUserEmail = account.getString("user_email")
            }

            override fun onDataNotAvailable() {
                activity?.let {
                    Toast.makeText(activity, "Failed to get logined user information.", Toast.LENGTH_SHORT).show()
                }
            }
        })



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> loginUserEmail?.let { presenter.clearCompletedTasks(it) }
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_refresh -> loginUserEmail?.let { presenter.loadTasks(it, true) }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun showFilteringPopUpMenu() {
        val activity = activity ?: return
        val context = context ?: return
        PopupMenu(context, activity.findViewById(R.id.menu_filter)).apply {
            menuInflater.inflate(R.menu.filter_tasks, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.active -> presenter.currentFiltering = TasksFilterType.ACTIVE_TASKS
                    R.id.completed -> presenter.currentFiltering = TasksFilterType.COMPLETED_TASKS
                    else -> presenter.currentFiltering = TasksFilterType.ALL_TASKS
                }
                loginUserEmail?.let { presenter.loadTasks(it, false) }
                true
            }
            show()
        }
    }

    override fun setLoadingIndicator(active: Boolean) {
        val root = view ?: return
        with(root.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)) {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            post { isRefreshing = active }
        }
    }

    override fun showTasks(tasks: List<Task>) {
        listAdapter.tasks = tasks
        tasksView.visibility = View.VISIBLE
        noTasksView.visibility = View.GONE
    }

    override fun showNoActiveTasks() {
        showNoTasksViews(resources.getString(R.string.no_tasks_active), R.drawable.ic_check_circle_24dp, false)
    }

    override fun showNoTasks() {
        showNoTasksViews(resources.getString(R.string.no_tasks_all), R.drawable.ic_assignment_turned_in_24dp, false)
    }

    override fun showNoCompletedTasks() {
        // Coroutine 사용
        // Coroutine 사용 안 하면, the original thread that created a view hierarchy can touch its views. coroutine 에러 발생함.
        GlobalScope.launch(Dispatchers.Main) {
            showNoTasksViews(resources.getString(R.string.no_tasks_completed), R.drawable.ic_verified_user_24dp, false)
        }
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message))
    }

    private fun showNoTasksViews(mainText: String, iconRes: Int, showAddView: Boolean) {
        tasksView.visibility = View.GONE
        noTasksView.visibility = View.VISIBLE

        noTaskMainView.text = mainText
        noTaskIcon.setImageResource(iconRes)
        noTaskAddView.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    override fun showActiveFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_active)
    }

    override fun showCompletedFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_completed)
    }

    override fun showAllFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_all)
    }

    override fun showAddTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK)
    }

    override fun showTaskDetailsUi(taskId: String) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        val intent = Intent(context, TaskDetailActivity::class.java).apply {
            putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        }
        startActivity(intent)
    }

    override fun showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete))
    }

    override fun showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active))
    }

    override fun showCompletedTasksCleared() {
        showMessage(getString(R.string.completed_tasks_cleared))
    }

    override fun showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error))
    }

    private fun showMessage(message: String) {
        view?.showSnackBar(message, Snackbar.LENGTH_LONG)
    }









    private class TasksAdapter(tasks: List<Task>, private val itemListener: TaskItemListener)
        : BaseAdapter() {

        var tasks: List<Task> = tasks
            set(tasks) {
                field = tasks

                // Coroutine 사용
                // Coroutine 사용 안 하면, the original thread that created a view hierarchy can touch its views. coroutine 에러 발생함.
                GlobalScope.launch(Dispatchers.Main) {
                    notifyDataSetChanged()
                }

            }

        override fun getCount() = tasks.size

        override fun getItem(i: Int) = tasks[i]

        override fun getItemId(i: Int) = i.toLong()

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val task = getItem(i)
            val rowView = view ?: LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.task_item, viewGroup, false)

            with(rowView.findViewById<TextView>(R.id.title)) {
                text = task.titleForList
            }

            with(rowView.findViewById<CheckBox>(R.id.complete)) {
                // Active/completed task UI
                isChecked = task.isCompleted
                val rowViewBackground =
                    if (task.isCompleted) R.drawable.list_completed_touch_feedback
                    else R.drawable.touch_feedback
                rowView.setBackgroundResource(rowViewBackground)
                setOnClickListener {
                    if (!task.isCompleted) {
                        itemListener.onCompleteTaskClick(task)
                    } else {
                        itemListener.onActivateTaskClick(task)
                    }
                }
            }
            rowView.setOnClickListener { itemListener.onTaskClick(task) }
            return rowView
        }
    }

    interface TaskItemListener {

        fun onTaskClick(clickedTask: Task)

        fun onCompleteTaskClick(completedTask: Task)

        fun onActivateTaskClick(activatedTask: Task)
    }

}