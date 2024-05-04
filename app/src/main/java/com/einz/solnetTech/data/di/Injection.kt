package com.einz.solnetTech.data.di

import android.content.Context
import com.einz.solnetTech.data.Repository

object Injection {
    fun provideRepository(context: Context) : Repository {
        return Repository(context)
    }
}