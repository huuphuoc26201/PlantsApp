package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class changePasswword : AppCompatActivity() {
    lateinit var new_password:String
    lateinit var newPassword: EditText
    lateinit var OldPassword: EditText

    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepasswword)
        builder = AlertDialog.Builder(this)
        val save=findViewById<Button>(R.id.save)
        newPassword=findViewById(R.id.mk_new)
        OldPassword=findViewById(R.id.mk_old)
       save.setOnClickListener {
           if (!validateOldpassword() or !validateNewPassword()) {
           } else {
               changepasswword()
           }
       }
    }

    private fun changepasswword() {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val users = FirebaseAuth.getInstance().currentUser ?: return
        new_password=newPassword.text.toString().trim()
        users?.updatePassword(new_password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Đổi mật khẩu thành công
                    builder.setTitle("Change Password")
                        .setMessage("Do you want to continue logging in?")
                        .setCancelable(true) // dialog box in cancellable
                        // set positive button
                        //take two parameters dialogInterface and an int
                        .setPositiveButton("Yes"){dialogInterface,it ->
                            val progressDialog = ProgressDialog(this)
                            progressDialog.setMessage("Please wait...")
                            progressDialog.setCancelable(false)
                            progressDialog.show()
                            Handler().postDelayed({progressDialog.dismiss()},4000)
                            val intent = Intent(this, Profile::class.java)
                            startActivity(intent)
                            finish()
                        }
                    builder.setNegativeButton("Logout") { dialog, which ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, logIn::class.java)
                        startActivity(intent)
                        finish()
                        dialog.cancel()
                    }
                        // show the builder
                        .show()

                } else {
                    // Đổi mật khẩu thất bại
                    Toast.makeText(this,"You are using google account can not change password!!!",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateOldpassword(): Boolean {
        val pass= OldPassword.text.toString().trim()
        //Mật khẩu gồm 11 ký tự, ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt:
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*([^a-zA-Z\\d\\s])).{11,20}\$".toRegex()

        return if (pass.isEmpty()) {
            OldPassword.setError("Password can not be blank")
            false
        }
        else if(!pass.matches(passwordPattern)){
            OldPassword.setError("Password consists of 11-20 characters, at least one uppercase letter, one lowercase letter, one number and one special character")
            false
        }
        else
        {
            OldPassword.setError(null)
            true
        }
    }

    private fun validateNewPassword(): Boolean {
        val pass= newPassword.text.toString().trim()
        //Mật khẩu gồm 11 ký tự, ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt:
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*([^a-zA-Z\\d\\s])).{11,20}\$".toRegex()

        return if (pass.isEmpty()) {
            newPassword.setError("Password can not be blank")
            false
        }
        else if(!pass.matches(passwordPattern)){
            newPassword.setError("Password consists of 11-20 characters, at least one uppercase letter, one lowercase letter, one number and one special character")
            false
        }
        else
        {
            newPassword.setError(null)
            true
        }
    }
    fun prev(view: View?){
        startActivity(Intent(this, Profile::class.java))
        finish()
    }
    fun fogot(view: View?){
        startActivity(Intent(this, forGotPassWord::class.java))
        finish()
    }
}