package com.aurumsystem.mysqlite

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.core.view.MenuItemCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAdd:Button = findViewById(R.id.btnAdd)
        val btnPrint:Button = findViewById(R.id.btnPrint)
        val tiName: TextInputLayout = findViewById(R.id.tiName)
        val tiAge: TextInputLayout = findViewById(R.id.tiAge)
        var name = tiName.editText?.text.toString()
        var age = tiAge.editText?.text.toString()
        val listView: ListView = findViewById(R.id.listView)

        val db = DBHelper(this, null)
        val arrayList:ArrayList<String> = db.getAllData() as ArrayList<String>
        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this@MainActivity,
        android.R.layout.simple_list_item_1, arrayList as List<Any?>)
        listView.adapter = arrayAdapter


        val header:MaterialTextView = layoutInflater.inflate(
            android.R.layout.simple_dropdown_item_1line, listView, false)
                as MaterialTextView
        header.text = "List of Name"
        header.setTypeface(header.typeface, Typeface.BOLD)
        header.setBackgroundColor(Color.parseColor("Purple"))
        header.setTextColor(Color.parseColor("White"))
        listView. addHeaderView(header)

        fun Display(){
            arrayList.clear()
            arrayList.addAll(db.getAllData())
            arrayAdapter.notifyDataSetChanged()
            listView.invalidateViews()
            listView.refreshDrawableState()
            listView.adapter = arrayAdapter
            Toast.makeText(this, "Data displayed", Toast.LENGTH_SHORT).show()
        }

        tiName.editText!!.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(tiName.editText?.text?.isBlank() == true){
                    Display()
                }
                if(tiName.editText?.text?.isNotBlank() == true)
                {
                    val arrlist:ArrayList<String> = db.SearchDataByName(tiName.editText?.text?.toString().toString()) as ArrayList<String>
                    val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this@MainActivity,
                        android.R.layout.simple_list_item_1, arrlist as List<Any?>)
                    arrayList.clear()
                    arrayList.addAll(db.SearchDataByName(tiName.editText?.text?.toString().toString()))
                    arrayAdapter.notifyDataSetChanged()
                    listView.invalidateViews()
                    listView.refreshDrawableState()
                    listView.adapter = arrayAdapter
                    //Toast.makeText(this@MainActivity, "Data found", Toast.LENGTH_SHORT).show()
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        btnPrint.setOnClickListener {
            Display()
        }
        btnAdd.setOnClickListener {
            if(tiName.editText?.text?.isNotEmpty() == true && tiAge.editText?.text?.isNotEmpty() == true){
                if(db.addData(tiName.editText?.text.toString(), tiAge.editText?.text.toString())){
                    Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show()
                    Display()
                }
                else
                {
                    Toast.makeText(this, "Not Inserted", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Cannot left blank", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener{adapterView, view, i, l ->
            var params: String = adapterView.getItemAtPosition(i).toString()
            db.SearchData(params)
            val context = this;
            MaterialAlertDialogBuilder(context).apply{
                setTitle("Data : $params, Age : ${DBHelper.getAge}")
                setIcon(R.drawable.ic_user)
                setMessage("What do you want to do?")
                setPositiveButton("Delete"){_,_ ->

                    db.deleteData(params)
                    Toast.makeText(this@MainActivity, "Data has been deleted", Toast.LENGTH_SHORT).show()
                    Display()
                }
                setNegativeButton("Update"){_,_ ->

                    val moveIntent = Intent(this@MainActivity, UpdateActivity::class.java).apply {
                        putExtra(UpdateActivity.Nama, params)
                        putExtra(UpdateActivity.Age, DBHelper.getAge)
                    }

                    //moveIntent.putExtra(UpdateActivity.Age, tiAge.editText?.text?.toString())
                    startActivity(moveIntent)
                    finish()
                }
                setNeutralButton("Cancel"){_,_ ->

                }
            }.create().show()

        }

    }

}