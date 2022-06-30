package com.david_glez.seccion9_proyecto_stores.mainModule.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.david_glez.seccion9_proyecto_stores.StoreApplication
import com.david_glez.seccion9_proyecto_stores.common.entities.StoreEntity
import com.david_glez.seccion9_proyecto_stores.common.utils.StoresException
import com.david_glez.seccion9_proyecto_stores.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainInteractor { //Model
    //Coroutines
    val stores: LiveData<MutableList<StoreEntity>> = liveData {
        val storesLiveData = StoreApplication.dataBase.storeDao().getAllStores()
        emitSource(storesLiveData.map { stores ->
            stores.sortedBy { it.name }.toMutableList()
        })
    }

    suspend fun deleteStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        val result = StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.DELETE)
    }

    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        val result = StoreApplication.dataBase.storeDao().updateStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.UPDATE)
    }
}