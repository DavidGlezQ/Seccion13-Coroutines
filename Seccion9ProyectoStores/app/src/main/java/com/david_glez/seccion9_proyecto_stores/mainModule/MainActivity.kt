package com.david_glez.seccion9_proyecto_stores.mainModule


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.david_glez.seccion9_proyecto_stores.common.entities.StoreEntity
import com.david_glez.seccion9_proyecto_stores.editModule.EditStoreFragment
import com.david_glez.seccion9_proyecto_stores.R
import com.david_glez.seccion9_proyecto_stores.common.utils.TypeError
import com.david_glez.seccion9_proyecto_stores.databinding.ActivityMainBinding
import com.david_glez.seccion9_proyecto_stores.editModule.viewModel.EditStoreViewModel
import com.david_glez.seccion9_proyecto_stores.mainModule.adapters.OnClickListener
import com.david_glez.seccion9_proyecto_stores.mainModule.adapters.StoreListAdapter
import com.david_glez.seccion9_proyecto_stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {

    //Clase 132
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreListAdapter
    private lateinit var mGridLayout: GridLayoutManager

    // MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fabAddStore.setOnClickListener { launchEditFragment() }

        setUpViewModel()
        setupRecyclerView()
    }

    // inicializar el viewModel y observar los cambios para la interface
    private fun setUpViewModel() {
        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mMainViewModel.getStores().observe(this) { stores->
            mBinding.progressBar.visibility = View.GONE
            mAdapter.submitList(stores)
        }

        mMainViewModel.isShowProgress().observe(this) { isShowProgress ->
            mBinding.progressBar.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }

        mMainViewModel.getTypeError().observe(this) { typeError ->
            val msgRes = when(typeError){
                TypeError.GET -> getString(R.string.main_error_get)
                TypeError.INSERT -> getString(R.string.main_error_insert)
                TypeError.UPDATE-> getString(R.string.main_error_update)
                TypeError.DELETE -> getString(R.string.error_main_delete)
                else -> getString(R.string.main_error)
            }
            Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
        }

        mEditStoreViewModel = ViewModelProvider(this)[EditStoreViewModel::class.java]
        mEditStoreViewModel.getShowFab().observe(this){ isVisible ->
            if (isVisible) mBinding.fabAddStore.show() else mBinding.fabAddStore.hide()
        }
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)

        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null) // Destruir el fragment del stack
        fragmentTransaction.commit()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreListAdapter( this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))

        mBinding.recyclerView.apply {
            setHasFixedSize(true) // Optimizacion de recursos
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mEditStoreViewModel.setShowFab(true)
    }

    /*
    * OnClickListener Interface
    * */
    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_option_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, i ->
                when(i){
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWebSIte(storeEntity.webSite)
                }
            }.show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dilaog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm)  { _, _ ->
                mMainViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null).show()
    }

    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel: $phone")
        }
        //A partir del API 30 es necesario agregar un queri en el manifest, por seguridad.
        startIntent(callIntent)
    }

    private fun goToWebSIte(webSite: String) {
        if (webSite.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_SHORT).show()
        } else {
            val webSiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(webSite)
            }
            startIntent(webSiteIntent)
        }
    }

    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        else Toast.makeText(this, R.string.main_error_no_result, Toast.LENGTH_SHORT).show()
    }
}