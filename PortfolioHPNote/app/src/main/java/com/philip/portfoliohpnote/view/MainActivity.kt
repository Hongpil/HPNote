package com.philip.portfoliohpnote.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.philip.portfoliohpnote.R
import com.philip.portfoliohpnote.util.replace
import com.philip.portfoliohpnote.view.tasks.TasksFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tasksFragment: TasksFragment by lazy {
        TasksFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replace(R.id.container, tasksFragment)
    }
}