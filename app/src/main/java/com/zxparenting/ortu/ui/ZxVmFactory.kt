package com.zxparenting.ortu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zxparenting.ortu.data.Simpanan

class ZxVmFactory(val simpanan: Simpanan) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ZxVm(simpanan) as T
}
