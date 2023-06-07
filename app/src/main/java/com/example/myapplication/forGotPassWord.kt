package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class forGotPassWord : AppCompatActivity() {

    private lateinit var sEmail: String
    private lateinit var email: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        builder = AlertDialog.Builder(this)
        email= findViewById(R.id.emailEt)
        val forgot=findViewById<Button>(R.id.reset_mk)
        val signup= findViewById<TextView>(R.id.sign_up)
        auth = FirebaseAuth.getInstance()

        signup.setOnClickListener{
            val intent=Intent(this,signUp::class.java)
            startActivity(intent)
        }

        forgot.setOnClickListener{
            if (!validateemail() ) {
            }else {

                sEmail = email.text.toString().trim()
                auth.sendPasswordResetEmail(sEmail)
                    .addOnSuccessListener {
                        builder.setTitle("Notification")
                            .setMessage("We've helped you reset your password. Please check your email again.")
                            .setCancelable(true) // dialog box in cancellable
                            // set positive button
                            //take two parameters dialogInterface and an int
                            .setPositiveButton("Confirm"){dialogInterface,it ->
                                val intent = Intent(this, logIn::class.java)
                                startActivity(intent)
                                finish() // close the app when yes clicked
                            }
                            // show the builder
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this, "Email not registered account!",
                            Toast.LENGTH_SHORT
                        ).show()
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
}