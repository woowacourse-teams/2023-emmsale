package com.emmsale.presentation.utils.builder

import android.net.Uri

fun uri(block: Uri.Builder.() -> Unit): Uri = Uri.Builder().apply(block).build()
