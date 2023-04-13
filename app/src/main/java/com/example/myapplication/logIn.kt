package com.example.myapplication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class logIn : AppCompatActivity() {

    private lateinit var sharedPreferences:SharedPreferences
    private val FILE_EMAIL="myFile"
    private lateinit var sEmail: String
    private lateinit var sPassword: String
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var client: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        email= findViewById(R.id.email)
        password= findViewById(R.id.password)
        val login=findViewById<Button>(R.id.signipbtn)

        val signup= findViewById<TextView>(R.id.sign_up)
        val checkBox= findViewById<CheckBox>(R.id.remember)
        val forGot= findViewById<TextView>(R.id.quenmk)
        val logInGG= findViewById<TextView>(R.id.lggoogle)
        auth=Firebase.auth

        sharedPreferences=getSharedPreferences(FILE_EMAIL,MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        val  sEmail=sharedPreferences.getString("sEmail","")
        val  sPassword=sharedPreferences.getString("sPassword","")

        if(sharedPreferences.contains("checked")&&sharedPreferences.getBoolean("checked",false)==true){
            checkBox.isChecked=true
        }else{
            checkBox.isChecked=false
        }
        email.setText(sEmail)
        password.setText(sPassword)




        login.setOnClickListener {
            val sEmail=email.text.toString().trim();
            val  sPassword=password.text.toString().trim();
            if(checkBox.isChecked()){
                editor.putBoolean("checked",true)
                editor.apply()
                storedDataUsingSharedpref(sEmail,sPassword)
                if (!validateemail() or !validatePassword()) {
                }else {
                    loginUser()
                }
            }else {
                if (!validateemail() or !validatePassword()) {
                }else {
                    getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit().clear().commit()
                    loginUser()
                }
            }
        }
        signup.setOnClickListener{
            val intent=Intent(this,signUp::class.java)
            startActivity(intent)
        }


        forGot.setOnClickListener{
            val intent=Intent(this,forGotPassWord::class.java)
            startActivity(intent)
        }
        val  options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            // dont worry about this error
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(this,options)
        logInGG.setOnClickListener {
            val intent = client.signInIntent
            startActivityForResult(intent,10001)
        }
    }

    private fun storedDataUsingSharedpref(sEmail: String, sPassword: String) {
        val editor=sharedPreferences.edit()
        editor.putString("sEmail",sEmail)
        editor.putString("sPassword",sPassword)
        editor.apply()
    }


    private fun loginUser() {
        sEmail=email.text.toString().trim();
        sPassword=password.text.toString().trim();
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        Handler().postDelayed({progressDialog.dismiss()},4000)
        auth.signInWithEmailAndPassword(sEmail, sPassword)
            .addOnCompleteListener(this) { task ->


                if (task.isSuccessful) {
                    val verification=auth.currentUser?.isEmailVerified
                    if(verification==true){
                        val intent=Intent(this,MainActivity::class.java)
                        Toast.makeText(baseContext, "Đăng nhập thành công!",
                            Toast.LENGTH_SHORT).show()
                        startActivity(intent)}
                    else{
                        Toast.makeText(baseContext, "Please verify your email!",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    try {
                        throw task.getException()!!;
                    } catch (e: FirebaseAuthInvalidUserException) {
                        email.setError("Người dùng không tồn tại. Vui lòng đăng ký người dùng mới.");
                        email.requestFocus();
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        password.setError("Mật khẩu không đúng. vui lòng kiểm tra và nhập lại.");
                        password.requestFocus();
                    } catch (e: Exception) {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT)
                            .show();
                    }
                }

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
    private fun validatePassword(): Boolean {
        val pass= password.text.toString().trim()
        return if (pass.isEmpty()) {
            password.setError("Password không được để trống")
            false
        }
        else
        {
            password.setError(null)
            true
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==10001){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){

                        val intent  = Intent(this,MainActivity::class.java)
                        Toast.makeText(this,"Đăng nhập bằng Google thành công!",Toast.LENGTH_SHORT).show()
                        startActivity(intent)

                    }else{
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }



}