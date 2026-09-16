package com.epaperlauncher.iconengine.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.epaperlauncher.core.ui.PaperTokens
import com.epaperlauncher.iconengine.domain.repository.IconRepository

/**
 * Composable that displays a processed monochrome icon for an app
 * Falls back to system icon if processed version not available
 */
@Composable
fun ProcessedIcon(
    packageName: String,
    modifier: Modifier = Modifier,
    iconRepository: IconRepository,
    contentDescription: String? = null
) {
    val context = LocalContext.current
    val iconPath by iconRepository.observeIconPath(packageName).collectAsState(initial = null)
    
    val imageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .build()
    
    val request = ImageRequest.Builder(context)
        .data(iconPath ?: packageName) // Use package name as fallback (Coil will resolve)
        .placeholder(android.R.drawable.sym_def_app_icon)
        .error(android.R.drawable.sym_def_app_icon)
        .build()
    
    Image(
        painter = rememberAsyncImagePainter(request, imageLoader),
        contentDescription = contentDescription,
        modifier = modifier.size(48.dp)
    )
}

/**
 * Simple icon display with paper-tone tinting applied via ColorMatrix
 * Used when processed icon not yet available
 */
@Composable
fun TintedAppIcon(
    drawableResId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    // For now, just display the standard icon
    // Future enhancement: apply ColorMatrix filter via Coil transformation
    Image(
        painter = androidx.compose.ui.res.painterResource(drawableResId),
        contentDescription = contentDescription,
        modifier = modifier.size(48.dp)
    )
}
