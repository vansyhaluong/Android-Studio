package com.example.tuan1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tuan1.ThoiKhoaBieu
class TKBAdapter(
    private var listTKB: List<TKBTenMon>,
    private val role: String,
    private val onEdit: (TKBTenMon) -> Unit = {},
    private val onDelete: (TKBTenMon) -> Unit = {}
) : RecyclerView.Adapter<TKBAdapter.TKBViewHolder>() {

    inner class TKBViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTenMon: TextView = itemView.findViewById(R.id.tvTenMon1)
        val tvThuTiet: TextView = itemView.findViewById(R.id.tvThu)
        val tvTuan: TextView = itemView.findViewById(R.id.tvTuan)
        val btnEdit: ImageButton = itemView.findViewById(R.id.ibEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.ibDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TKBViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tkb, parent, false)
        return TKBViewHolder(view)
    }

    override fun onBindViewHolder(holder: TKBViewHolder, position: Int) {
        val tkb = listTKB[position]

        if (role != "admin") {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }
        holder.tvTenMon.text =tkb.tenMon
        holder.tvThuTiet.text = "Thứ ${tkb.thu} • Tiết ${tkb.tietBD} - ${tkb.tietKT}"
        holder.tvTuan.text = "Tuần ${tkb.tuan}"
        holder.btnEdit.setOnClickListener {
            onEdit(tkb)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(tkb)
        }
    }

    override fun getItemCount(): Int = listTKB.size

    fun updateData(newList: List<TKBTenMon>) {
        listTKB = newList
        notifyDataSetChanged()
    }


}
