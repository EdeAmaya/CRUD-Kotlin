package RecyclerViewHelper

import Modelo.ClaseConexion
import Modelo.dataClassProductos
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import paco.crudpaco.R

class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {

    fun actualizarLista(nuevaLista:List<dataClassProductos>){
        Datos = nuevaLista
        notifyDataSetChanged()
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
            eliminarRegistro(item.nombreProducto, position)
        }
    }
}