package com.example.ag221353_ms172008_gg171680

import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ag221353_ms172008_gg171680.database.DatabaseContract
import com.example.ag221353_ms172008_gg171680.database.DatabaseHelper
import com.example.ag221353_ms172008_gg171680.models.Evaluacion

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonCrearEvaluacion: Button
    private lateinit var buttonVolver: Button
    private var evaluacionesList = mutableListOf<Evaluacion>()
    private lateinit var adapter: EvaluacionAdapter
    private lateinit var session: SharedPreferences
    private var usuarioId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Configurar sesión
        session = getSharedPreferences("app_prefs", MODE_PRIVATE)
        usuarioId = recoverUserId()

        // 2. Validar sesión
        if (usuarioId == -1) {
            return
        }

        // 3. Inicializar componentes
        initDatabase()
        setupUI()
        cargarEvaluaciones()
    }

    private fun recoverUserId(): Int {
        val intentId = intent.getIntExtra("USUARIO_ID", -1)
        return if (intentId != -1) {
            session.edit().putInt("USER_ID", intentId).apply()
            intentId
        } else {
            session.getInt("USER_ID", -1)
        }
    }

    private fun initDatabase() {
        dbHelper = DatabaseHelper(this)
        db = dbHelper.readableDatabase
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerViewEvaluaciones)
        buttonCrearEvaluacion = findViewById(R.id.buttonCrearEvaluacion)
        buttonVolver = findViewById(R.id.buttonVolver)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EvaluacionAdapter(evaluacionesList) { evaluacion ->
            onEvaluacionClicked(evaluacion)
        }
        recyclerView.adapter = adapter

        // Configurar botón Crear Evaluación
        buttonCrearEvaluacion.setOnClickListener {
            Log.d("MainActivity", "Intentando abrir CrearEvaluacionActivity")

            // Verificación explícita del contexto
            val context = this@MainActivity
            val intent = Intent(context, CrearEvaluacionActivity::class.java).apply {
                putExtra("USUARIO_ID", usuarioId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                startActivity(intent)
                Log.d("MainActivity", "Actividad iniciada con éxito")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al iniciar actividad", e)
                Toast.makeText(context, "No se pudo abrir: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Configurar botón Volver
        buttonVolver.setOnClickListener {
            volverASeleccionarModulo()
        }
    }

    private fun volverASeleccionarModulo() {
        Intent(this, SeleccionarModuloActivity::class.java).apply {
            putExtra("USUARIO_ID", usuarioId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (usuarioId != -1) {
            cargarEvaluaciones()
        }
    }

    private fun cargarEvaluaciones() {
        evaluacionesList.clear()
        val cursor = db.query(
            DatabaseContract.EvaluacionEntry.TABLE_NAME,
            null,
            "${DatabaseContract.EvaluacionEntry.COLUMN_USUARIO_ID} = ?",
            arrayOf(usuarioId.toString()),
            null, null, null
        )

        try {
            while (cursor.moveToNext()) {
                evaluacionesList.add(
                    Evaluacion(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.EvaluacionEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.EvaluacionEntry.COLUMN_NOMBRE)),
                        usuarioId
                    )
                )
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al cargar evaluaciones", e)
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
        } finally {
            cursor.close()
        }
    }

    private fun onEvaluacionClicked(evaluacion: Evaluacion) {
        AlertDialog.Builder(this)
            .setTitle(evaluacion.nombre)
            .setItems(arrayOf("Agregar Preguntas", "Editar Preguntas", "Eliminar")) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, AgregarPreguntaActivity::class.java).apply {
                        putExtra("EVALUACION_ID", evaluacion.id)
                    })
                    1 -> abrirEditarPreguntas(evaluacion.id)
                    2 -> confirmarEliminacion(evaluacion.id)
                }
            }
            .show()
    }

    private fun abrirEditarPreguntas(evaluacionId: Int) {
        Intent(this, EditarPreguntasActivity::class.java).apply {
            putExtra("EVALUACION_ID", evaluacionId)
            startActivity(this)
        }
    }

    private fun confirmarEliminacion(evaluacionId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Eliminar esta evaluación y todas sus preguntas?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarEvaluacion(evaluacionId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarEvaluacion(evaluacionId: Int) {
        try {
            db.beginTransaction()

            // Eliminar preguntas
            db.delete(
                DatabaseContract.PreguntaEntry.TABLE_NAME,
                "${DatabaseContract.PreguntaEntry.COLUMN_EVALUACION_ID} = ?",
                arrayOf(evaluacionId.toString())
            )

            // Eliminar evaluación
            val rowsDeleted = db.delete(
                DatabaseContract.EvaluacionEntry.TABLE_NAME,
                "${DatabaseContract.EvaluacionEntry.COLUMN_ID} = ?",
                arrayOf(evaluacionId.toString())
            )

            if (rowsDeleted > 0) {
                db.setTransactionSuccessful()
                cargarEvaluaciones()
                Toast.makeText(this, "Evaluación eliminada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            db.endTransaction()
        }
    }


    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}