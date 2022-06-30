package com.david_glez.seccion9_proyecto_stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david_glez.seccion9_proyecto_stores.common.entities.StoreEntity
import com.david_glez.seccion9_proyecto_stores.common.utils.Constants
import com.david_glez.seccion9_proyecto_stores.common.utils.StoresException
import com.david_glez.seccion9_proyecto_stores.common.utils.TypeError
import com.david_glez.seccion9_proyecto_stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() { // Aqui se alamcenan todas las tiendas de nuestro modelo

    // esta clase se comunica con la vista y por getStores puede devolver el resultado
    /*tambien tiene acceso al modelo, en este caso el interactor (MainInteractor) ViewModel*/
    private var interactor: MainInteractor

    init {
        interactor = MainInteractor()
    }

    private val typeError: MutableLiveData<TypeError> = MutableLiveData()
    private val showProgress: MutableLiveData<Boolean> = MutableLiveData()

    //Coroutines
    private val stores = interactor.stores

    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun isShowProgress(): LiveData<Boolean>{
        return showProgress
    }

    fun deleteStore(storeEntity: StoreEntity){
        //Coroutines
        executeAction { interactor.deleteStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity){
        //Coroutines
        storeEntity.isFavorite = !storeEntity.isFavorite
        executeAction { interactor.updateStore(storeEntity) }
    }

    private fun executeAction(block: suspend () -> Unit): Job{
        return viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                block()
            } catch (e: StoresException){
                e.printStackTrace()
                typeError.value = e.typeError
            } finally {
                showProgress.value = Constants.HIDE
            }
        }
    }
}