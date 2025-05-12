package com.example.ag221353_ms172008_gg171680

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ag221353_ms172008_gg171680.R
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract.UsuarioEntry


class IngresarModuloActivity : AppCompatActivity()  {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase

    private lateinit var correoEditText: EditText
    private lateinit var contrasenaEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var salirButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreso_modulo)

        dbHelper =DatabaseHelper(this)
        database = dbHelper.readableDatabase

        correoEditText = findViewById(R.id.CorreoLogin)
        contrasenaEditText = findViewById(R.id.PassLogin)
        loginButton = findViewById(R.id.Btn_Logeo)
        salirButton = findViewById(R.id.Btn_Salir)

        loginButton.setOnClickListener {
            val correo = correoEditText.text.toString().trim()
            val contrasena = contrasenaEditText.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                if (validarCredenciales(correo, contrasena)) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "¡Bienvenido $correo!", Toast.LENGTH_SHORT).show()
                    // Aquí podrías ir a otra actividad
                    // startActivity(Intent(this, MainActivity::class.java))
                    val intent = Intent(this, SeleccionarModuloActivity::class.java)
                    intent.putExtra("nombre_usuario", correo) // Opcional: pasar el nombre a la siguiente activity
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        salirButton.setOnClickListener {
            finish() // Cierra la actividad
        }
    }

    private fun validarCredenciales(usuario: String, contrasena: String): Boolean {
        val cursor = database.rawQuery(
            "SELECT * FROM ${DatabaseContract.UsuarioEntry.TABLE_NAME} WHERE ${DatabaseContract.UsuarioEntry.COLUMN_NOMBRE} = ? AND ${DatabaseContract.UsuarioEntry.COLUMN_CONTRASENA} = ?",
            arrayOf(usuario, contrasena)
        )

        val valido = cursor.count > 0
        cursor.close()
        return valido
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }
}