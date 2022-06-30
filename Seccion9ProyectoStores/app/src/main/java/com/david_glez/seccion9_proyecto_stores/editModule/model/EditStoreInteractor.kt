package com.david_glez.seccion9_proyecto_stores.editModule.model

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.david_glez.seccion9_proyecto_stores.StoreApplication
import com.david_glez.seccion9_proyecto_stores.common.entities.StoreEntity
import com.david_glez.seccion9_proyecto_stores.common.utils.StoresException
import com.david_glez.seccion9_proyecto_stores.common.utils.TypeError
import kotlinx.coroutines.Dispatchers

class EditStoreInteractor {

    fun getStoreById(id: Long): LiveData<StoreEntity>{
        return StoreApplication.dataBase.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity) = with(Dispatchers.IO){
        try {
            StoreApplication.dataBase.storeDao().addStore(storeEntity)
        } catch (e: SQLiteConstraintException){
            e.printStackTrace()
            throw StoresException(TypeError.INSERT)
        }
    }

    suspend fun updateStore(storeEntity: StoreEntity) = with(Dispatchers.IO){
        try {
            val result = StoreApplication.dataBase.storeDao().updateStore(storeEntity)
            if (result == 0) throw StoresException(TypeError.UPDATE)
        }catch (e: SQLiteConstraintException){
            throw StoresException(TypeError.UPDATE)
        }
    }
}