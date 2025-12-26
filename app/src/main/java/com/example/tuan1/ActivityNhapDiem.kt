package com.example.tuan1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ActivityNhapDiem : AppCompatActivity() {


    lateinit var spMonHoc: Spinner
    lateinit var edtDiemGK: EditText
    lateinit var edtDiemCK: EditText
    lateinit var rvDiem: RecyclerView
    lateinit var btnSave: Button
    lateinit var toolbar: Toolbar
    private var listMonHoc: List<DB_MonHoc> = emptyList()
    private var listDiem: List<Diem> = emptyList()
    private var editingDiem: Diem?=null
    lateinit var diemDao: DiemDao
    lateinit var adapter: DiemAdapter
    private lateinit var db: AppDatabase
    lateinit var llNhapDiem: LinearLayout
    var userRole:String="user"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_nhap_diem)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        db = AppDatabase.getInstance(this)
        userRole = intent.getStringExtra("USER_ROLE") ?: "user"
        if(userRole!="admin"){
            llNhapDiem.visibility= View.GONE
        }
        loadMonHoc()
        setRecyclerView()
        loadDiem()
        setEvent()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_nhapdiem, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_home -> startActivity(Intent(this, CuoiKy::class.java))
            R.id.menu_monhoc -> startActivity(Intent(this, MonHoc::class.java))
            R.id.menu_tkb -> {
                val intent = Intent(this, ActivityTKB::class.java)
                intent.putExtra("USER_ROLE", userRole)
                startActivity(intent)
            }
            R.id.menu_thongke -> startActivity(Intent(this, ActivityThongKe::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setControl(){
         toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Nhập điểm"
        spMonHoc = findViewById(R.id.spMonHoc)
        edtDiemGK = findViewById(R.id.edtDiemGK)
        edtDiemCK = findViewById(R.id.edtDiemCK)
        btnSave = findViewById(R.id.btnSave)
        rvDiem = findViewById(R.id.rvDiem)
        llNhapDiem=findViewById(R.id.llNhapDiem)
    }
    private fun setEvent() {
        btnSave.setOnClickListener {
            val monIndex = spMonHoc.selectedItemPosition
            if (monIndex == -1) return@setOnClickListener

            val maMon = listMonHoc[monIndex].maMon
            val diemGK = edtDiemGK.text.toString().toFloatOrNull()
            val diemCK = edtDiemCK.text.toString().toFloatOrNull()

            if (diemGK == null || diemCK == null || diemGK !in 0f..10f || diemCK !in 0f..10f) {
                Toast.makeText(this, "Điểm phải từ 0 đến 10", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (editingDiem == null) {
                    // Thêm mới
                    val newDiem = Diem(maMon = maMon, diemGK = diemGK, diemCK = diemCK)
                    db.diemDao().insert(newDiem)
                } else {
                    // Cập nhật
                    val updated = editingDiem!!.copy(diemGK = diemGK, diemCK = diemCK, maMon = maMon)
                    db.diemDao().update(updated)
                    editingDiem = null
                    btnSave.text = "Lưu"
                }
                edtDiemGK.text.clear()
                edtDiemCK.text.clear()
                spMonHoc.setSelection(0)
                loadDiem()
            }
        }
    }
    private fun setRecyclerView() {
        adapter = DiemAdapter(listDiem, listMonHoc,userRole,
            onEdit = { diem ->
                editingDiem = diem
                // load dữ liệu vào form
                spMonHoc.setSelection(listMonHoc.indexOfFirst { it.maMon == diem.maMon })
                edtDiemGK.setText(diem.diemGK.toString())
                edtDiemCK.setText(diem.diemCK.toString())
                btnSave.text = "Cập nhật"
            },
            onDelete = { diem ->
                // Xác nhận xóa
                AlertDialog.Builder(this)
                    .setTitle("Xóa điểm")
                    .setMessage("Bạn có chắc muốn xóa điểm này không?")
                    .setPositiveButton("Có") { _, _ ->
                        lifecycleScope.launch {
                            db.diemDao().delete(diem)
                            loadDiem()
                        }
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            }
        )
        rvDiem.layoutManager = LinearLayoutManager(this)
        rvDiem.adapter = adapter
    }
    private fun loadMonHoc() {
        lifecycleScope.launch {
            // Lấy danh sách môn học từ db
            listMonHoc = db.monHocDao().getAll()

            // Nếu danh sách trống, tạo một danh sách rỗng để tránh crash
            val tenMonList = listMonHoc.map { it.tenMon }

            runOnUiThread {
                val spinnerAdapter = ArrayAdapter(
                    this@ActivityNhapDiem,
                    android.R.layout.simple_spinner_item,
                    tenMonList
                )
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spMonHoc.adapter = spinnerAdapter
            }
        }
    }
    private fun luuDiem() {
        val pos = spMonHoc.selectedItemPosition
        if (pos == AdapterView.INVALID_POSITION) {
            toast("Chưa chọn môn học")
            return
        }

        val gk = edtDiemGK.text.toString().toFloatOrNull()
        val ck = edtDiemCK.text.toString().toFloatOrNull()

        if (gk == null || ck == null || gk !in 0f..10f || ck !in 0f..10f) {
            toast("Điểm phải từ 0 đến 10")
            return
        }

        val mon = listMonHoc[pos]

        val diem = Diem(
            maMon = mon.maMon,
            diemGK = gk,
            diemCK = ck
        )

        lifecycleScope.launch {
            diemDao.insert(diem)
            toast("Đã lưu điểm")

            edtDiemGK.text.clear()
            edtDiemCK.text.clear()
        }
    }
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    private fun loadDiem() {
        lifecycleScope.launch {
            listDiem = db.diemDao().getAll()
            adapter.updateData(listDiem)
        }
    }
}