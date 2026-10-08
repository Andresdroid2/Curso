package com.example.curso

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.curso.usuarios.Login

class LoginP : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_p)

        val iniciar = findViewById<Button>(R.id.InicioA)
        val emailEditTextA = findViewById<EditText>(R.id.emailEditTextA)
        val passwordEditTextA = findViewById<EditText>(R.id.passwordEditTextA)
        val btnUsuarios = findViewById<Button>(R.id.btnUsuario)
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)

        btnAdmin.setOnClickListener {
            btnAdmin.setTextColor(Color.WHITE)
            btnUsuarios.setTextColor(Color.BLACK)
            finish()
            startActivity(Intent(this, LoginP::class.java))
        }
        btnUsuarios.setOnClickListener {
            btnUsuarios.setTextColor(Color.WHITE)
            btnAdmin.setTextColor(Color.BLACK)
            finish()
            startActivity(Intent(this, Login::class.java))
        }

        iniciar.setOnClickListener {
            RepositorioUsuarios.cerrarSesion()

            val email = emailEditTextA.text.toString().trim()
            val password = passwordEditTextA.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe ingresar email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = RepositorioUsuarios.iniciarSesion(email, password)

            if (usuario == null) {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            } else if (!usuario.esAdmin) {
                RepositorioUsuarios.cerrarSesion()
                Toast.makeText(this, "Esta cuenta no tiene permisos de administrador", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, Administracion::class.java))
                finish()
            }
        }
    }
}