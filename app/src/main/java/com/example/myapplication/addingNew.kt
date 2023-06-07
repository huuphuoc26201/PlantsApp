package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.listSpeciesData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class addingNew : AppCompatActivity() {

    private lateinit var subjectRadioGroup: RadioGroup
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_SELECT_IMAGE = 2

    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var edt1: String
    private lateinit var edt2: String
    private lateinit var edt3: String
    lateinit var keyrandom:String
    var image_add=" "
    lateinit var ekey:String
    lateinit var keyname:String
    lateinit var image:String
    lateinit var imageUrl:String
    lateinit var text:String
    lateinit var name: EditText
    lateinit var title:EditText
    lateinit var description:EditText
    lateinit var btn_specise:Button
    lateinit var species:Button
    lateinit var articles:Button
    lateinit var imagept:ImageView
    private lateinit var database: FirebaseDatabase
    lateinit var linear_species:LinearLayout
    lateinit var linear_articles:LinearLayout
    //species
    lateinit var raingbar:RatingBar
    lateinit var textevaluate:TextView
    lateinit var namesp:EditText
    lateinit var des_sp:EditText
    lateinit var plant:TextView
    lateinit var btn_articles:Button
    private lateinit var edtname: String
    private lateinit var edtplant: String
    private lateinit var edtdes: String
    private lateinit var Rating:String
    lateinit var addimage:ImageView

    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addingnew)
        val radioButton1 = findViewById<RadioButton>(R.id.radio1)
        val radioButton2 = findViewById<RadioButton>(R.id.radio2)
        val radioButton3 = findViewById<RadioButton>(R.id.radio3)
        val radioButton4 = findViewById<RadioButton>(R.id.radio4)
        val radioButton5 = findViewById<RadioButton>(R.id.radio5)
        builder = AlertDialog.Builder(this)

        name=findViewById(R.id.name)
        title=findViewById(R.id.title)
        description=findViewById(R.id.description)
        btn_articles=findViewById(R.id.btnarticles)
        species=findViewById(R.id.btnsp)
        articles=findViewById(R.id.btnar)
        database = FirebaseDatabase.getInstance()
        imagept=findViewById(R.id.image)
        linear_species=findViewById(R.id.linear_species)
        linear_articles=findViewById(R.id.linear_articles)
        addimage=findViewById(R.id.add)

        //bottom menu
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.person
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, Home::class.java))
                    true
                }
                R.id.person -> {
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
                else -> false
            }
        }
        // Khởi tạo  Database
        databaseReference = FirebaseDatabase.getInstance().reference
        subjectRadioGroup = findViewById(R.id.subject_radio_group)
        subjectRadioGroup.visibility = View.GONE

        image = intent.getStringExtra("message").toString()
        Glide.with(this@addingNew).load(image).error(R.drawable.bradding)
            .into(imagept)

        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            dispatchTakePictureIntent()
        }
        addimage.setOnClickListener {
            dispatchSelectImageIntent()
        }


        //species
        raingbar = findViewById(R.id.ratingBar)
        textevaluate = findViewById(R.id.evaluate)
        namesp=findViewById(R.id.namesp)
        btn_specise=findViewById(R.id.btnspecise)
        des_sp=findViewById(R.id.descriptionsp)
        plant=findViewById(R.id.title1)
        raingbar.setOnRatingBarChangeListener { _, rating, _ ->
            subjectRadioGroup.visibility = View.GONE
            textevaluate.text = rating.toString()
        }

        //plants types
        plant.setOnClickListener {
            subjectRadioGroup.visibility = View.VISIBLE
        }
        des_sp.setOnClickListener {
            subjectRadioGroup.visibility = View.GONE
        }
        subjectRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio1 -> {
                    plant.text = "Cacti and Succulents"
                    radioButton2.isChecked = false
                    radioButton3.isChecked = false
                    radioButton4.isChecked = false
                    radioButton5.isChecked = false
                }
                R.id.radio2 -> {
                    plant.text = "Flowers and Ornamentals"
                    radioButton1.isChecked = false
                    radioButton3.isChecked = false
                    radioButton4.isChecked = false
                    radioButton5.isChecked = false
                }
                R.id.radio3 -> {
                    plant.text = "Fruit-bearing Plants"
                    radioButton1.isChecked = false
                    radioButton2.isChecked = false
                    radioButton4.isChecked = false
                    radioButton5.isChecked = false
                }
                R.id.radio4 -> {
                    plant.text = "Herbs and Spices"
                    radioButton1.isChecked = false
                    radioButton2.isChecked = false
                    radioButton3.isChecked = false
                    radioButton5.isChecked = false
                }
                R.id.radio5 -> {
                    plant.text = "Trees"
                    radioButton1.isChecked = false
                    radioButton2.isChecked = false
                    radioButton4.isChecked = false
                    radioButton3.isChecked = false
                }
            }
        }


        species.setOnClickListener {
            species.setBackgroundResource(R.drawable.custom_button_5)
            articles.setBackgroundResource(R.drawable.custom_button_8)
            linear_species.visibility = View.VISIBLE
            linear_articles.visibility = View.GONE
        }
        articles.setOnClickListener {
            species.setBackgroundResource(R.drawable.custom_button_8)
            articles.setBackgroundResource(R.drawable.custom_button_5)
            linear_species.visibility = View.GONE
            linear_articles.visibility = View.VISIBLE
        }

        // Thông tin users
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("email")
            .equalTo(eemail)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(userData::class.java)
                    keyname=user?.key.toString()
                    name.setText(user?.fullName.toString())
                    imageUrl=user?.imageAvt.toString()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        // Xử lí button species up bài lên realtime
        btn_specise.setOnClickListener {
            if (!validatename()or !validateplant()or !validatdes()) {
            } else {
                builder.setTitle("ADDING NEW")
                    .setMessage("Are you sure you want to post?")
                    .setCancelable(true) // dialog box in cancellable
                    // set positive button
                    //take two parameters dialogInterface and an int
                    .setPositiveButton("Yes") { _, _ ->
                        if (image_add == " ") {
                            image = intent.getStringExtra("message").toString()
                        } else {
                            image = image_add
                        }
                        edtname = namesp.text.toString().trim()
                        edtplant = plant.text.toString().trim()
                        edtdes = des_sp.text.toString().trim()
                        Rating = textevaluate.text.toString().trim()
                        val listSpecies = listSpeciesData(image, edtname, Rating, edtdes, edtplant)
                        val ref = database.getReference("ListSpecies")
                        val key = ref.push().key
                        key?.let {
                            val userRef = ref.child(edtname)
                            userRef.setValue(listSpecies)
                        }
                        val ref1 =
                            database.getReference("AddingNew").child(keyname).child("Species")
                        val key1 = ref1.push().key
                        key1?.let {
                            val userRef1 = ref1.child(edtname)
                            userRef1.setValue(listSpecies)
                        }
                        val intent = Intent(this, Profile::class.java)
                        Toast.makeText(this,"The post has been successfully uploaded!",Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                    // show the builder
                    .show()
            }

        }

        // Xử lí button articles up bài lên realtime
        btn_articles.setOnClickListener {
            if (!validatenamear()or !validatetitlear()or !validatdesar()) {
            } else {
                builder.setTitle("ADDING NEW")
                    .setMessage("Are you sure you want to post?")
                    .setCancelable(true) // dialog box in cancellable
                    // set positive button
                    //take two parameters dialogInterface and an int
                    .setPositiveButton("Yes") { _, _ ->

                        if (image_add == " ") {
                            image = intent.getStringExtra("message").toString()
                        } else {
                            image = image_add
                        }
                        val calendar = Calendar.getInstance()
                        val currentDate = calendar.time
                        // Định dạng ngày tháng
                        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                        edt1 = name.text.toString().trim()
                        edt2 = title.text.toString().trim()
                        edt3 = description.text.toString().trim()
                        val randomString = (1..4)
                            .map { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }
                            .joinToString("")
                        keyrandom= "mypost$randomString"
                        val articles = articlesData(image, imageUrl, edt2, edt1, dateFormat.format(currentDate).toString(), edt3,keyrandom)
                        val ref = database.getReference("Articles")
                        val key = ref.push().key
                        key?.let {
                            val userRef = ref.child(keyrandom)
                            userRef.setValue(articles)
                        }


                        val ref1 =
                            database.getReference("AddingNew").child(keyname).child("Articles")
                        val key1 = ref1.push().key
                        key1?.let {
                            val userRef = ref1.child(keyrandom)
                            userRef.setValue(articles)
                        }
                        val intent = Intent(this, Profile::class.java)
                        Toast.makeText(this,"The post has been successfully uploaded!",Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                    // show the builder
                    .show()
            }

        }
    }

    private fun validatenamear(): Boolean {
        val sname= name.text.toString().trim()

        return if (sname.isEmpty()) {
            name.error = "Information cannot be left blank!"
            false
        }

        else
        {
            name.error = null
            true
        }
    }

    private fun validatetitlear(): Boolean {
        val stitle= title.text.toString().trim()
        return if (stitle.isEmpty()) {
            title.error = "Information cannot be left blank!"
            false
        }
        else
        {
            title.error = null
            true
        }
    }

    private fun validatdesar(): Boolean {
        val sdes= description.text.toString().trim()
        return if (sdes.isEmpty()) {
            description.error = "Information cannot be left blank!"
            false
        }
        else
        {
            description.error = null
            true
        }
    }

    private fun validatdes(): Boolean {
        val sdes= des_sp.text.toString().trim()
        return if (sdes.isEmpty()) {
            des_sp.error = "Information cannot be left blank!"
            false
        }
        else
        {
            des_sp.error = null
            true
        }
    }

    private fun validateplant(): Boolean {
        val splant= plant.text.toString().trim()
        return if (splant.isEmpty()) {
            plant.error = "Information cannot be left blank!"
            false
        }
        else
        {
            plant.error = null
            true
        }
    }

    private fun validatename(): Boolean {
        val snamesp= namesp.text.toString().trim()
        val namePattern="^[a-zA-Z\\d ]{5,30}\$".toRegex()
        return if (snamesp.isEmpty()) {
            namesp.error = "Information cannot be left blank!"
            false
        }
        else if(!snamesp.matches(namePattern)){
            namesp.error = "Species name include 5-30 characters, do not use special characters"
            false
        }
        else
        {
            namesp.error = null
            true
        }
    }

    // Gửi Intent để mở máy ảnh
    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }else{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    // Gửi Intent để mở thư viện ảnh
    private fun dispatchSelectImageIntent() {
        val intent = Intent()
            .setType("image/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val users = FirebaseAuth.getInstance().currentUser ?: return
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    // Tạo reference tới file ảnh trên FirebaseStorage
                    val storageRef = Firebase.storage.reference
                    val eemail = users.email
                    val database = FirebaseDatabase.getInstance()
                    database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (ds in dataSnapshot.children) {
                                    val user = ds.getValue(userData::class.java)
                                    if (user != null) {
                                        ekey= user?.key.toString()
                                        val imagesRef = storageRef.child("PostArticle").child(ekey).child("${UUID.randomUUID()}.jpg")
                                        // Chuyển đổi bitmap thành byte array để upload lên FirebaseStorage
                                        val baos = ByteArrayOutputStream()
                                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                        val data = baos.toByteArray()

                                        // Upload ảnh lên FirebaseStorage
                                        val uploadTask = imagesRef.putBytes(data)

                                        // Lắng nghe sự kiện hoàn thành upload
                                        if (uploadTask != null) {
                                            uploadTask.continueWithTask { task ->
                                                if (!task.isSuccessful) {
                                                    task.exception?.let { throw it }
                                                }
                                                imagesRef.downloadUrl
                                            }.addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val downloadUri = task.result
                                                    image_add=downloadUri.toString()
                                                    Glide.with(this@addingNew).load(image_add).error(R.drawable.bradding)
                                                        .into(imagept)

                                                } else {
                                                    // Xử lý khi không thể tải lên hình ảnh

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Xảy ra lỗi trong quá trình đọc dữ liệu

                        }
                    })
                }
                REQUEST_SELECT_IMAGE -> {
                    // Lấy ảnh từ thư viện và upload lên Firebase Storage
                    val imageUri = data?.data
                    // Load image to ImageView
                    imagept.setImageURI(imageUri)
                    val eemail = users.email
                    database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (ds in dataSnapshot.children) {
                                    val user = ds.getValue(userData::class.java)
                                    if (user != null) {
                                        ekey= user?.key.toString()
                                        val storageRef = FirebaseStorage.getInstance().reference.child("PostArticle").child(ekey).child("${UUID.randomUUID()}.jpg")
                                        val uploadTask = imageUri?.let { storageRef.putFile(it) }

                                        uploadTask?.continueWithTask { task ->
                                            if (!task.isSuccessful) {
                                                task.exception?.let { throw it }
                                            }

                                            storageRef.downloadUrl
                                        }?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Lưu đường dẫn của hình ảnh vào database Realtime Database
                                                val downloadUri = task.result
                                                image_add=downloadUri.toString()
                                                Glide.with(this@addingNew).load(image_add).error(R.drawable.bradding)
                                                    .into(imagept)
                                            } else {
                                                // Xử lý khi không thể tải lên hình ảnh
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Xảy ra lỗi trong quá trình đọc dữ liệu

                        }
                    })
                }
            }
        }
    }
    fun prev(view: View?){
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }

    // Yêu cầu quyền truy máy ảnh
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted
            } else {
                // Permission has been denied
            }
        }
    }
}