package com.example.birdyapp.features.searching_by_name.view

import com.example.birdyapp.R
import com.example.birdyapp.db.OfflineBirdsModel
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_bird_item.*
class OfflineBirdItem(
    private val model: OfflineBirdsModel
): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            //TODO
        }
    }

    override fun getLayout(): Int = R.layout.layout_bird_item
}