package com.mkdevelopment.myaccounts.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.database.AccountEntity
import com.mkdevelopment.myaccounts.databinding.MainItemBinding
import com.mkdevelopment.myaccounts.viewmodel.AccountDataViewModel

class AccountAdapter(
    private val activity: Activity,
    private val accountDataViewModel: AccountDataViewModel
) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {
    private var dataList = emptyList<AccountEntity>()
    private var zoomItemValues = 0.95f
    private var animationDuration = 100L
    private var deleteDialog: Dialog? = null
    private var isLongClick = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder(
            MainItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.binding.mainItem.setOnClickListener {
            showPopup(holder.binding.mainItem, currentItem)
        }

        holder.binding.mainItem.setOnLongClickListener {
            isLongClick = true
            zoomInItem(holder.binding.mainItem)
            deleteDialog(currentItem, holder.binding.mainItem)
            true
        }
        holder.binding.textTitle.text = currentItem.title
        val username = currentItem.username
        val email = currentItem.email

        if (username.isEmpty() && email.isEmpty()) {
            holder.binding.textDesc.visibility = View.GONE
        } else {
            holder.binding.textDesc.visibility = View.VISIBLE
            holder.binding.textDesc.text = email.ifEmpty { username }
        }

        if (currentItem.password == "") {
            holder.binding.textPassword.visibility = View.INVISIBLE
        } else {
            holder.binding.textPassword.visibility = View.VISIBLE
            val passwordLength = currentItem.password.length
            val hiddenPassword = "*".repeat(passwordLength)
            holder.binding.textPassword.text = hiddenPassword
        }
        holder.binding.imageView.setImageResource(currentItem.icon)
    }


    @SuppressLint("InflateParams")
    private fun showPopup(mainItem: MaterialCardView, currentItem: AccountEntity) {
        zoomInItem(mainItem)
        val popupView = activity.layoutInflater.inflate(R.layout.custom_popup, null)
        val titleList: MutableList<String> = mutableListOf()
        val dataList: MutableList<String> = mutableListOf()
        val messageList: MutableList<String> = mutableListOf()

        if (currentItem.name.isNotEmpty()) {
            titleList.add(activity.getString(R.string.name_copy))
            dataList.add(currentItem.name)
            messageList.add(activity.getString(R.string.name_copy_message))
        }

        if (currentItem.surname.isNotEmpty()) {
            titleList.add(activity.getString(R.string.surname_copy))
            dataList.add(currentItem.surname)
            messageList.add(activity.getString(R.string.surname_copy_message))
        }

        if (currentItem.username.isNotEmpty()) {
            titleList.add(activity.getString(R.string.username_copy))
            dataList.add(currentItem.username)
            messageList.add(activity.getString(R.string.username_copy_message))
        }

        if (currentItem.email.isNotEmpty()) {
            titleList.add(activity.getString(R.string.email_copy))
            dataList.add(currentItem.email)
            messageList.add(activity.getString(R.string.email_copy_message))
        }

        if (currentItem.recoveryEmail.isNotEmpty()) {
            titleList.add(activity.getString(R.string.recovery_email_copy))
            dataList.add(currentItem.recoveryEmail)
            messageList.add(activity.getString(R.string.recovery_email_copy_message))
        }

        if (currentItem.recoveryPhone.isNotEmpty()) {
            titleList.add(activity.getString(R.string.recovery_phone_copy))
            dataList.add(currentItem.recoveryPhone)
            messageList.add(activity.getString(R.string.recovery_phone_copy_message))
        }

        if (currentItem.phone.isNotEmpty()) {
            titleList.add(activity.getString(R.string.phone_copy))
            dataList.add(currentItem.phone)
            messageList.add(activity.getString(R.string.phone_copy_message))
        }

        if (currentItem.password.isNotEmpty()) {
            titleList.add(activity.getString(R.string.password_copy))
            dataList.add(currentItem.password)
            messageList.add(activity.getString(R.string.password_copy_message))
        }

        if (currentItem.title.isNotEmpty()) {
            titleList.add(activity.getString(R.string.title_copy))
            dataList.add(currentItem.title)
            messageList.add(activity.getString(R.string.title_copy_message))
        }

        if (currentItem.birthday.isNotEmpty()) {
            titleList.add(activity.getString(R.string.birthday_copy))
            dataList.add(currentItem.birthday)
            messageList.add(activity.getString(R.string.birthday_copy_message))
        }

        if (currentItem.securityQuestion.isNotEmpty()) {
            titleList.add(activity.getString(R.string.security_question_copy))
            dataList.add(currentItem.securityQuestion)
            messageList.add(activity.getString(R.string.security_question_copy_message))
        }

        if (currentItem.securityQuestionAnswer.isNotEmpty()) {
            titleList.add(activity.getString(R.string.security_question_answer_copy))
            dataList.add(currentItem.securityQuestionAnswer)
            messageList.add(activity.getString(R.string.security_question_answer_copy_message))
        }

        if (currentItem.address.isNotEmpty()) {
            titleList.add(activity.getString(R.string.address_copy))
            dataList.add(currentItem.address)
            messageList.add(activity.getString(R.string.address_copy_message))
        }

        if (currentItem.other.isNotEmpty()) {
            titleList.add(activity.getString(R.string.other_informations_copy))
            dataList.add(currentItem.other)
            messageList.add(activity.getString(R.string.other_informations_copy_message))
        }

        titleList.add(activity.getString(R.string.delete_account))
        dataList.add("delete_item")
        messageList.add(activity.getString(R.string.delete_account_message))


        val elevation = activity.resources.getDimension(R.dimen._3sdp).toInt()
        val marginOffset = activity.resources.getDimension(R.dimen._6sdp).toInt()
        val screenWidth = activity.resources.displayMetrics.widthPixels
        val screenHeight = activity.resources.displayMetrics.heightPixels


        val popupWindow = PopupWindow(
            popupView,
            screenWidth / 2,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.isFocusable = true
        popupWindow.elevation = elevation.toFloat()

        popupWindow.elevation = activity.resources.getDimension(R.dimen._3sdp)

        val location = IntArray(2)
        mainItem.getLocationOnScreen(location)

        val x = location[0] + mainItem.width - popupWindow.width - marginOffset

        val y = if (location[1] + mainItem.height + popupWindow.height > screenHeight) {
            location[1] - popupWindow.height
        } else {
            location[1] + mainItem.height - marginOffset
        }

        popupWindow.showAtLocation(mainItem, Gravity.START or Gravity.TOP, x, y)

        popupWindow.setOnDismissListener {
            zoomOutItem(mainItem)
        }


        val recyclerView = popupView.findViewById<RecyclerView>(R.id.main_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                val listItem = titleList[position]
                val viewHolder = holder as ViewHolder

                if (listItem == activity.getString(R.string.delete_account)) {
                    viewHolder.textView.setTextColor(
                        ContextCompat.getColor(
                            mainItem.context,
                            R.color.dialog_on_container_urgent
                        )
                    )
                } else {
                    viewHolder.textView.setTextColor(
                        ContextCompat.getColor(
                            mainItem.context,
                            R.color.dialog_on_container
                        )
                    )
                }
                viewHolder.textView.text = listItem

                holder.itemView.setOnClickListener {
                    if (dataList[position] == "delete_item") {
                        deleteDialog(currentItem, mainItem)
                    } else {
                        Toast.makeText(activity, messageList[position], Toast.LENGTH_SHORT).show()
                        val clipboardManager =
                            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText(null, dataList[position])
                        clipboardManager.setPrimaryClip(clipData)
                    }

                    if (popupView != null) {
                        popupWindow.dismiss()
                    }
                }
            }

            override fun getItemCount(): Int {
                return titleList.size
            }

            inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val textView: TextView = itemView.findViewById(R.id.textView)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class AccountViewHolder(val binding: MainItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun zoomInItem(mainItem: MaterialCardView) {
        val originalWidth = mainItem.width
        val originalHeight = mainItem.height

        val targetWidth = (originalWidth * zoomItemValues).toInt()
        val targetHeight = (originalHeight * zoomItemValues).toInt()

        val originalX = mainItem.x
        val originalY = mainItem.y
        val originalCenterX = originalX + originalWidth / 2
        val originalCenterY = originalY + originalHeight / 2

        val targetX = originalX + (originalWidth - targetWidth) / 2
        val targetY = originalY + (originalHeight - targetHeight) / 2
        val targetCenterX = targetX + targetWidth / 2
        val targetCenterY = targetY + targetHeight / 2

        val scaleX =
            ObjectAnimator.ofFloat(mainItem, View.SCALE_X, 1f, zoomItemValues)
        val scaleY =
            ObjectAnimator.ofFloat(mainItem, View.SCALE_Y, 1f, zoomItemValues)
        val moveX = ObjectAnimator.ofFloat(
            mainItem,
            View.TRANSLATION_X,
            0f,
            targetCenterX - originalCenterX
        )
        val moveY = ObjectAnimator.ofFloat(
            mainItem,
            View.TRANSLATION_Y,
            0f,
            targetCenterY - originalCenterY
        )

        AnimatorSet().apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            playTogether(scaleX, scaleY, moveX, moveY)
            start()
        }

        mainItem.strokeColor =
            ContextCompat.getColor(mainItem.context, R.color.item_selected_stroke)
    }

    private fun zoomOutItem(mainItem: MaterialCardView) {
        val originalWidth = mainItem.width
        val originalHeight = mainItem.height

        val targetWidth = (originalWidth * 1.0).toInt()
        val targetHeight = (originalHeight * 1.0).toInt()

        val originalX = mainItem.x
        val originalY = mainItem.y
        val originalCenterX = originalX + originalWidth / 2
        val originalCenterY = originalY + originalHeight / 2

        val targetX = originalX + (originalWidth - targetWidth) / 2
        val targetY = originalY + (originalHeight - targetHeight) / 2
        val targetCenterX = targetX + targetWidth / 2
        val targetCenterY = targetY + targetHeight / 2

        val scaleX =
            ObjectAnimator.ofFloat(mainItem, View.SCALE_X, zoomItemValues, 1f)
        val scaleY =
            ObjectAnimator.ofFloat(mainItem, View.SCALE_Y, zoomItemValues, 1f)
        val moveX = ObjectAnimator.ofFloat(
            mainItem,
            View.TRANSLATION_X,
            targetCenterX - originalCenterX,
            0f
        )
        val moveY = ObjectAnimator.ofFloat(
            mainItem,
            View.TRANSLATION_Y,
            targetCenterY - originalCenterY,
            0f
        )

        AnimatorSet().apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            playTogether(scaleX, scaleY, moveX, moveY)
            start()
        }

        mainItem.strokeColor =
            ContextCompat.getColor(mainItem.context, R.color.app_surface_container_low)
    }


    private fun deleteDialog(currentItem: AccountEntity, cardView: MaterialCardView) {
        if (deleteDialog == null) {
            deleteDialog = Dialog(activity, R.style.WideDialog)
            deleteDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
            deleteDialog?.window?.attributes?.windowAnimations =
                R.style.DialogZoomAnimation
            deleteDialog?.setContentView(R.layout.delete_dialog)
            deleteDialog?.setCancelable(true)
            val yesButton = deleteDialog?.findViewById<MaterialButton>(R.id.yesButton)
            val noButton = deleteDialog?.findViewById<MaterialButton>(R.id.noButton)
            val title = deleteDialog?.findViewById<TextView>(R.id.title)
            val desc = deleteDialog?.findViewById<TextView>(R.id.desc)

            title?.text = activity.getString(R.string.delete_account)
            desc?.text = activity.getString(R.string.delete_account_desc)

            deleteDialog?.setOnDismissListener {
                if (isLongClick) {
                    zoomOutItem(cardView)
                    isLongClick = false
                }
                deleteDialog = null
            }
            deleteDialog?.setOnCancelListener {
                if (isLongClick) {
                    zoomOutItem(cardView)
                    isLongClick = false
                }
                deleteDialog = null
            }

            noButton?.setOnClickListener {
                deleteDialog?.dismiss()
            }

            yesButton?.setOnClickListener {
                accountDataViewModel.deleteData(currentItem)
                Toast.makeText(
                    activity,
                    activity.getString(R.string.delete_account_message),
                    Toast.LENGTH_SHORT
                ).show()
                deleteDialog?.dismiss()
            }

            if (!activity.isFinishing && deleteDialog?.isShowing != true) {
                deleteDialog?.show()
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(accountDataList: List<AccountEntity>) {
        dataList = accountDataList
        notifyDataSetChanged()
    }

}

