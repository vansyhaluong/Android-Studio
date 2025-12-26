package com.example.tuan1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ThongKeAdapter(
    private var list: List<BangDiemThongKe>
) : RecyclerView.Adapter<ThongKeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTenMon: TextView = view.findViewById(R.id.tvTenMon)
        val tvDiemGK: TextView = view.findViewById(R.id.tvDiemGK)
        val tvDiemCK: TextView = view.findViewById(R.id.tvDiemCK)
        val tvDiemTK: TextView = view.findViewById(R.id.tvDiemTK)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thongke, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvTenMon.text = item.tenMon
        holder.tvDiemGK.text = "GK: %.1f".format(item.diemGK)
        holder.tvDiemCK.text = "CK: %.1f".format(item.diemCK)
        holder.tvDiemTK.text = "Tổng kết: %.1f".format(item.diemTK)
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<BangDiemThongKe>) {
        list = newList
        notifyDataSetChanged()
    }
}