package com.example.curso


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.curso.usuarios.Login


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ingresar = findViewById<Button>(R.id.Ingresar)
        ingresar.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


    }
}
