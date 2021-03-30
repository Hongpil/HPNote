package com.philip.portfoliohpnote.view.taskadd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.data.account.AccountRepository
import com.philip.portfoliohpnote.data.tasks.source.Injection
import com.philip.portfoliohpnote.util.replaceFragmentInActivity
import com.philip.portfoliohpnote.util.setupActionBar
import com.philip.portfoliohpnote.view.taskadd.presenter.AddEditTaskPresenter

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var addEditTaskPresenter: AddEditTaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)
        val taskId = intent.getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setTitle(if (taskId == null) R.string.add_task else R.string.edit_task)
        }

        val addEditTaskFragment =
            supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                ?: AddEditTaskFragment.newInstance(taskId).also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

        val shouldLoadDataFromRepo =
        // Prevent the presenter from loading data from the repository if this is a config change.
            // Data might not have loaded when the config change happen, so we saved the state.
            savedInstanceState?.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY) ?: true


        // Create the presenter
        addEditTaskPresenter = AddEditTaskPresenter(taskId,
            AccountRepository(this),
            Injection.provideTasksRepository(applicationContext),
            addEditTaskFragment,
            shouldLoadDataFromRepo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the state so that next time we know if we need to refresh data.
        super.onSaveInstanceState(outState.apply {
            putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, addEditTaskPresenter.isDataMissing)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
        const val REQUEST_ADD_TASK = 1
    }
}