package com.example.curso.usuarios

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.curso.R
import com.example.curso.RepositorioUsuarios

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //variables
        val emailEditText = findViewById<EditText>(R.id.emailEdit)
        val passwordEditText = findViewById<EditText>(R.id.passwordEdit)
        val confirmarPasswordEdit = findViewById<EditText>(R.id.cContraseña)
        val nombreEdit = findViewById<EditText>(R.id.NombreF)
        val cursoEdit = findViewById<EditText>(R.id.CursoE)
        val btnRegistrar = findViewById<Button>(R.id.registrarse)

        //Boton de registro
        btnRegistrar.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmarPassword = confirmarPasswordEdit.text.toString().trim()
            val nombre = nombreEdit.text.toString().trim()
            val curso = cursoEdit.text.toString().trim()

            // Validaciones
            if (email.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()
                || nombre.isEmpty() || curso.isEmpty()
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password != confirmarPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar en memoria
            val registrado = RepositorioUsuarios.registrar(
                Usuario(email = email, password = password, nombre = nombre, curso = curso)
            )

            if (registrado) {
                Toast.makeText(this, "Registro completo", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login::class.java))
                finish()
            } else {
                Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_LONG).show()
            }
        }
    }
}