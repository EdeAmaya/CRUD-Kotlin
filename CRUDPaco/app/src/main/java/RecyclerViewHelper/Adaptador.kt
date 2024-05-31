package RecyclerViewHelper

import Modelo.ClaseConexion
import Modelo.dataClassProductos
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paco.crudpaco.R
import paco.crudpaco.detalles_productos
import java.util.UUID

class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {

    fun actualizarLista(nuevaLista:List<dataClassProductos>){
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun actualizarListaDespuesDeActualizarDatos(uuid: String,nuevoNombre:String){
        val index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[index].nombreProducto = nuevoNombre
        notifyItemChanged(index)
    }

    fun eliminarRegistro(nombreProducto: String,posicion: Int){



        val listaDatos =Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO){
            val objConexion = ClaseConexion().cadenaConexion()
            val delProductos = objConexion?.prepareStatement("delete tbProductos where nombreProducto = ?")!!
            delProductos.setString(1,nombreProducto)
            delProductos.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun actualizarProducto(nombreProducto: String,uuid: String){
        GlobalScope.launch(Dispatchers.IO){
            val objConexion = ClaseConexion().cadenaConexion()
            val updateProducto = objConexion?.prepareStatement("update tbProductos set nombreProducto = ? where uuid = ?")!!
            updateProducto.setString(1,nombreProducto)
            updateProducto.setString(2,uuid)
            updateProducto.executeUpdate()

            val commit = objConexion?.prepareStatement("commit")!!
            commit.executeUpdate()

            withContext(Dispatchers.Main){
                actualizarListaDespuesDeActualizarDatos(uuid,nombreProducto)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(vista)    }
    override fun getItemCount() = Datos.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = Datos[position]
        holder.textView.text = producto.nombreProducto

        val item = Datos[position]

        holder.imgBorrar.setOnClickListener {
            val context =  holder.itemView.context

            val builder = AlertDialog.Builder(context)

            builder.setTitle("¿Estas seguro?")

            builder.setMessage("¿Desea eliminar el registro?")


            builder.setNegativeButton("No"){dialog,which ->

            }

            builder.setPositiveButton("Si"){dialog,which ->
                eliminarRegistro(item.nombreProducto, position)
            }



            val alertDialog = builder.create()

            alertDialog.show()
        }

        holder.imgEditar.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar nombre")

            val nuevoNombre = EditText(context)
            nuevoNombre.setHint(item.nombreProducto)
            builder.setView(nuevoNombre)

            builder.setPositiveButton("Actualizar"){dialog,wich ->
                actualizarProducto(nuevoNombre.text.toString(),item.uuid)
            }

            builder.setNegativeButton("Cancelar"){dialog,wich ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        holder.itemView.setOnClickListener {
         val context = holder.itemView.context

            val pantallaDetalles = Intent(context,detalles_productos::class.java)
            pantallaDetalles.putExtra("uuid",item.uuid)
            pantallaDetalles.putExtra("nombre",item.nombreProducto)
            pantallaDetalles.putExtra("precio",item.precio)
            pantallaDetalles.putExtra("cantidad",item.cantidad)

            context.startActivity(pantallaDetalles)
        }

    }
}