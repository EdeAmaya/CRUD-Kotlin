package paco.crudpaco

import Modelo.ClaseConexion
import Modelo.dataClassProductos
import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtPrecio = findViewById<EditText>(R.id.txtPrecio)
        val txtCantidad = findViewById<EditText>(R.id.txtCantidad)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)

        fun limpiar(){
            txtNombre.setText("")
            txtPrecio.setText("")
            txtCantidad.setText("")
        }

        ////////////////////////////////TODO:Mostrar Datos////////////////////////////////////////////////////////////////////////////////////////////////////

        val rcvProductos = findViewById<RecyclerView>(R.id.rcvProductos)
        rcvProductos.layoutManager = LinearLayoutManager(this)


        fun obtenerDatos(): List<dataClassProductos>{
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultset = statement?.executeQuery("select * from tbProductos")!!


            val productos = mutableListOf<dataClassProductos>()
            while (resultset.next()){
                val uuid = resultset.getString("uuid")
                val nombre = resultset.getString("nombreProducto")
                val precio = resultset.getInt("precio")
                val cantidad = resultset.getInt("cantidad")
                val producto = dataClassProductos(uuid,nombre,precio,cantidad)
                productos.add(producto)
            }
            return productos
        }


        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val miAdapter = Adaptador(productosDB)
                rcvProductos.adapter = miAdapter
            }
        }

        /////////////////////////////////TODO:Guardar Datos///////////////////////////////////////////////////////////////

        btnAgregar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                val claseConexion =ClaseConexion().cadenaConexion()

                val addProducto = claseConexion?.prepareStatement("insert into tbProductos(uuid,nombreProducto,precio,cantidad)values(?,?,?,?)")!!
                addProducto.setString(1,UUID.randomUUID().toString())
                addProducto.setString(2,txtNombre.text.toString())
                addProducto.setInt(3,txtPrecio.text.toString().toInt())
                addProducto.setInt(4,txtCantidad.text.toString().toInt())
                addProducto.executeUpdate()

                val nuevosProductos = obtenerDatos()

                withContext(Dispatchers.Main){
                    (rcvProductos.adapter as? Adaptador)?.actualizarLista(nuevosProductos)
                }

            }
            ///limpiar()
        }


    }
}