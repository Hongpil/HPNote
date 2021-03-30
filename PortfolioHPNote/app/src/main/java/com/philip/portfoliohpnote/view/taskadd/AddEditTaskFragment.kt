package com.philip.portfoliohpnote.view.taskadd

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.data.account.AccountDataSource
import com.philip.portfoliohpnote.util.showSnackBar
import com.philip.portfoliohpnote.view.taskadd.presenter.AddEditTaskContract
import org.json.JSONObject

class AddEditTaskFragment : Fragment(), AddEditTaskContract.View {

    override lateinit var presenter: AddEditTaskContract.Presenter
    override var isActive = false
        get() = isAdded

    private lateinit var title: TextView
    private lateinit var description: TextView

    private var loginUserEmail: String? = null


    override fun onResume() {
        super.onResume()

        presenter.start()


        // show logined user Info
        presenter.getLoginUserInfo(object : AccountDataSource.LoadAccountCallback {
            override fun onAccountLoaded(account: JSONObject) {
                // logined user email
                loginUserEmail = account.getString("user_email")
            }

            override fun onDataNotAvailable() {
                activity?.let {
                    Toast.makeText(activity, "Failed to get logined user information.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task_done)?.apply {
            setImageResource(R.drawable.ic_done)

            setOnClickListener {
                loginUserEmail?.let { it -> presenter.saveTask(it, title.text.toString(), description.text.toString()) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addtask_frag, container, false)
        with(root) {
            title = findViewById(R.id.add_task_title)
            description = findViewById(R.id.add_task_description)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun showEmptyTaskError() {
        title.showSnackBar(getString(R.string.empty_task_message), Snackbar.LENGTH_LONG)
    }

    override fun showTasksList() {
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setDescription(description: String) {
        this.description.text = description
    }

    companion object {

        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance(taskId: String?) =
            AddEditTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
                }
            }
    }
}
