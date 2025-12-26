package com.example.tuan1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.tuan1.User

class Login : AppCompatActivity() {
    lateinit var edtMasv: EditText
    lateinit var edtPassword: EditText
    lateinit var btnLogin: Button
    lateinit var db: AppDatabase
    lateinit var tvForgotPassword: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycleScope.launch {
            val users = db.userDao().getAll()
            if (users.isEmpty()) {
                db.userDao().insertAll(
                    listOf(
                        User("admin", "123", "admin"),
                        User("24211tt3936", "123", "user")
                    )
                )
            }
        }
        setControl()
        setEvent()
    }
    private fun setControl(){
        edtMasv=findViewById(R.id.masv)
        edtPassword=findViewById(R.id.password)
        btnLogin=findViewById(R.id.btnLogin)
        tvForgotPassword=findViewById(R.id.tvQuenMK)
    }
    private fun setEvent(){
        btnLogin.setOnClickListener {
            lifecycleScope.launch{
                val user = db.userDao().login(edtMasv.text.toString(), edtPassword.text.toString())

                if (user != null) {
                    val intent = Intent(this@Login, CuoiKy::class.java)
                    intent.putExtra("USER_ROLE", user.role)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@Login, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                }
            }
        }
        tvForgotPassword.setOnClickListener {
            val inputMasv = edtMasv.text.toString().trim()
            if(inputMasv.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập mã sinh viên để lấy lại mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().getUserByMaSV(inputMasv)
                if(user != null){
                    // Hiện mật khẩu hoặc gửi email (ở đây demo hiện Toast)
                    Toast.makeText(this@Login, "Mật khẩu của bạn là: ${user.password}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@Login, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}