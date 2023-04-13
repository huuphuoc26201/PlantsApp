package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class signUp : AppCompatActivity() {

    private lateinit var sEmail: String
    private lateinit var sPassword: String
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val sginup=findViewById<Button>(R.id.signupbtn)
        val signin=findViewById<TextView>(R.id.sign_in)
        name= findViewById<EditText>(R.id.name)
        email= findViewById(R.id.email)
        password= findViewById<EditText>(R.id.password)


        signin.setOnClickListener{
            val intent=Intent(this,logIn::class.java)
            startActivity(intent)
        }
        sginup.setOnClickListener {
            if (!validateemail() or !validatePassword()  or !validatename()) {
            }else {

                sEmail = email.text.toString().trim();
                sPassword = password.text.toString().trim();
                auth.createUserWithEmailAndPassword(sEmail, sPassword)
                    .addOnCompleteListener(this) { task ->
                        val progressDialog = ProgressDialog(this)
                        progressDialog.setMessage("Please wait...")
                        progressDialog.setCancelable(false)
                        progressDialog.show()
                        Handler().postDelayed({progressDialog.dismiss()},4000)
                        if (task.isSuccessful) {
                            auth.currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    Toast.makeText(
                                        this, "Đăng ký thành công!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Toast.makeText(
                                        this, "Please Verify your email!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, logIn::class.java)
                                    startActivity(intent)
                                }
                                ?.addOnFailureListener {
                                    Toast.makeText(
                                        this, it.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        } else {
                            Toast.makeText(
                                this, "Tài khoản đã tồn tại!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

    }

    private fun validatename(): Boolean {
        val sName= name.text.toString().trim()
        return if (sName.isEmpty()) {
            name.setError("Name không được để trống")
            false
        }
        else
        {
            name.setError(null)
            true
        }

    }

    private fun validatePassword(): Boolean {
        val pass= password.text.toString().trim()
        //Mật khẩu gồm 11 ký tự, ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt:
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*([^a-zA-Z\\d\\s])).{11}\$".toRegex()

        return if (pass.isEmpty()) {
            password.setError("Password không được để trống")
            false
        }
        else if(!pass.matches(passwordPattern)){
            password.setError("Định dạng password không hợp lệ")
            false
        }
        else
        {
            password.setError(null)
            true
        }
    }

    private fun validateemail(): Boolean {
        val mail= email.text.toString().trim()
        return if (mail.isEmpty()) {
            email.setError("Email không được để trống")
            false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.setError("Địa chỉ email không hợp lệ!")
            false
        }
        else
        {
            email.setError(null)
            true
        }
    }
}