package com.epaperlauncher.feature.launcherhome.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboard
import androidx.compose.ui.text.input.ImeAction
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.core.ui.components.PaperSearchField
import com.epaperlauncher.core.ui.theme.PaperTheme

/**
 * Minimalist search bar for E-Paper Launcher home screen
 * - No outlines, no shadows
 * - Subtle background fill
 * - Clean typography
 * - Simple close button
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search apps..."
) {
    val keyboardController = LocalSoftwareKeyboard.current
    val focusRequester = remember { FocusRequester() }
    val colors = PaperTheme.colors
    
    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxWidth()
    ) {
        PaperSearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = placeholder,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        
        // Hairline divider below search
        com.epaperlauncher.core.ui.components.PaperDivider(
            modifier = Modifier.fillMaxWidth()
        )
    }
}
