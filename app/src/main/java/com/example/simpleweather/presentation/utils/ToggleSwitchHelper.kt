package com.example.simpleweather.presentation.utils

import android.view.View
import android.widget.Button

class ToggleSwitchHelper(
    private val toggleGroup: View,
    private val toggleIndicator: View,
    private val button1: Button,
    private val button2: Button,
    private val isFirstSelected: Boolean = true,
    private val onSelectionChanged: (Boolean) -> Unit
) {
    private var isFirstOption: Boolean = isFirstSelected

    fun setup() {
        // Init state ban đầu
        updateButtonStates()

        // Đợi layout được measure xong rồi mới set indicator position
        toggleGroup.post {
            updateIndicatorPosition()
        }

        // Handle click event for button 1
        button1.setOnClickListener {
            if (!isFirstOption) {
                isFirstOption = true
                updateIndicatorPosition()
                updateButtonStates()
                onSelectionChanged(true)
            }
        }

        // Handle click event for button 2
        button2.setOnClickListener {
            if (isFirstOption) {
                isFirstOption = false
                updateIndicatorPosition()
                updateButtonStates()
                onSelectionChanged(false)
            }
        }
    }

    private fun updateButtonStates() {
        // Update checked state to trigger selector
        button1.isSelected = isFirstOption
        button2.isSelected = !isFirstOption

        // Refresh drawable state for selector to update
        button1.refreshDrawableState()
        button2.refreshDrawableState()
    }

    private fun updateIndicatorPosition() {
        // Calculate button width (50% each)
        val buttonWidth = (toggleGroup.width - toggleGroup.paddingLeft - toggleGroup.paddingRight) / 2

        // Nếu width = 0, layout chưa được measure, bỏ qua
        if (buttonWidth <= 0) return

        // Calculate indicator position
        val targetX = if (isFirstOption) 0f else buttonWidth.toFloat()

        // Set initial width trước
        val layoutParams = toggleIndicator.layoutParams
        layoutParams.width = buttonWidth
        toggleIndicator.layoutParams = layoutParams

        // Set position (không animate nếu là lần đầu setup)
        val currentX = toggleIndicator.x
        if (currentX == 0f && isFirstOption) {
            // Lần đầu setup, set trực tiếp không animate
            toggleIndicator.x = targetX + toggleGroup.paddingStart
        } else {
            // Các lần sau, animate
            toggleIndicator.animate()
                .x(targetX + toggleGroup.paddingStart)
                .setDuration(200)
                .start()
        }
    }

    fun getCurrentSelection(): Boolean = isFirstOption
}