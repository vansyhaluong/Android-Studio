package com.example.tuan1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ActivityThongKe : AppCompatActivity() {
    lateinit var rvHK1: RecyclerView
    lateinit var rvHK2: RecyclerView
    lateinit var tvGPAHK1: TextView
    lateinit var tvGPAHK2: TextView
    lateinit var tvHocLucHK1: TextView
    lateinit var tvHocLucHK2: TextView

    lateinit var adapterHK1: ThongKeAdapter
    lateinit var adapterHK2: ThongKeAdapter
    lateinit var toolbar: Toolbar
    lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_thong_ke)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        setEvent()
        adapterHK1 = ThongKeAdapter(emptyList())
        adapterHK2 = ThongKeAdapter(emptyList())
        rvHK1.layoutManager = LinearLayoutManager(this)
        rvHK2.layoutManager = LinearLayoutManager(this)
        rvHK1.adapter = adapterHK1
        rvHK2.adapter = adapterHK2

        db = AppDatabase.getInstance(this)
        loadData()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_thongke, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_home -> startActivity(Intent(this, CuoiKy::class.java))
            R.id.menu_monhoc -> startActivity(Intent(this, MonHoc::class.java))
            R.id.menu_tkb -> startActivity(Intent(this, ActivityTKB::class.java))
            R.id.menu_nhapdiem -> startActivity(Intent(this, ActivityNhapDiem::class.java))
            R.id.menu_thoatTK -> startActivity(Intent(this, Login::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setControl(){
        rvHK1 = findViewById(R.id.rvHK1)
        rvHK2 = findViewById(R.id.rvHK2)
        tvGPAHK1 = findViewById(R.id.tvGPAHK1)
        tvGPAHK2 = findViewById(R.id.tvGPAHK2)
        tvHocLucHK1 = findViewById(R.id.tvHocLucHK1)
        tvHocLucHK2 = findViewById(R.id.tvHocLucHK2)
        toolbar=findViewById(R.id.toolbarTK)
        setSupportActionBar(toolbar)
        supportActionBar?.title ="\uD83D\uDCCA Thống kê kết quả học tập"

    }
    private fun setEvent(){

    }
    private fun loadData() {
        lifecycleScope.launch {
            val list = db.bangDiemDao().bangDiemTatCaHocKy()

            val hk1 = list.filter { it.hocKy == 1 }
            val hk2 = list.filter { it.hocKy == 2 }

            adapterHK1.updateData(hk1)
            adapterHK2.updateData(hk2)

            val gpaHK1 = tinhGPA(hk1)
            val gpaHK2 = tinhGPA(hk2)

            tvGPAHK1.text = "GPA: %.2f".format(gpaHK1)
            tvGPAHK2.text = "GPA: %.2f".format(gpaHK2)

            tvHocLucHK1.text = xepLoaiHocLuc(gpaHK1)
            tvHocLucHK2.text = xepLoaiHocLuc(gpaHK2)
        }
    }
    fun tinhGPA(list: List<BangDiemThongKe>): Double {
        if (list.isEmpty()) return 0.0
        return list.map { it.diemTK }.average()
    }

    private fun xepLoaiHocLuc(gpa: Double): String {
        return when {
            gpa >= 8.0 -> "Giỏi"
            gpa >= 7.0 -> "Khá"
            gpa >= 5.0 -> "Trung bình"
            else -> "Yếu"
        }
    }

}