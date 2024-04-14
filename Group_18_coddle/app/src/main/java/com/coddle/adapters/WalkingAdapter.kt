package com.coddle.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.coddle.databinding.WalkingItemBinding
import com.coddle.model.Walking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WalkingAdapter : RecyclerView.Adapter<WalkingAdapter.ViewHolder>() {

    inner class MyDiffUtil(
        private val oldList: List<Walking>,
        private val newList: List<Walking>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    private val walking: ArrayList<Walking> = arrayListOf()
    private var onClickListener:((Walking)->Unit)? = null

    class ViewHolder(val binding: WalkingItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            WalkingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            "Distance : ${ walking[position].distance}".also { txtDistance.text = it }
            "Steps : ${ walking[position].steps}".also { txtSteps.text = it }
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            txtDate.text = sdf.format(Date(walking[position].date.toLong()))

            layout.setOnClickListener {
                onClickListener?.invoke(walking[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return walking.size
    }

    override fun getItemViewType(position: Int): Int {
        return (position)
    }

    fun setData(newList: List<Walking>){
        val diffResult = DiffUtil.calculateDiff(MyDiffUtil(walking,newList))
        walking.clear()
        walking.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnclickListener(listener:(Walking)->Unit){
        onClickListener = listener
    }

}