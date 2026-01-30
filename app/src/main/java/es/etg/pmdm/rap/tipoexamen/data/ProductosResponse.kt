package es.etg.pmdm.rap.tipoexamen.data

data class ProductosResponse(
    val id: Int = 0,
    var name: String,
    var data: Map<String, Any>?
)