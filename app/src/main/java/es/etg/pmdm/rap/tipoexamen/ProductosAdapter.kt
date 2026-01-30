package es.etg.pmdm.rap.tipoexamen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.etg.pmdm.rap.tipoexamen.database.ProductosEntity

class ProductosAdapter(
    private var lista: List<ProductosEntity>
) : RecyclerView.Adapter<ProductosAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDatos: TextView = view.findViewById(R.id.tvDatos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lista[position]

        holder.tvNombre.text = producto.nombre
        holder.tvDatos.text = producto.data
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarProductos(nuevaLista: List<ProductosEntity>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
