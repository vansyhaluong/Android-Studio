package com.example.tuan1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ActivityTKB : AppCompatActivity() {
    lateinit var adapter: TKBAdapter
    lateinit var tkbDao: TKBDao
    lateinit var monHocDao: MonHocDao
    lateinit var rvTKB: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var spLocTuan: Spinner
    lateinit var db: AppDatabase
    lateinit var fabAdd: FloatingActionButton
    var currentWeek = 1
    var userRole: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tkb)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setControl()
        userRole = intent.getStringExtra("USER_ROLE") ?: "user"
        if( userRole!="admin"){
            fabAdd.visibility=View.GONE
        }
        setEvent()
         db = AppDatabase.getInstance(this)
        tkbDao = db.tkbDao()
        monHocDao = db.monHocDao()
        loadSpinnerTuanFromDB()
        val listTKB = tkbDao.getAll()
        val listMon = monHocDao.getAll()
        adapter = TKBAdapter(
            listTKB = emptyList(),
            userRole,
            onEdit = { tkb -> openDialogEdit(tkb) },
            onDelete = { tkb -> deleteTKB(tkb) }
        )
        loadTKB()
        rvTKB.adapter = adapter
        rvTKB.layoutManager = LinearLayoutManager(this)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_tkb, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_home -> startActivity(Intent(this, CuoiKy::class.java))
            R.id.menu_monhoc -> startActivity(Intent(this, MonHoc::class.java))
            R.id.menu_nhapdiem -> startActivity(Intent(this, ActivityNhapDiem::class.java))
            R.id.menu_thongke -> startActivity(Intent(this, ActivityThongKe::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setControl() {
        rvTKB = findViewById(R.id.rvTKB)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Quản lý thời khóa biểu - Trần Văn Tiên"
        fabAdd = findViewById(R.id.fabAddTK)
        spLocTuan = findViewById(R.id.spinnerTuan)
        rvTKB.layoutManager = LinearLayoutManager(this)
    }
    private fun setEvent() {
        fabAdd.setOnClickListener {
            openDialogAdd()
        }
        spLocTuan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val text = parent?.getItemAtPosition(position).toString()
                currentWeek = text.replace("Tuần ", "").toInt()
                loadTKB()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun openDialogAdd() {
        openDialog(null)
    }

    private fun openDialogEdit(tkb: TKBTenMon) {
        openDialog(tkb)
    }

    private fun loadTKB() {
        lifecycleScope.launch {
            val list = db.tkbDao().getByTuan(currentWeek)
            adapter.updateData(list)
        }
    }
    private fun deleteTKB(tkb: TKBTenMon) {
        AlertDialog.Builder(this)
            .setTitle("Xóa thời khóa biểu")
            .setMessage("Bạn có chắc muốn xóa môn ${tkb.tenMon}?")
            .setPositiveButton("Xóa") { _, _ ->
                lifecycleScope.launch {
                    db.tkbDao().deleteById(tkb.maTKB)
                    loadTKB()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    private fun saveTKB(
        tkbEdit: TKBTenMon?,
        listMon: List<DB_MonHoc>,
        spMon: Spinner,
        edtThu: EditText,
        edtTietBD: EditText,
        edtTietKT: EditText,edtTuan: EditText
    ) {
        val thu = edtThu.text.toString().toIntOrNull()
        val tietBD = edtTietBD.text.toString().toIntOrNull()
        val tietKT = edtTietKT.text.toString().toIntOrNull()
        val tuan = edtTuan.text.toString().toIntOrNull()
        if (tuan == null || tuan <= 0) {
            AlertDialog.Builder(this)
                .setMessage("Tuần không hợp lệ")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        if (thu == null || tietBD == null || tietKT == null) {
            AlertDialog.Builder(this)
                .setMessage("Vui lòng nhập đầy đủ và đúng định dạng số")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        if (thu !in 2..8) {
            AlertDialog.Builder(this)
                .setMessage("Thứ phải từ 2 đến 8")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        if (tietBD <= 0 || tietKT < tietBD) {
            AlertDialog.Builder(this)
                .setMessage("Tiết học không hợp lệ")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val mon = listMon[spMon.selectedItemPosition]

        val entity = ThoiKhoaBieu(
            maTKB = tkbEdit?.maTKB ?: 0,
            maMon = mon.maMon,
            thu = thu,
            tietBD = tietBD,
            tietKT = tietKT,
            tuan = tuan
        )

        lifecycleScope.launch {
            if (tkbEdit == null)
                tkbDao.insert(entity)
            else
                tkbDao.update(entity)

            loadSpinnerTuanFromDB()
        }
    }
    private fun openDialog(tkbEdit: TKBTenMon?) {
        val view = layoutInflater.inflate(R.layout.them_tkb, null)
        val spMon = view.findViewById<Spinner>(R.id.spMonHoc1)
        val edtThu = view.findViewById<EditText>(R.id.edtThu)
        val edtTuan=view.findViewById<EditText>(R.id.edtTuan)
        val edtTietBD = view.findViewById<EditText>(R.id.edtTietBD)
        val edtTietKT = view.findViewById<EditText>(R.id.edtTietKT)

        lifecycleScope.launch {
            // Lấy danh sách môn học từ DB
            val listMon = monHocDao.getAll()
            val adapterMon = ArrayAdapter(
                this@ActivityTKB,
                android.R.layout.simple_spinner_item,
                listMon.map { it.tenMon }
            )
            adapterMon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spMon.adapter = adapterMon

            // Nếu edit, load dữ liệu lên dialog
            tkbEdit?.let {
                if (tkbEdit == null) {
                    edtTuan.setText(currentWeek.toString())
                }
// SỬA
                else {
                    val index = listMon.indexOfFirst { mh -> mh.maMon == tkbEdit.maMon }
                    spMon.setSelection(index)

                    edtTuan.setText(tkbEdit.tuan.toString())
                    edtThu.setText(tkbEdit.thu.toString())
                    edtTietBD.setText(tkbEdit.tietBD.toString())
                    edtTietKT.setText(tkbEdit.tietKT.toString())
                }
            }

            AlertDialog.Builder(this@ActivityTKB)
                .setTitle(if (tkbEdit == null) "Thêm TKB" else "Sửa TKB")
                .setView(view)
                .setPositiveButton("Lưu") { _, _ ->
                    saveTKB(
                        tkbEdit,
                        listMon,
                        spMon,
                        edtThu,
                        edtTietBD,
                        edtTietKT,edtTuan
                    )
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

    }
    private fun loadSpinnerTuanFromDB() {
        lifecycleScope.launch {
            val listTuan = tkbDao.getAllTuan()

            if (listTuan.isEmpty()) {
                spLocTuan.adapter = null
                return@launch
            }

            val adapterTuan = ArrayAdapter(
                this@ActivityTKB,
                android.R.layout.simple_spinner_item,
                listTuan.map { "Tuần $it" }
            )
            adapterTuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spLocTuan.adapter = adapterTuan

            if (!listTuan.contains(currentWeek)) {
                currentWeek = listTuan[0]
            }
            val index = listTuan.indexOf(currentWeek)
            if (index >= 0) {
                spLocTuan.setSelection(index)
            }
            loadTKB()
        }
    }
}
