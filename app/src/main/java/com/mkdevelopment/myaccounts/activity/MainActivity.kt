package com.mkdevelopment.myaccounts.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.adapter.AccountAdapter
import com.mkdevelopment.myaccounts.adapter.CategoryAdapter
import com.mkdevelopment.myaccounts.common.ItemClickListener
import com.mkdevelopment.myaccounts.common.ItemLongClickListener
import com.mkdevelopment.myaccounts.database.CategoryEntity
import com.mkdevelopment.myaccounts.databinding.ActivityMainBinding
import com.mkdevelopment.myaccounts.utils.SharedPreferencesHelper
import com.mkdevelopment.myaccounts.viewmodel.AccountDataViewModel
import com.mkdevelopment.myaccounts.viewmodel.CategoryDataViewModel
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private val accountDataViewModel: AccountDataViewModel by viewModels()
    private val categoryDataViewModel: CategoryDataViewModel by viewModels()
    private var selectedPopupItem = 0
    private var selectedCategoryId = 0
    private var deleteDialog: Dialog? = null
    private val categoryList: MutableList<CategoryEntity> = mutableListOf()

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainInitialize()
    }

    override fun onPause() {
        super.onPause()
        selectedPopupItem = 0
        selectedCategoryId = 0
    }

    override fun onResume() {
        super.onResume()
        binding.categoryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            categoryAdapter = CategoryAdapter(object : ItemClickListener {
                override fun onItemClick(id: Int) {
                    selectedCategoryId = id
                    loadMainData(selectedPopupItem)
                }
            }, object : ItemLongClickListener {
                override fun onItemClick(categoryEntity: CategoryEntity) {
                    deleteCategoryDialog(categoryEntity, categoryAdapter)
                }
            })
            this.adapter = categoryAdapter
            categoryDataViewModel.getAllDataByIdASC.observe(this@MainActivity) { data ->
                categoryList.clear()
                categoryList.add(CategoryEntity(id = 0, title = getString(R.string.all_items)))
                if (data.isNotEmpty()) {
                    categoryList.addAll(data)
                }
                categoryAdapter.setDataList(categoryList)
            }
        }

        // First Load
        loadMainData(selectedPopupItem)
    }

    @SuppressLint("InflateParams")
    private fun mainInitialize() {
        accountAdapter = AccountAdapter(this, accountDataViewModel)
        val screenWidth = resources.displayMetrics.widthPixels

        binding.mainRecyclerView.adapter = accountAdapter
        binding.mainRecyclerView.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

        binding.fabAddAccount.setOnClickListener {
            val intent = Intent(this@MainActivity, AddAccountActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this@MainActivity,
                R.anim.from_right,
                R.anim.to_left
            )
            startActivity(intent, options.toBundle())
        }

        binding.filterIcon.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.custom_popup, null)
            val popupWindow = PopupWindow(
                popupView,
                screenWidth / 2,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.elevation = resources.getDimension(R.dimen._3sdp)
            popupWindow.showAsDropDown(
                binding.filterIcon,
                0,
                resources.getDimension(R.dimen._6sdp).toInt()
            )

            val stringArray = arrayOf(
                getString(R.string.getAllDataByIdDESC),
                getString(R.string.getAllDataByIdASC),
                getString(R.string.getAllDataByTextASC),
                getString(R.string.getAllDataByTextDESC)
            )
            val mainRecyclerView = popupView.findViewById<RecyclerView>(R.id.main_recycler_view)
            mainRecyclerView.layoutManager = LinearLayoutManager(this)
            mainRecyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.custom_popup_item, parent, false)
                    return ViewHolder(view)
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder,
                    @SuppressLint("RecyclerView") position: Int
                ) {
                    val listItem = stringArray[position]
                    (holder as ViewHolder).bind(listItem)

                    holder.itemView.setOnClickListener {
                        selectedPopupItem = position
                        notifyDataSetChanged()
                        loadMainData(position)
                        if (popupView != null) {
                            popupWindow.dismiss()
                        }
                    }

                    if (position == selectedPopupItem) {
                        holder.itemView.setBackgroundResource(R.drawable.item_selected_12sdp)
                        holder.textView.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.category_selected_text
                            )
                        )
                    } else {
                        holder.itemView.background = null
                        holder.textView.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.category_unselected_text
                            )
                        )
                    }
                }

                override fun getItemCount(): Int {
                    return stringArray.size
                }

                inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                    val textView: TextView = itemView.findViewById(R.id.textView)

                    fun bind(listItem: String) {
                        textView.text = listItem
                    }
                }
            }
        }

        binding.userIcon.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.custom_user_popup, null)
            val popupWindow = PopupWindow(
                popupView,
                resources.getDimension(R.dimen._154sdp).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.elevation = resources.getDimension(R.dimen._3sdp)
            val marginOffset = resources.getDimension(R.dimen._6sdp).toInt()

            val location = IntArray(2)
            binding.userIcon.getLocationOnScreen(location)
            val x = location[0] + binding.userIcon.width - popupWindow.width
            val y = location[1] + binding.userIcon.height + popupWindow.height + marginOffset

            popupWindow.showAtLocation(binding.userIcon, Gravity.START or Gravity.TOP, x, y)


            val themeSwitch = popupView.findViewById<MaterialSwitch>(R.id.themeSwitch)
            val deleteAllDataTextView = popupView.findViewById<TextView>(R.id.deleteAllData)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                themeSwitch.isVisible = true
                val nightModeFlags: Int = resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK

                themeSwitch.isChecked =
                    nightModeFlags == Configuration.UI_MODE_NIGHT_NO || nightModeFlags == Configuration.UI_MODE_NIGHT_UNDEFINED
                themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    popupWindow.dismiss()
                }

            } else {
                themeSwitch.isVisible = false
            }


            deleteAllDataTextView.setOnClickListener {
                deleteAllDataDialog()
                if (popupView != null) {
                    popupWindow.dismiss()
                }
            }
        }

        binding.searchBarVoiceIcon.setOnClickListener { promptSpeechInput() }

        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if (searchText.isNotEmpty()) {
                    binding.fabAddAccount.hide()
                    binding.categoryLayout.visibility = View.GONE
                } else {
                    binding.fabAddAccount.show()
                    binding.categoryLayout.visibility = View.VISIBLE
                }

                accountDataViewModel.searchDatabase(searchText).observe(this@MainActivity) { data ->
                    if (data.isEmpty()) {
                        binding.emptyDataText.text = if (selectedCategoryId == 0) {
                            getString(R.string.no_saved_account)
                        } else {
                            getString(R.string.no_saved_account_category)
                        }
                        binding.emptyDataIcon.visibility = View.VISIBLE
                        binding.emptyDataText.visibility = View.VISIBLE
                        binding.mainRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyDataIcon.visibility = View.GONE
                        binding.emptyDataText.visibility = View.GONE
                        binding.mainRecyclerView.visibility = View.VISIBLE
                        accountAdapter.setDataList(data)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


        accountDataViewModel.getTotalAccountCount().observe(this@MainActivity) { totalCount ->
            if (totalCount != null && totalCount > 0) {
                binding.searchLayout.visibility = View.VISIBLE
                binding.mainRecyclerView.visibility = View.VISIBLE
                binding.categoryLayout.visibility = View.VISIBLE
            } else {
                binding.searchLayout.visibility = View.GONE
                binding.mainRecyclerView.visibility = View.GONE
                binding.categoryLayout.visibility = View.GONE
            }
        }
    }


    private fun deleteAllDataDialog() {
        if (deleteDialog == null) {
            deleteDialog = Dialog(this@MainActivity, R.style.WideDialog)
            deleteDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
            deleteDialog?.window?.attributes?.windowAnimations =
                R.style.DialogZoomAnimation
            deleteDialog?.setContentView(R.layout.delete_dialog)
            //   errorDialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            deleteDialog?.setCancelable(true)
            val yesButton = deleteDialog?.findViewById<MaterialButton>(R.id.yesButton)
            val noButton = deleteDialog?.findViewById<MaterialButton>(R.id.noButton)
            val title = deleteDialog?.findViewById<TextView>(R.id.title)
            val desc = deleteDialog?.findViewById<TextView>(R.id.desc)

            title?.text = getString(R.string.delete_all_data)
            desc?.text = getString(R.string.delete_all_data_desc)

            deleteDialog?.setOnDismissListener { deleteDialog = null }
            deleteDialog?.setOnCancelListener { deleteDialog = null }

            noButton?.setOnClickListener {
                deleteDialog?.dismiss()
            }

            yesButton?.setOnClickListener {
                accountDataViewModel.deleteAllData()
                SharedPreferencesHelper(this@MainActivity).clearAllData()
                deleteDialog?.dismiss()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.delete_all_data_message),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            if (!this@MainActivity.isFinishing && deleteDialog?.isShowing != true) {
                deleteDialog?.show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteCategoryDialog(category: CategoryEntity, categoryAdapter: CategoryAdapter) {
        if (deleteDialog == null) {
            deleteDialog = Dialog(this@MainActivity, R.style.WideDialog)
            deleteDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
            deleteDialog?.window?.attributes?.windowAnimations =
                R.style.DialogZoomAnimation
            deleteDialog?.setContentView(R.layout.delete_dialog)
            //   errorDialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            deleteDialog?.setCancelable(true)
            val yesButton = deleteDialog?.findViewById<MaterialButton>(R.id.yesButton)
            val noButton = deleteDialog?.findViewById<MaterialButton>(R.id.noButton)
            val title = deleteDialog?.findViewById<TextView>(R.id.title)
            val desc = deleteDialog?.findViewById<TextView>(R.id.desc)

            title?.text = getString(R.string.delete_category)
            desc?.text = getString(R.string.delete_category_desc)

            deleteDialog?.setOnDismissListener { deleteDialog = null }
            deleteDialog?.setOnCancelListener { deleteDialog = null }

            noButton?.setOnClickListener {
                deleteDialog?.dismiss()
            }

            yesButton?.setOnClickListener {
                accountDataViewModel.updateAccountsCategory(category.id)

                categoryDataViewModel.deleteData(category)
                categoryAdapter.notifyDataSetChanged()
                selectedPopupItem = 0
                selectedCategoryId = 0
                loadMainData(selectedPopupItem)
                deleteDialog?.dismiss()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.delete_category_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (!this@MainActivity.isFinishing && deleteDialog?.isShowing != true) {
                deleteDialog?.show()
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    binding.searchBarEditText.setText(speechResults?.get(0)?.lowercase() ?: "")
                }
            }
        }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        try {
            activityResultLauncher.launch(intent)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(
                applicationContext,
                getString(R.string.voice_service_failed), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadMainData(position: Int) {
        when (position) {
            0 -> {
                accountDataViewModel.getAllDataByIdDESC(selectedCategoryId)
                    .observe(this@MainActivity) { data ->
                        if (data.isEmpty()) {
                            if (selectedCategoryId == 0) {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account)
                            } else {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account_category)
                            }
                            loadDataView(true)
                        } else {
                            accountAdapter.setDataList(data)
                            loadDataView(false)
                        }
                    }
            }

            1 -> {
                accountDataViewModel.getAllDataByIdASC(selectedCategoryId)
                    .observe(this@MainActivity) { data ->
                        if (data.isEmpty()) {
                            if (selectedCategoryId == 0) {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account)
                            } else {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account_category)
                            }
                            loadDataView(true)
                        } else {
                            accountAdapter.setDataList(data)
                            loadDataView(false)
                        }
                    }
            }

            2 -> {
                accountDataViewModel.getAllDataByTitleASC(selectedCategoryId)
                    .observe(this@MainActivity) { data ->
                        if (data.isEmpty()) {
                            if (selectedCategoryId == 0) {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account)
                            } else {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account_category)
                            }
                            loadDataView(true)
                        } else {
                            accountAdapter.setDataList(data)
                            loadDataView(false)
                        }
                    }
            }

            3 -> {
                accountDataViewModel.getAllDataByTitleDESC(selectedCategoryId)
                    .observe(this@MainActivity) { data ->
                        if (data.isEmpty()) {
                            if (selectedCategoryId == 0) {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account)
                            } else {
                                binding.emptyDataText.text =
                                    getString(R.string.no_saved_account_category)
                            }
                            loadDataView(true)
                        } else {
                            accountAdapter.setDataList(data)
                            loadDataView(false)
                        }
                    }
            }
        }
    }

    private fun loadDataView(isEmptyData: Boolean) {
        if (isEmptyData) {
            binding.emptyDataIcon.visibility = View.VISIBLE
            binding.emptyDataText.visibility = View.VISIBLE
            binding.mainRecyclerView.visibility = View.GONE
        } else {
            binding.emptyDataIcon.visibility = View.GONE
            binding.emptyDataText.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE
        }
    }
}