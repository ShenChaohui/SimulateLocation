package com.sch.simulatelocation.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sch.simulatelocation.R
import com.sch.simulatelocation.entrys.HistoryLocation

/**
 * Created by Sch.
 * Date: 2021/1/6
 * description:
 */
class HistoryLocationAdapter :
    BaseQuickAdapter<HistoryLocation, BaseViewHolder>(R.layout.item_location) {
    override fun convert(holder: BaseViewHolder, item: HistoryLocation) {
        holder.setText(R.id.tvItemAddress, item.formatted_address)
        holder.setText(R.id.tvItemLocation, item.location)
    }
}