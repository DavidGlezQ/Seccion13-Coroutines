package com.david_glez.seccion9_proyecto_stores.mainModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.david_glez.seccion9_proyecto_stores.common.entities.StoreEntity
import com.david_glez.seccion9_proyecto_stores.R
import com.david_glez.seccion9_proyecto_stores.databinding.ItemStoreBinding

class StoreListAdapter(private var listener: OnClickListener):
    ListAdapter<StoreEntity, RecyclerView.ViewHolder>(StoreDiffCallback()){

    private lateinit var mContext: Context // La 'm' se refiere a que es un miembro de la clase

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val mBinding = ItemStoreBinding.bind(view) // Creamos en binding para el item

        fun setListener(storeEntity: StoreEntity){
            with(mBinding.root){
                setOnClickListener { listener.onClick(storeEntity) }
                setOnLongClickListener{
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }
            mBinding.cbFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }
        }
    }

    //Methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val store = getItem(position)

        with(holder as ViewHolder){
            setListener(store)

            mBinding.tvName.text = store.name
            mBinding.cbFavorite.isChecked = store.isFavorite
            Glide.with(mContext)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }
    }

    class StoreDiffCallback: DiffUtil.ItemCallback<StoreEntity>(){
        override fun areItemsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
            return oldItem == newItem
        }

    }
}