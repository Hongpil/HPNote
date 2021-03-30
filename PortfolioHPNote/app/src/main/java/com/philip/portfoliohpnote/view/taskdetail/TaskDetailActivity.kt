package com.philip.portfoliohpnote.view.taskdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.data.tasks.source.Injection
import com.philip.portfoliohpnote.util.replaceFragmentInActivity
import com.philip.portfoliohpnote.util.setupActionBar
import com.philip.portfoliohpnote.view.taskdetail.presenter.TaskDetailPresenter

class TaskDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.taskdetail_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Get the requested task id
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)

        val taskDetailFragment = supportFragmentManager
            .findFragmentById(R.id.contentFrame) as TaskDetailFragment? ?:
        TaskDetailFragment.newInstance(taskId).also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
        // Create the presenter
        if (taskId != null) {
            TaskDetailPresenter(taskId, Injection.provideTasksRepository(applicationContext),
                taskDetailFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }
}
