package com.example.foodmark.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RoundTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable (() -> Unit))? = null,
    // Focus-aware background colors:
    focusedBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBackgroundColor: Color = MaterialTheme.colorScheme.secondary
) {
    val shape = RoundedCornerShape(24.dp)
    val colors = MaterialTheme.colorScheme

    var focused by remember { mutableStateOf(false) }
    val bgColor = if (focused) focusedBackgroundColor else unfocusedBackgroundColor

    // Choose contrasting content color against the dynamic background
    val contentColor =
        when (bgColor) {
            MaterialTheme.colorScheme.primary -> MaterialTheme.colorScheme.onPrimary
            MaterialTheme.colorScheme.secondary -> MaterialTheme.colorScheme.onSecondary
            else -> MaterialTheme.colorScheme.onSurface
        }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = contentColor),
        cursorBrush = SolidColor(contentColor),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .onFocusChanged { focused = it.isFocused }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (placeholder != null && value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = contentColor.copy(alpha = 0.7f)
                        )
                    }
                    innerTextField()
                }
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides contentColor
                    ) {
                        trailingIcon()
                    }
                }
            }
        }
    )
}

@Composable
fun RoundPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    focusedBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
) {
    var showPassword by remember { mutableStateOf(false) }

    val transformation =
        if (showPassword) VisualTransformation.None else PasswordVisualTransformation()

    RoundTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        singleLine = singleLine,
        visualTransformation = transformation,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password
        ),
        trailingIcon = {
            val icon = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val contentDesc = if (showPassword) "Hide password" else "Show password"
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showPassword = !showPassword }
            )
        },
        focusedBackgroundColor = focusedBackgroundColor,
        unfocusedBackgroundColor = unfocusedBackgroundColor
    )
}

@Composable
fun RoundTextFieldWithTitle(
    title: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        RoundTextField(
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun RoundPasswordTextFieldWithTitle(
    title: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        RoundPasswordTextField(
            value = value,
            onValueChange = onValueChange
        )
    }
}
