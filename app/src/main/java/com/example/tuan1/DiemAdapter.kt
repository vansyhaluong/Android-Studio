package com.example.tuan1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiemAdapter(
    private var listDiem: List<Diem>,
    listMonHoc: List<DB_MonHoc>,
    private val role: String,
    private val onEdit: ((Diem) -> Unit)? = null,
    private val onDelete: ((Diem) -> Unit)? = null,
    private val onItemClick: ((Diem) -> Unit)? = null
) : RecyclerView.Adapter<DiemAdapter.DiemViewHolder>() {

    private val monHocMap = listMonHoc.associateBy { it.maMon }

    inner class DiemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTenMon: TextView = itemView.findViewById(R.id.tvTenMon)
        val tvGK: TextView = itemView.findViewById(R.id.tvDiemGK)
        val tvCK: TextView = itemView.findViewById(R.id.tvDiemCK)
        val ibEdit: ImageButton=itemView.findViewById(R.id.ibEdit)
        val ibDelete: ImageButton=itemView.findViewById(R.id.ibDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diem, parent, false)
        return DiemViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiemViewHolder, position: Int) {
        val diem = listDiem[position]
        val mon = monHocMap[diem.maMon]
        if (role != "admin") {
            holder.ibEdit.visibility = View.GONE
            holder.ibDelete.visibility = View.GONE
        }
        holder.tvTenMon.text = mon?.tenMon ?: ""
        holder.tvGK.text = diem.diemGK.toString()
        holder.tvCK.text = diem.diemCK.toString()

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(diem)
        }
        holder.ibEdit.setOnClickListener {
            onEdit?.invoke(diem)
        }

        // Click delete
        holder.ibDelete.setOnClickListener {
            onDelete?.invoke(diem)
        }
    }
    fun updateData(newList: List<Diem>) {
        listDiem = newList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = listDiem.size
}

