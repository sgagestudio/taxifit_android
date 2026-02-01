package com.mm.taxifit.auth

import android.content.Intent
import com.mm.taxifit.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.createSupabaseClient

object SupabaseProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.TAXIFIT_SUPABASE_URL,
            supabaseKey = BuildConfig.TAXIFIT_SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "taxifit"
                host = "auth-callback"
                alwaysAutoRefresh = true
                autoLoadFromStorage = false
                autoSaveToStorage = false
            }
        }
    }

    fun handleDeepLink(intent: Intent?) {
        if (intent != null) {
            client.handleDeeplinks(intent)
        }
    }
}
