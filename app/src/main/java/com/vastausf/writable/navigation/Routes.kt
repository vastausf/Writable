package com.vastausf.writable.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class EditorRoute(val documentId: Long)
