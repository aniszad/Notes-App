package com.az.notes.presentation.views

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.az.notes.R
import com.az.notes.databinding.ActivityMainBinding
import com.az.notes.databinding.CustomTabItemLayoutBinding
import com.az.notes.presentation.adapters.MyPagerAdapter
import com.az.notes.presentation.utils.ViewAnimator
import com.az.notes.presentation.viewmodels.ViewModelMain
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: MyPagerAdapter

    private val viewModelMain: ViewModelMain by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customizeSystemBars(binding.root.id)
        onBackPressedDispatcher.addCallback {
            if (viewModelMain.searchBarHasFocus.value == true) {
                viewModelMain.searchBarHasFocus(false)
            } else if (viewModelMain.noteSelectionMode.value == true) {
                viewModelMain.setSelectionMode(false)
            }else{
                finish()
            }
        }
        createUncategorizedFolderIfNotExistent()

        setCustomTabItemLayouts()

        setViewModelObservers()

        setViewPagerAdapter()

    }

    override fun onResume() {
        super.onResume()
       // setViewPagerAdapter()
    }

    private fun createUncategorizedFolderIfNotExistent() {
        viewModelMain.getUncategorizedFolder()
    }

    private fun setViewModelObservers() {
        viewModelMain.searchBarHasFocus.observe(this@MainActivity) { hasFocus ->
            if (hasFocus) {
                ViewAnimator.shrinkView(binding.tabLayoutMain)
            } else {
                ViewAnimator.expandView(binding.tabLayoutMain)

            }
        }
    }

    private fun setViewPagerAdapter() {
        val notesFragment = NotesFragment()
        val tasksFragment = TasksFragment()
        pagerAdapter = MyPagerAdapter(this@MainActivity, listOf(notesFragment, tasksFragment))
        binding.viewPagerMain.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayoutMain.selectTab(binding.tabLayoutMain.getTabAt(position))
            }

        })
        binding.viewPagerMain.adapter = pagerAdapter
        binding.viewPagerMain.isUserInputEnabled = false
    }

    private fun setCustomTabItemLayouts() {
        binding.tabLayoutMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerMain.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        val states = arrayOf(
            intArrayOf(android.R.attr.state_selected),
            intArrayOf(-android.R.attr.state_selected)
        )
        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.colorYellowPrimary),
            ContextCompat.getColor(this, R.color.colorGrayPrimary)
        )
        val tabIconColorStateList = ColorStateList(states, colors)
        for (i in 0 until binding.tabLayoutMain.tabCount) {
            val tab = binding.tabLayoutMain.getTabAt(i)
            val customTabLayoutBinding = CustomTabItemLayoutBinding.inflate(layoutInflater)
            customTabLayoutBinding.tabIcon.imageTintList = tabIconColorStateList
            if (i == 0) {
                customTabLayoutBinding.tabIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity, R.drawable.selector_drawable_notes
                    )
                )

            } else {
                customTabLayoutBinding.tabIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MainActivity, R.drawable.selector_drawable_tasks
                    )
                )
            }
            tab?.customView = customTabLayoutBinding.root
        }
    }
}