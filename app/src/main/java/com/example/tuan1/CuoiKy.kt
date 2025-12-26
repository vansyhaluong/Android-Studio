package com.example.tuan1

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat


class CuoiKy : AppCompatActivity() {
    lateinit var cvQlMonHoc: CardView
    lateinit var cvNhapdiem: CardView
    lateinit var cvTHongke: CardView
    lateinit var cvTkb: CardView
    lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = intent.getStringExtra("USER_ROLE") ?: "user"

        setContentView(R.layout.activity_cuoi_ky)
        setControl()
        setEvent()
    }
    private fun setControl(){
        cvNhapdiem=findViewById(R.id.nhapdiem)
        cvQlMonHoc=findViewById(R.id.ql_monhoc)
        cvTHongke=findViewById(R.id.thongke)
        cvTkb=findViewById(R.id.tkb)
    }
    private fun setEvent(){
        cvQlMonHoc.setOnClickListener {
            val intent = Intent(this, MonHoc::class.java)
            intent.putExtra("USER_ROLE", role)
            startActivity(intent)
        }
        cvNhapdiem.setOnClickListener {
            val intent = Intent(this, ActivityNhapDiem::class.java)
            intent.putExtra("USER_ROLE", role)
            startActivity(intent)
        }
        cvTkb.setOnClickListener {
            val intent = Intent(this, ActivityTKB::class.java)
            intent.putExtra("USER_ROLE", role)
            startActivity(intent)
        }
        cvTHongke.setOnClickListener {
            val intent = Intent(this, ActivityThongKe::class.java)
            intent.putExtra("USER_ROLE", role)
            startActivity(intent)
        }

    }
}