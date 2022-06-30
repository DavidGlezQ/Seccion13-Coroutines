package com.david_glez.seccion9_proyecto_stores.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.david_glez.seccion9_proyecto_stores.common.entities.StoreEntity

@Database(entities = [StoreEntity::class], version = 3)
abstract class StoreDataBase : RoomDatabase(){
    abstract fun storeDao(): StoreDao
}