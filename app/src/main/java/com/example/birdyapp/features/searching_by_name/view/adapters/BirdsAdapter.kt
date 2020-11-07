package com.example.birdyapp.features.searching_by_name.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.birdyapp.base.BaseAdapter
import com.example.birdyapp.databinding.LayoutBirdItemBinding
import com.example.birdyapp.features.searching_by_name.model.BirdModel

class BirdsAdapter :
    BaseAdapter<BirdModel, BirdsAdapter.BirdsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdsViewHolder =
        BirdsViewHolder(
            LayoutBirdItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BirdsViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onClick?.invoke(getItem(position))
        }
    }

    inner class BirdsViewHolder(private val binding: LayoutBirdItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BirdModel) {
            binding.bird = item
        }
    }

}