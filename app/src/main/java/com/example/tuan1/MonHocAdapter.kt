package com.example.tuan1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MonHocAdapter(
    private var listMH: List<DB_MonHoc>,
    private val role: String,
    private val listHocKy: List<HocKy>,
    private val onEditClick: (DB_MonHoc) -> Unit,
    private val onDeleteClick: (DB_MonHoc) -> Unit
) : RecyclerView.Adapter<MonHocAdapter.MonHocViewHolder>() {

    inner class MonHocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTenMon: TextView = itemView.findViewById(R.id.txtTenMon)
        val txtMaMon: TextView = itemView.findViewById(R.id.txtMaMon)
        val txtTinChi: TextView = itemView.findViewById(R.id.txtTinChi)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val layoutAdmin: LinearLayout = itemView.findViewById(R.id.layoutAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonHocViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycleview, parent, false)
        return MonHocViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonHocViewHolder, position: Int) {
        val monHoc = listMH[position]
        if (role != "admin") {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }
        else{
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
        }
        // Gán dữ liệu
        holder.txtTenMon.text = monHoc.tenMon
        holder.txtMaMon.text = "Mã môn: ${monHoc.maMon}"
        holder.txtTinChi.text = "Số tín chỉ: ${monHoc.tinChi}"

        holder.btnEdit.setOnClickListener {
            onEditClick(monHoc)
        }
        holder.btnDelete.setOnClickListener {
            onDeleteClick(monHoc)
        }
    }

    override fun getItemCount(): Int = listMH.size
    fun updateData(newList: List<DB_MonHoc>) {
        listMH = newList
        notifyDataSetChanged()
    }
}
