package com.mm.taxifit.data.local

import android.content.Context

class LocalUserStorage(context: Context) {
    private val dao = TaxifitDatabase.getInstance(context).localUserDao()

    suspend fun save(id: String, email: String) {
        dao.upsert(LocalUserEntity(id = id, email = email))
    }

    suspend fun clear() {
        dao.clear()
    }
}
