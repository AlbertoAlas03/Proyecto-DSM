package com.example.ag221353_ms172008_gg171680
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SeleccionarModuloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_modulo)

        // 1. Obtener el nombre del usuario desde el intent
        val nombreUsuario = intent.getStringExtra("nombre_usuario")

        // 2. Referencia al TextView donde mostrarás el nombre del usuario
        val usuarioSesionTextView = findViewById<TextView>(R.id.ModuloTXT)
        usuarioSesionTextView.text = "Usuario activo: $nombreUsuario"

        // 3. Opcional: botones con funcionalidades futuras
        val btnOpcion1 = findViewById<Button>(R.id.Btn_Opcion1)
        val btnOpcion2 = findViewById<Button>(R.id.Btn_Opcion2)

        btnOpcion1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USUARIO_ID", intent.getIntExtra("USUARIO_ID", -1))
            startActivity(intent)
            finish()
        }

        btnOpcion2.setOnClickListener {
            val intent = Intent(this, RealizarEvaluacionActivity::class.java)
            startActivity(intent)
        }
    }
}