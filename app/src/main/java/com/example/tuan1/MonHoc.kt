package com.example.tuan1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MonHoc : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var fabAdd: FloatingActionButton
    lateinit var db: AppDatabase
    lateinit var spLocMonHoc: Spinner
    lateinit var adapter: MonHocAdapter
    lateinit var rvMonHoc: RecyclerView
    private var listHocKy: List<HocKy> = listOf()
    var userRole: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.monhoc)
        setControll()
        setEvent()
        db = AppDatabase.getInstance(this)
        userRole = intent.getStringExtra("USER_ROLE") ?: "user"
        if( userRole!="admin"){
            fabAdd.visibility=View.GONE
        }
        rvMonHoc.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            seedHocKyIfNeeded()

            val hocKy = db.hocKyDao().getAll()
            val monHoc = db.monHocDao().getAll()

            runOnUiThread {
                listHocKy = hocKy
                adapter = MonHocAdapter(
                    listMH = monHoc,
                    listHocKy = listHocKy,
                    role=userRole,
                    onEditClick = { openEditDialog(it) },
                    onDeleteClick = { openDeleteDialog(it) }
                )

                rvMonHoc.adapter = adapter
                setupHocKyFilter()
            }
        }

    }

    private fun setControll() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        fabAdd = findViewById(R.id.fabAdd)
        spLocMonHoc=findViewById(R.id.spLocMonHoc)
        rvMonHoc = findViewById(R.id.rvMonHoc)
    }

    private fun setEvent() {
        // Thêm nút mở Drawer
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_home -> {
                    startActivity(Intent(this, CuoiKy::class.java))
                }
            }

            drawerLayout.closeDrawers()
            true
        }
        fabAdd.setOnClickListener {

            openAddDialog()
        }
    }

    private fun openAddDialog() {
        if (listHocKy.isEmpty()) {
            Toast.makeText(this, "Chưa có dữ liệu học kỳ", Toast.LENGTH_SHORT).show()
            return
        }
        val view = layoutInflater.inflate(R.layout.them_monhoc, null)
        val edtMaMon = view.findViewById<EditText>(R.id.edtMaMon)
        val edtTenMon = view.findViewById<EditText>(R.id.edtTenMon)
        val edtTinChi = view.findViewById<EditText>(R.id.edtTinChi)
        val spHocKy=view.findViewById<Spinner>(R.id.spChonHocKy)
        val adapterHocKy = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listHocKy.map { it.tenHocKy }
        )
        adapterHocKy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHocKy.adapter = adapterHocKy

        AlertDialog.Builder(this)
            .setTitle("Thêm môn học")
            .setView(view)
            .setPositiveButton("Lưu") { _, _ ->
                val maMon = edtMaMon.text.toString().trim()
                val tenMon = edtTenMon.text.toString().trim()
                val tinChi = edtTinChi.text.toString().trim().toIntOrNull() ?: 0
                val idHocKy = listHocKy.getOrNull(spHocKy.selectedItemPosition)?.maHocKy?: 1

                if (maMon.isEmpty() || tenMon.isEmpty() || tinChi <= 0) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val monHoc = DB_MonHoc(maMon = maMon, tenMon = tenMon, tinChi = tinChi, maHocKy = idHocKy)
                CoroutineScope(Dispatchers.IO).launch { db.monHocDao().insert(monHoc) }
                loadData()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun loadData() {
        val list = db.monHocDao().getAll()
        adapter.updateData(list)
    }

    private fun openDeleteDialog(monHoc: DB_MonHoc) {
        AlertDialog.Builder(this)
            .setTitle("Xóa môn học")
            .setMessage("Bạn có chắc muốn xóa môn \"${monHoc.tenMon}\" không?")
            .setPositiveButton("Xóa") { _, _ ->
                db.monHocDao().delete(monHoc)
                loadData()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun openEditDialog(monHoc: DB_MonHoc) {
        val view = layoutInflater.inflate(R.layout.them_monhoc, null)

        val edtMaMon = view.findViewById<EditText>(R.id.edtMaMon)
        val edtTenMon = view.findViewById<EditText>(R.id.edtTenMon)
        val edtTinChi = view.findViewById<EditText>(R.id.edtTinChi)

        // Đổ dữ liệu cũ
        edtMaMon.setText(monHoc.maMon)
        edtTenMon.setText(monHoc.tenMon)
        edtTinChi.setText(monHoc.tinChi.toString())

        AlertDialog.Builder(this)
            .setTitle("Sửa môn học")
            .setView(view)
            .setPositiveButton("Lưu") { _, _ ->
                val newMaMon = edtMaMon.text.toString().trim()
                val newTenMon = edtTenMon.text.toString().trim()
                val newTinChi = edtTinChi.text.toString().trim()

                if (newMaMon.isEmpty() || newTenMon.isEmpty() || newTinChi.isEmpty()) {
                    Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedMonHoc = monHoc.copy(
                    maMon = newMaMon,
                    tenMon = newTenMon,
                    tinChi = newTinChi.toInt()
                )

                db.monHocDao().update(updatedMonHoc)
                loadData()
                Toast.makeText(this, "Đã cập nhật môn học", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchEdit=menu.findItem(R.id.exit)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Tìm môn học..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterData(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText)
                return true
            }
        })

        return true
    }

    private fun filterData(keyword: String?) {
        val allList = db.monHocDao().getAll()

        if (keyword.isNullOrEmpty()) {
            adapter.updateData(allList)
            return
        }

        val filteredList = allList.filter {
            it.tenMon.contains(keyword, ignoreCase = true) ||
                    it.maMon.contains(keyword, ignoreCase = true)
        }

        adapter.updateData(filteredList)
    }
    private suspend fun seedHocKyIfNeeded() {
        val hocKyDao = db.hocKyDao()
        if (hocKyDao.getAll().isEmpty()) {
            hocKyDao.insert(HocKy(tenHocKy = "Học kỳ 1"))
            hocKyDao.insert(HocKy(tenHocKy = "Học kỳ 2"))

        }
    }
    private fun setupHocKyFilter() {

        if (listHocKy.isEmpty()) return

        val hocKyDisplayList = mutableListOf("Tất cả học kỳ")
        hocKyDisplayList.addAll(listHocKy.map { it.tenHocKy })

        val adapterHocKy = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            hocKyDisplayList
        )
        adapterHocKy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spLocMonHoc.adapter = adapterHocKy

        spLocMonHoc.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val list = if (position == 0) {
                        db.monHocDao().getAll()
                    } else {
                        val hocKyId = listHocKy[position - 1].maHocKy
                        db.monHocDao().getByHocKy(hocKyId)
                    }
                    adapter.updateData(list)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // ép load dữ liệu ban đầu
        spLocMonHoc.setSelection(0)
    }

}