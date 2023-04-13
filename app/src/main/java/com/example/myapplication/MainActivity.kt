package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text= findViewById<TextView>(R.id.out)
        val logout= findViewById<Button>(R.id.signout)




        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            val intent= Intent(this,logIn::class.java)
            Toast.makeText(baseContext, "SignOut thành công!",
                Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
}