package com.example.sistema_transporte.network


import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // --- AUTENTICACIÓN Y REGISTRO ---
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/registro")
    fun registro(@Body request: RegistroRequest): Call<RegistroResponse>

    @POST("api/generar_token_registro")
    fun generarTokenRegistro(@Body request: TokenRegistroRequest): Call<TokenRegistroResponse>

    // --- GESTIÓN DE UNIDADES (CHOFER) ---
    // 1. Vinculación usando el modelo correcto
    @POST("api/chofer/vincular_unidad")
    fun vincularUnidad(@Body request: VinculacionUnidadRequest): Call<GenericResponse>

    // 2. Baja usando el POST correcto
    @POST("api/chofer/dar_de_baja")
    fun darDeBajaUnidad(@Body request: BajaUnidadRequest): Call<GenericResponse>

    // --- GESTIÓN DE RUTAS Y ZONAS ---
    // En tu ApiService.kt
    // --- GESTIÓN DE RUTAS Y ZONAS ---
    @GET("api/paradas/{id_ruta}")
    fun obtenerParadas(@Path("id_ruta") idRuta: Int): Call<List<ParadaResponse>>

    @PUT("api/rutas/{id}/estado")
    fun cambiarEstadoRuta(@Path("id") idRuta: Int, @Body request: EstadoRutaRequest): Call<GenericResponse>

    // Solo deja una vez esta línea y sin la barra final
    @GET("api/zonas")
    fun obtenerZonas(): Call<List<String>>

    @GET("api/obtener_rutas_por_zona/{nombre_zona}")
    fun obtenerRutasPorZona(@Path("nombre_zona") zona: String): Call<List<RutaResponse>>
    // --- ALTAS DESDE CATÁLOGOS ---
    @POST("api/alta/zona")
    fun altaZona(@Body request: ZonaRequest): Call<GenericResponse>

    @POST("api/alta_ruta")
    fun altaRuta(@Body request: RutaRequest): Call<GenericResponse>

    @POST("api/alta/parada")
    fun altaParada(@Body request: ParadaRequest): Call<GenericResponse>

    // --- CONTROL OPERATIVO ---
    @POST("api/control_operativo/salida")
    fun registrarSalida(@Body request: SalidaRequest): Call<GenericResponse>

    @PUT("api/control_operativo/llegada/{id_control}")
    fun registrarLlegada(@Path("id_control") idControl: Int): Call<GenericResponse>



    @GET("/api/paradas_por_ruta/{id_ruta}")
    fun obtenerParadasPorRuta(@Path("id_ruta") idRuta: Int): Call<List<ParadaResponse>>

    @GET("/api/choferes_por_ruta/{id_ruta}")
    fun obtenerChoferesPorRuta(@Path("id_ruta") idRuta: Int): Call<List<ChoferCombiVinculada>>
    // En ApiService.kt
    @GET("/api/rutas")
    fun obtenerRutas(): Call<List<RutaResponse>>

    @POST("/api/chofer/asignar_ruta")
    fun asignarRutaAlChofer(@Body request: AsignarRutaRequest): Call<GenericResponse>



    data class ZonaResponse(val id: Int, val nombre: String)
    // Agrega estas líneas a tu interfaz ApiService:
    @GET("api/pasajero/tarifa/{id_ruta}")
    fun obtenerTarifa(@Path("id_ruta") idRuta: Int): Call<TarifaResponse>

    // Y asegúrate de tener este modelo de datos (DataModel)
    data class TarifaResponse(val costo: Double)
    // Asegúrate de que esta función exista en tu interfaz ApiService:
    @POST("api/chofer/actualizar_registro")
    fun actualizarRegistro(@Body request: Map<String, Any>): Call<GenericResponse>


    @GET("api/chofer/estado/{id_chofer}")
    fun obtenerEstadoChofer(@Path("id_chofer") idChofer: Int): Call<UnidadInfo>

    @GET("api/control_operativo/activos/{zona}")
    fun obtenerViajesActivos(@Path("zona") zona: String): Call<List<ViajeControlOperativo>>

    @POST("api/control_operativo/llegada")
    fun registrarLlegada(@Body request: LlegadaRequest): Call<GenericResponse>

    @GET("api/choferes_por_ruta/{id_ruta}")
    fun choferesPorRuta(@Path("id_ruta") idRuta: Int): Call<List<ChoferCombiVinculada>>
}




