package com.socheanith.nytimesmostpopular

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.socheanith.nytimesmostpopular.fragment.AboutFragment
import com.socheanith.nytimesmostpopular.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame_layout)
        navigationView = findViewById(R.id.navigation_view)

        //class setupToolbar function
        setUpToolbar()
        //Open Home page directly whenever user open the app
        openHome()

        //create a hamburger toggle bar
        var actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // add color to the item selected
        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.home->{
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.about->{
                    val frag = AboutFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_layout, frag)
                    transaction.commit()
                    supportActionBar?.title = "About"
                    drawerLayout.closeDrawers()
                }
                R.id.exit->{
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Exit App")
                    dialog.setMessage("Are you sure want to exit?")
                    dialog.setPositiveButton("Yes"){text,listener ->
                        finish()
                    }
                    dialog.setNegativeButton("No"){text,listener ->
                        openHome()
                        drawerLayout.closeDrawers()
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Title Toolbar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun openHome(){
        val frag = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout,frag)
        transaction.commit()
        supportActionBar?.title = "NY Time Most Popular"
        navigationView.setCheckedItem(R.id.home)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        if(id == R.id.search){
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame_layout)
        when(frag){
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }

}