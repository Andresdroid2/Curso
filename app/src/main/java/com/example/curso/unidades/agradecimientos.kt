package com.example.curso.unidades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.curso.Generarcertificado
import com.example.curso.R

class agradecimientos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agradecimientos)
        //boton para finalizar curso
        val finalizarcurso = findViewById<Button>(R.id.FinalizarC)

        //ir a la pantalla de generar el certificado
        finalizarcurso.setOnClickListener {
            val intent = Intent(this, Generarcertificado::class.java)
            startActivity(intent)
            finish()
        }
    }
}