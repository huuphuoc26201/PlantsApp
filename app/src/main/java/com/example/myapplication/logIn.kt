package com.example.myapplication

import android.app.ProgressDialog
import android.content.ContentValues.TAG
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
import com.example.myapplication.model.userData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class logIn : AppCompatActivity() {

    private lateinit var sharedPreferences:SharedPreferences
    private val FILE_EMAIL="myFile"
    lateinit var text:String
    private lateinit var sEmail: String
    private lateinit var sPassword: String
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var client: GoogleSignInClient
    private lateinit var database: FirebaseDatabase
    private  val RC_SIGN_IN = 9001
    private  val TAG = "GoogleSignInActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        database = FirebaseDatabase.getInstance()
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

        checkBox.isChecked =
            sharedPreferences.contains("checked")&&sharedPreferences.getBoolean("checked",false)==true
        email.setText(sEmail)
        password.setText(sPassword)


        login.setOnClickListener {
            val sEmail=email.text.toString().trim()
            val  sPassword=password.text.toString().trim()
            if(checkBox.isChecked){
                editor.putBoolean("checked",true)
                editor.apply()
                storedDataUsingSharedpref(sEmail,sPassword)

                if (!validateemail() or !validatePassword()) {
                }
                else {
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
        //chuyển tới màn hình signup
        signup.setOnClickListener{
            val intent=Intent(this,signUp::class.java)
            startActivity(intent)
        }

        //chuyển tói màn hình fogot password
        forGot.setOnClickListener{
            val intent=Intent(this,forGotPassWord::class.java)
            startActivity(intent)
        }

        //Đăng nhập bằng tài khoản google
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        client = GoogleSignIn.getClient(this,options)

        logInGG.setOnClickListener {
            val signInIntent = client.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun storedDataUsingSharedpref(sEmail: String, sPassword: String) {
        val editor=sharedPreferences.edit()
        editor.putString("sEmail",sEmail)
        editor.putString("sPassword",sPassword)
        editor.apply()
    }


    //Quá trình đăng nhập
    private fun loginUser() {
        sEmail=email.text.toString().trim();
        sPassword=password.text.toString().trim();
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        Handler().postDelayed({progressDialog.dismiss()},2000)
        auth.signInWithEmailAndPassword(sEmail, sPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val verification=auth.currentUser?.isEmailVerified
                    if(verification==true){
                        val intent=Intent(this,Home::class.java)
                        Toast.makeText(baseContext, "Logged in successfully!",
                            Toast.LENGTH_SHORT).show()
                        startActivity(intent)}
                    else{
                        Toast.makeText(baseContext, "Please verify your email!",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    try {
                        throw task.exception!!;
                    } catch (e: FirebaseAuthInvalidUserException) {
                        email.error = "User does not exist. Please register a new user.";
                        email.requestFocus();
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        password.error = "Incorrect password. please check and re-enter.";
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
            email.error = "Email can not be blank"
            false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.error = "Email address is not valid!"
            false
        }
        else
        {
            email.error = null
            true
        }
    }
    private fun validatePassword(): Boolean {
        val pass= password.text.toString().trim()
        return if (pass.isEmpty()) {
            password.error = "Password can not be blank"
            false
        }
        else
        {
            password.error = null
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Sign in succeeded, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Sign in failed, handle error
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userName = user?.displayName
                    val userEmail = user?.email
                    val userPhotoUrl = user?.photoUrl.toString()

                    // Tạo User object và đưa thông tin lên Realtime Database
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val database= FirebaseDatabase.getInstance()
                    database.getReference("Users").orderByChild("key").equalTo(userId).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (ds in dataSnapshot.children) {
                                    val user = ds.getValue(userData::class.java)
                                    if (user != null) {
                                    }
                                }
                            } else {
                                val user = userData(userName, userEmail, userPhotoUrl, userId)
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId.toString())
                                    .setValue(user)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Xảy ra lỗi trong quá trình đọc dữ liệu

                        }
                    })

                    val intent  = Intent(this,Home::class.java)
                    Toast.makeText(this,"Sign in with Google successfully!",Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                } else {
                    Log.w(TAG, "Google sign in failed", task.exception)
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
    }





}