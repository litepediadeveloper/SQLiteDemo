package com.litepedia.sqlitedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var etName : EditText
    private lateinit var etEmail : EditText
    private lateinit var btAdd: Button
    private lateinit var btView: Button
    private lateinit var btUpdate: Button
    private lateinit var btDelete: Button

    private lateinit var  sqLiteHelper: SQLiteHelper
    private lateinit var recyclerView:RecyclerView
    private var adapter: StudentAdapter? = null
    private var std:StudentModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initRecyclerView()
        sqLiteHelper = SQLiteHelper(this)

        btAdd.setOnClickListener { addStudent() }
        btView.setOnClickListener { getStudents() }
        btUpdate.setOnClickListener { updateStudent() }


        adapter?.setOnClickItem {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            //Ok now we need to update record
            etName.setText(it.name)
            etEmail.setText(it.email)
            std = it
        }

        adapter?.setOnClickDeleteItem {
            deleteStudent(it.id)
        }
    }

    private fun getStudents(){
        val stdList = sqLiteHelper.getAllStudent()
        Log.e("ppp", "${stdList.size}")

        //Ok now we need to display data in recyclerview
        adapter?.addItem(stdList)
    }

    private fun addStudent(){
        val name = etName.text.toString()
        val email = etEmail.text.toString()

        if (name.isEmpty()||email.isEmpty()){
            Toast.makeText(this, "Please enter required field", Toast.LENGTH_SHORT).show()
        }else{
            val std = StudentModel(name = name, email = email)
            val status = sqLiteHelper.insertStudent(std)
            //Check insert success or not success
            if (status > -1){
                Toast.makeText(this, "Student added...", Toast.LENGTH_SHORT).show()
                clearEditText()
            }else{
                Toast.makeText(this, "Record not saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStudent(){
        val name = etName.text.toString()
        val email = etEmail.text.toString()

        if (name==std?.name && email == std?.email){
            Toast.makeText(this, "Record not changed...", Toast.LENGTH_SHORT).show()
            return
        }

        if (std == null) return

        val std = StudentModel(id = std!!.id, name = name, email = email)
        val status = sqLiteHelper.updateStudent(std)
        if (status>-1){
            clearEditText()
            getStudents()
        }else{
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudent(id:Int){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete item?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){dialog,_ ->
            sqLiteHelper.deleteStudentById(id)
            getStudents()
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog,_ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }

    private fun clearEditText(){
        etName.setText("")
        etEmail.setText("")
        etName.requestFocus()
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter()
        recyclerView.adapter = adapter
    }

    private fun initView() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btAdd = findViewById(R.id.btAdd)
        btView = findViewById(R.id.btView)
        btUpdate = findViewById(R.id.btUpdate)
        recyclerView = findViewById(R.id.recyclerView)
    }
}