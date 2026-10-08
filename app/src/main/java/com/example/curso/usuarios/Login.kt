package com.example.curso.usuarios

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.curso.LoginP
import com.example.curso.R
import com.example.curso.RepositorioUsuarios
import com.example.curso.unidades.unidades

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvRegistrarse = findViewById<TextView>(R.id.tvRegistrarse)
        val btnRecuperar = findViewById<TextView>(R.id.Recupera)
        val btnUsuarios = findViewById<Button>(R.id.btnUsuario)
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        tvRegistrarse.paintFlags = tvRegistrarse.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        btnRecuperar.paintFlags = btnRecuperar.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        tvRegistrarse.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btnUsuarios.setOnClickListener {
            btnUsuarios.setTextColor(Color.WHITE)
            btnAdmin.setTextColor(Color.BLACK)
            finish()
            startActivity(Intent(this, Login::class.java))
        }
        btnAdmin.setOnClickListener {
            btnAdmin.setTextColor(Color.WHITE)
            btnUsuarios.setTextColor(Color.BLACK)
            finish()
            startActivity(Intent(this, LoginP::class.java))
        }

        val iniciar = findViewById<Button>(R.id.iniciar)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        btnRecuperar.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            recuperarContrasena(email)
        }

        iniciar.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe ingresar email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = RepositorioUsuarios.iniciarSesion(email, password)

            if (usuario != null) {
                Toast.makeText(this, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, unidades::class.java))
                finish()
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Sin servidor no se puede enviar un correo, así que se cambia la contraseña directamente
    private fun recuperarContrasena(email: String) {
        if (email.isBlank()) {
            Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show()
            return
        }
        if (!RepositorioUsuarios.existeCorreo(email)) {
            Toast.makeText(this, "Ese correo no está registrado", Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(this).apply {
            hint = "Nueva contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("Restablecer contraseña")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nueva = input.text.toString().trim()
                if (nueva.length < 6) {
                    Toast.makeText(this, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                } else {
                    RepositorioUsuarios.cambiarPassword(email, nueva)
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}