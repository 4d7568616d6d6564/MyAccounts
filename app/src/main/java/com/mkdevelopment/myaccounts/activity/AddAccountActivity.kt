package com.mkdevelopment.myaccounts.activity

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.adapter.IconAdapter
import com.mkdevelopment.myaccounts.common.IconHelper
import com.mkdevelopment.myaccounts.database.AccountEntity
import com.mkdevelopment.myaccounts.database.CategoryEntity
import com.mkdevelopment.myaccounts.databinding.ActivityAddAccountBinding
import com.mkdevelopment.myaccounts.utils.ShakeAnimatorHelper
import com.mkdevelopment.myaccounts.utils.SharedPreferencesHelper
import com.mkdevelopment.myaccounts.viewmodel.AccountDataViewModel
import com.mkdevelopment.myaccounts.viewmodel.CategoryDataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class AddAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAccountBinding
    private val accountDataViewModel: AccountDataViewModel by viewModels()
    private val categoryDataViewModel: CategoryDataViewModel by viewModels()
    private var isPasswordVisible = true
    private var isDatePickerShown = false
    private var selectedGender: String = ""
    private var getDateFormatPattern: String = ""
    private var selectedIconPosition: Int = 0
    private var selectedCategoryPosition: Int = 0
    private var selectedPatternPosition: Int = -1
    private var addCategoryDialog: Dialog? = null
    private var dateFormatPatternDialog: Dialog? = null
    private val drawableList: MutableList<Int> = mutableListOf()
    private val categoryList: MutableList<String> = mutableListOf()
    private val categoryIdList: MutableList<Int> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener { finish() }

        binding.saveButton.setOnClickListener {
            insertData()
        }
        initialize()
    }

    private fun insertData() {
        binding.titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isNotEmpty() && binding.titleLayout.isErrorEnabled) {
                    binding.titleLayout.isErrorEnabled = false
                    binding.titleLayout.error = ""
                }
            }
        })


        if (binding.titleEditText.text?.isEmpty() == true) {
            ShakeAnimatorHelper(applicationContext, binding.titleLayout, 500).startAnimation()
            binding.titleLayout.isErrorEnabled = true
            binding.titleLayout.error = getString(R.string.warning_title)
            binding.nestedScrollView.smoothScrollTo(0, 0, 200)
        } else {
            binding.saveButton.isEnabled = false
            binding.saveButton.isFocusable = false
            binding.saveButton.isClickable = false
            accountDataViewModel.insertData(
                AccountEntity(
                    categoryId = selectedCategoryPosition,
                    title = binding.titleEditText.text.toString(),
                    name = binding.nameEditText.text.toString(),
                    surname = binding.surnameEditText.text.toString(),
                    gender = selectedGender,
                    birthday = binding.birthdayEditText.text.toString(),
                    username = binding.usernameEditText.text.toString(),
                    password = binding.passwordEditText.text.toString(),
                    email = binding.emailEditText.text.toString(),
                    phone = binding.phoneEditText.text.toString(),
                    recoveryEmail = binding.recoveryEmailEditText.text.toString(),
                    recoveryPhone = binding.recoveryPhoneEditText.text.toString(),
                    securityQuestion = binding.securityQuestionEditText.text.toString(),
                    securityQuestionAnswer = binding.securityQuestionAnswerEditText.text.toString(),
                    address = binding.addressEditText.text.toString(),
                    other = binding.otherEditText.text.toString(),
                    addedTime = System.currentTimeMillis().toString(),
                    updatedTime = "",
                    iconPosition = selectedIconPosition,
                )
            )
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.added_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        }

    }

    private fun initialize() {
        getDateFormatPattern = SharedPreferencesHelper(this).getDateFormatPattern()
        drawableList.addAll(IconHelper.iconList)
        binding.iconRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@AddAccountActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = IconAdapter(layoutManager as LinearLayoutManager) { selected ->
                selectedIconPosition = selected
            }.apply {
                setDataList(drawableList)
            }
        }
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                binding.headerLayout.elevation = 0f
                binding.headerLayout.translationZ = 0f
            } else {
                //  val firstItemHeight: Int = binding.titleLayout.height
                val firstItemHeight = 200
                val finalElevation: Float = dpToPx(3f)
                val setElevation = max(
                    0f,
                    min(finalElevation, scrollY.toFloat() / firstItemHeight * finalElevation)
                )
                binding.headerLayout.elevation = dpToPx(setElevation)
            }
        })


        categoryDataViewModel.getAllDataByIdASC.observe(this@AddAccountActivity) { data ->
            categoryList.clear()
            categoryIdList.clear()
            categoryList.add(getString(R.string.all_items))
            categoryIdList.add(0)
            if (data.isNotEmpty()) {
                data.forEach {
                    categoryList.add(it.title)
                    categoryIdList.add(it.id)
                }
            }
            binding.categoryAutoComplete.setAdapter(
                ArrayAdapter(
                    this@AddAccountActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    categoryList
                )
            )
        }


        binding.categoryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryPosition = categoryIdList[position]
        }


        binding.addCategory.setOnClickListener {
            addCategoryDialog()
        }


        val gilroyRegularTypeface =
            ResourcesCompat.getFont(this@AddAccountActivity, R.font.gilroy_regular)
        val gilroyBoldTypeface =
            ResourcesCompat.getFont(this@AddAccountActivity, R.font.gilroy_bold)

        binding.female.setOnClickListener {
            selectedGender = "female"
            binding.female.typeface = gilroyBoldTypeface
            binding.female.setStrokeColorResource(R.color.app_button_female)
            binding.female.backgroundTintList =
                ContextCompat.getColorStateList(this@AddAccountActivity, R.color.app_button_female)
            binding.female.setTextColor(
                ContextCompat.getColor(
                    this@AddAccountActivity,
                    R.color.gender_button_selected_text
                )
            )

            binding.male.typeface = gilroyRegularTypeface
            binding.male.setStrokeColorResource(R.color.gender_button_stroke)
            binding.male.backgroundTintList =
                ContextCompat.getColorStateList(this@AddAccountActivity, R.color.transparent)
            binding.male.setTextColor(
                ContextCompat.getColor(
                    this@AddAccountActivity,
                    R.color.gender_button_unselected_text
                )
            )
        }


        binding.male.setOnClickListener {
            selectedGender = "male"
            binding.male.typeface = gilroyBoldTypeface
            binding.male.setStrokeColorResource(R.color.app_button_male)
            binding.male.backgroundTintList =
                ContextCompat.getColorStateList(this@AddAccountActivity, R.color.app_button_male)
            binding.male.setTextColor(
                ContextCompat.getColor(
                    this@AddAccountActivity,
                    R.color.gender_button_selected_text
                )
            )

            binding.female.typeface = gilroyRegularTypeface
            binding.female.setStrokeColorResource(R.color.gender_button_stroke)
            binding.female.backgroundTintList =
                ContextCompat.getColorStateList(this@AddAccountActivity, R.color.transparent)
            binding.female.setTextColor(
                ContextCompat.getColor(
                    this@AddAccountActivity,
                    R.color.gender_button_unselected_text
                )
            )
        }


        binding.birthdayEditText.setOnClickListener {
            if (getDateFormatPattern == "") {
                dateFormatPatternDialog()
            } else {
                if (!isDatePickerShown) {
                    isDatePickerShown = true

                    val datePicker =
                        MaterialDatePicker.Builder.datePicker()
                            .setTitleText(getString(R.string.birthday))
                            .setSelection(Calendar.getInstance().timeInMillis)
                            .setTheme(R.style.CustomMaterialCalendar)
                            .build()
                    datePicker.addOnPositiveButtonClickListener {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it
                        val selectedDateString = SimpleDateFormat(
                            getDateFormatPattern,
                            Locale.getDefault()
                        ).format(calendar.time)
                        val editable: Editable =
                            Editable.Factory.getInstance().newEditable(selectedDateString)
                        binding.birthdayEditText.text = editable
                    }
                    datePicker.addOnNegativeButtonClickListener {
                        isDatePickerShown = false
                    }
                    datePicker.addOnCancelListener {
                        isDatePickerShown = false
                    }
                    datePicker.addOnDismissListener {
                        isDatePickerShown = false
                    }
                    datePicker.show(supportFragmentManager, "DATE_PICKER")
                }
            }
        }


        binding.passwordLayout.setEndIconDrawable(R.drawable.password_toggle_drawable)
        binding.passwordLayout.endIconMode =
            TextInputLayout.END_ICON_PASSWORD_TOGGLE
        binding.passwordEditText.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        if (isPasswordVisible) {
            binding.passwordEditText.letterSpacing = 0.4f
        } else {
            binding.passwordEditText.letterSpacing = 0.1f
        }

        binding.passwordLayout.setEndIconOnClickListener {
            val selectionStart = binding.passwordEditText.selectionStart
            val selectionEnd = binding.passwordEditText.selectionEnd
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.passwordEditText.letterSpacing = 0.4f
                binding.passwordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            } else {
                binding.passwordEditText.letterSpacing = 0.1f
                binding.passwordEditText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            }
            binding.passwordEditText.setSelection(selectionStart, selectionEnd)
        }
    }

    private fun addCategoryDialog() {
        if (addCategoryDialog == null) {
            addCategoryDialog = Dialog(this@AddAccountActivity, R.style.WideDialog)
            addCategoryDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
            addCategoryDialog?.window?.attributes?.windowAnimations = R.style.DialogZoomAnimation
            addCategoryDialog?.setContentView(R.layout.add_category_dialog)
            //   errorDialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addCategoryDialog?.setCancelable(true)
            val saveButton = addCategoryDialog?.findViewById<MaterialButton>(R.id.saveButton)
            val categoryLayout =
                addCategoryDialog?.findViewById<TextInputLayout>(R.id.category_layout)
            val categoryEditText =
                addCategoryDialog?.findViewById<TextInputEditText>(R.id.categoryEditText)

            categoryEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    if (text.isNotEmpty() && categoryLayout?.isErrorEnabled == true) {
                        categoryLayout.isErrorEnabled = false
                        categoryLayout.error = ""
                    }
                }
            })


            addCategoryDialog?.setOnDismissListener { addCategoryDialog = null }
            addCategoryDialog?.setOnCancelListener { addCategoryDialog = null }

            saveButton?.setOnClickListener {
                saveButton.isClickable = false
                saveButton.isFocusable = false
                saveButton.isEnabled = false

                if (categoryEditText?.text?.isEmpty() == true) {
                    if (categoryLayout != null) {
                        ShakeAnimatorHelper(
                            applicationContext,
                            categoryLayout,
                            500
                        ).startAnimation()
                        categoryLayout.isErrorEnabled = true
                        categoryLayout.error = getString(R.string.warning_category)
                    }
                    saveButton.isClickable = true
                    saveButton.isFocusable = true
                    saveButton.isEnabled = true

                } else {
                    if (categoryLayout != null) {
                        categoryLayout.isErrorEnabled = false
                        categoryLayout.error = ""
                    }
                    val getCategory = categoryEditText?.text.toString().trim()
                    if (getCategory == getString(R.string.all_items)) {
                        if (categoryLayout != null) {
                            ShakeAnimatorHelper(
                                applicationContext,
                                categoryLayout,
                                500
                            ).startAnimation()
                            categoryLayout.isErrorEnabled = true
                            categoryLayout.error = getString(R.string.warning_exist_category)
                        }
                        saveButton.isClickable = true
                        saveButton.isFocusable = true
                        saveButton.isEnabled = true
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            val categoryExists =
                                categoryDataViewModel.checkCategoryTitle(getCategory)
                            if (categoryExists) {
                                withContext(Dispatchers.Main) {
                                    if (categoryLayout != null) {
                                        ShakeAnimatorHelper(
                                            applicationContext,
                                            categoryLayout,
                                            500
                                        ).startAnimation()
                                        categoryLayout.isErrorEnabled = true
                                        categoryLayout.error =
                                            getString(R.string.warning_exist_category)
                                        saveButton.isClickable = true
                                        saveButton.isFocusable = true
                                        saveButton.isEnabled = true
                                    }
                                }
                            } else {
                                val lastInsertedID =
                                    categoryDataViewModel.insertDataLastID(CategoryEntity(title = getCategory))
                                runOnUiThread {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.added_successfully),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectedCategoryPosition = lastInsertedID.toInt()
                                    binding.categoryAutoComplete.setText(getCategory)
                                }
                                addCategoryDialog?.dismiss()
                            }
                        }
                    }
                }
            }
            if (!isFinishing && addCategoryDialog?.isShowing != true) {
                addCategoryDialog?.show()
            }
        }
    }

    private fun dateFormatPatternDialog() {
        if (dateFormatPatternDialog == null) {
            selectedPatternPosition = -1
            dateFormatPatternDialog = Dialog(this@AddAccountActivity, R.style.WideDialog)
            dateFormatPatternDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
            dateFormatPatternDialog?.window?.attributes?.windowAnimations =
                R.style.DialogZoomAnimation
            dateFormatPatternDialog?.setContentView(R.layout.date_format_pattern_dialog)
            //   errorDialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dateFormatPatternDialog?.setCancelable(true)
            val saveButton = dateFormatPatternDialog?.findViewById<MaterialButton>(R.id.saveButton)
            val autoComplete =
                dateFormatPatternDialog?.findViewById<AutoCompleteTextView>(R.id.autoComplete)
            val inputLayout =
                dateFormatPatternDialog?.findViewById<TextInputLayout>(R.id.inputLayout)


            dateFormatPatternDialog?.setOnDismissListener { dateFormatPatternDialog = null }
            dateFormatPatternDialog?.setOnCancelListener { dateFormatPatternDialog = null }
            val datePatternList: MutableList<String> = mutableListOf(
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "dd.MM.yyyy",
                "yyyy年MM月dd日",
                "yyyy년 MM월 dd일"
            )

            val datePatternTitles: MutableList<String> = mutableListOf(
                "Gün/Ay/Yıl",
                "Ay/Gün/Yıl",
                "Yıl-Ay-Gün",
                "Yıl/Ay/Gün",
                "Gün.Ay.Yıl",
                "Yıl年Ay月Gün日",
                "Yıl년 Ay월 Gün일"
            )

            autoComplete?.setAdapter(
                ArrayAdapter(
                    this@AddAccountActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    datePatternTitles
                )
            )

            autoComplete?.setOnItemClickListener { _, _, position, _ ->
                selectedPatternPosition = position
                if (inputLayout?.isErrorEnabled == true) {
                    inputLayout.isErrorEnabled = false
                    inputLayout.error = ""
                }
            }

            saveButton?.setOnClickListener {
                saveButton.isClickable = false
                saveButton.isFocusable = false
                saveButton.isEnabled = false

                if (autoComplete?.text?.isEmpty() == true) {
                    if (inputLayout != null) {
                        ShakeAnimatorHelper(applicationContext, inputLayout, 500).startAnimation()
                        inputLayout.isErrorEnabled = true
                        inputLayout.error = getString(R.string.pattern_warning)
                    }
                    saveButton.isClickable = true
                    saveButton.isFocusable = true
                    saveButton.isEnabled = true
                } else {
                    getDateFormatPattern = datePatternList[selectedPatternPosition]
                    SharedPreferencesHelper(this).setDateFormatPattern(getDateFormatPattern)
                    dateFormatPatternDialog?.dismiss()
                }
            }
            if (!isFinishing && dateFormatPatternDialog?.isShowing != true) {
                dateFormatPatternDialog?.show()
            }
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    @Suppress("DEPRECATION")
    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.from_left,
            R.anim.to_right
        )
    }
}