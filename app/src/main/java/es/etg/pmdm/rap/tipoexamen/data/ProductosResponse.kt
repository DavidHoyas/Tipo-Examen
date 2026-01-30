package es.etg.pmdm.rap.tipoexamen.data

data class ProductosResponse(
    val id: String,
    val name: String?,
    val data: Map<String, Any>?
)
