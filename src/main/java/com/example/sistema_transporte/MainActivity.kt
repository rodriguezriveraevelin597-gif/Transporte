package com.example.sistema_transporte

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
// Cambia "network" si tu archivo TokenViewModel está en otra carpeta


// 🌟 IMPORTACIONES COMPLETAS
import com.example.sistema_transporte.iu.*
import com.example.sistema_transporte.ui.*
import com.example.sistema_transporte.ui.theme.*
import com.example.sistema_transporte.network.*
import com.example.sistema_transporte.network.ParadaResponse
import com.example.sistema_transporte.network.SalidaRequest
import com.example.sistema_transporte.network.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sistema_TransporteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var pantallaActual by remember { mutableStateOf("inicio") }
                    val contexto = LocalContext.current

                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    var rolPostRegistro by remember { mutableStateOf("") }


                    // Estados de base de datos
                    var listaZonas by remember { mutableStateOf<List<String>>(emptyList()) }
                    var listaRutas by remember { mutableStateOf<List<RutaResponse>>(emptyList()) }
                    var datosUnidadChofer by remember { mutableStateOf<UnidadInfo?>(null) }
                    // Dentro de tu class MainActivity : ComponentActivity() {



                    // Declara esta lista junto a tus otros estados
                    var listaChoferesUnidades by remember { mutableStateOf<List<ChoferCombiVinculada>>(emptyList()) }
                    // Declara esto arriba de var pantallaActual
                    var idChoferLogueado by remember { mutableStateOf(0) }
                    // Decláralo junto a tus otros estados
                    var listaParadas by remember { mutableStateOf<List<ParadaResponse>>(emptyList()) }
                    // Asegúrate de que esta variable sea de tipo List<ViajeControlOperativo>
                    var viajesActivosDesdeBD by remember { mutableStateOf<List<ViajeControlOperativo>>(emptyList()) }

                    // Cambia la declaración original por esta:
                    var zonaTrabajoActual by remember { mutableStateOf("") }
                    var rutaTrabajoActual by remember { mutableStateOf<RutaModelo?>(null) } // 👈 DEBE SER RUTA MODELO
                    var rutasDeLaZona by remember { mutableStateOf<List<RutaResponse>>(emptyList()) }
                    var tokenGeneradoDesdeBD by remember { mutableStateOf("") }
                    var mensajeErrorToken by remember { mutableStateOf("") }
                    var estaCargandoToken by remember { mutableStateOf(false) }
                    // Agrega esta variable para guardar el folio temporalmente
                    var folioAsignado by remember { mutableStateOf("") }
                    // Decláralo junto a tus otros estados


                    // 1. CARGA ÚNICA AL INICIAR LA APP
                    // 2. CARGA CONDICIONAL SEGÚN PANTALLA
                    LaunchedEffect(pantallaActual) {
                        // Dentro de tu LaunchedEffect(pantallaActual) en MainActivity:

                        // En tu MainActivity, dentro del LaunchedEffect:
                        // Dentro de tu LaunchedEffect en MainActivity:
                        // ... dentro de LaunchedEffect(pantallaActual) { ...

                        if (pantallaActual == "admin_control_operativo" && zonaTrabajoActual.isNotEmpty()) {

                            // 1. CARGAR RUTAS DE LA ZONA
                            RetrofitClient.apiService.obtenerRutasPorZona(zonaTrabajoActual)
                                .enqueue(object : retrofit2.Callback<List<RutaResponse>> {
                                    override fun onResponse(call: retrofit2.Call<List<RutaResponse>>, response: retrofit2.Response<List<RutaResponse>>) {
                                        if (response.isSuccessful) {
                                            rutasDeLaZona = response.body() ?: emptyList()
                                        }
                                    }
                                    override fun onFailure(call: retrofit2.Call<List<RutaResponse>>, t: Throwable) {
                                        Toast.makeText(contexto, "Error al cargar rutas", Toast.LENGTH_SHORT).show()
                                    }
                                })

                            // 2. CARGAR VIAJES ACTIVOS
                            RetrofitClient.apiService.obtenerViajesActivos(zonaTrabajoActual.trim())
                                .enqueue(object : retrofit2.Callback<List<ViajeControlOperativo>> {
                                    override fun onResponse(call: retrofit2.Call<List<ViajeControlOperativo>>, response: retrofit2.Response<List<ViajeControlOperativo>>) {
                                        if (response.isSuccessful) {
                                            viajesActivosDesdeBD = response.body() ?: emptyList()
                                        }
                                    }
                                    override fun onFailure(call: retrofit2.Call<List<ViajeControlOperativo>>, t: Throwable) {
                                        Toast.makeText(contexto, "Error al cargar viajes activos", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }


                        if (pantallaActual == "conductor_configuracion") {
                            RetrofitClient.apiService.obtenerRutas().enqueue(object : Callback<List<RutaResponse>> {
                                override fun onResponse(call: Call<List<RutaResponse>>, response: Response<List<RutaResponse>>) {
                                    if (response.isSuccessful) {
                                        listaRutas = response.body() ?: emptyList()
                                    }
                                }
                                override fun onFailure(call: Call<List<RutaResponse>>, t: Throwable) {
                                    // Toast para saber si algo falla
                                }
                            })
                        }

                        // Bloque 1: Carga de Zonas
                        if (pantallaActual == "admin_inicio" || pantallaActual == "admin_catalogo" || pantallaActual == "admin_control_rutas") {
                            RetrofitClient.apiService.obtenerZonas().enqueue(object : Callback<List<String>> {
                                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                    if (response.isSuccessful) {
                                        listaZonas = response.body() ?: emptyList()
                                    }
                                }
                                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                    Toast.makeText(contexto, "Error al cargar zonas", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }

                        if (pantallaActual == "admin_control_operativo" && zonaTrabajoActual.isNotEmpty()) {
                            RetrofitClient.apiService.obtenerViajesActivos(zonaTrabajoActual).enqueue(object : Callback<List<ViajeControlOperativo>> {
                                override fun onResponse(call: Call<List<ViajeControlOperativo>>, response: Response<List<ViajeControlOperativo>>) {
                                    if (response.isSuccessful) {
                                        // Aquí debes actualizar el estado que usa AdminControlOperativoScreen
                                        // Asegúrate de tener esta variable declarada en tu MainActivity
                                        viajesActivosDesdeBD = response.body() ?: emptyList()
                                    }
                                }
                                override fun onFailure(call: Call<List<ViajeControlOperativo>>, t: Throwable) {
                                    Toast.makeText(contexto, "Error al cargar viajes activos", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                        if (pantallaActual == "conductor_inicio" && idChoferLogueado != 0) {
                            RetrofitClient.apiService.obtenerEstadoChofer(idChoferLogueado)
                                .enqueue(object : Callback<UnidadInfo> {
                                    override fun onResponse(call: Call<UnidadInfo>, response: Response<UnidadInfo>) {
                                        // Si la respuesta es exitosa actualiza, si no, pon null para mostrar vacío
                                        datosUnidadChofer = if (response.isSuccessful) response.body() else null
                                    }
                                    override fun onFailure(call: Call<UnidadInfo>, t: Throwable) {
                                        datosUnidadChofer = null
                                    }
                                })
                        }
                    }


                    // Listas de control de pantallas
                    val pantallasPasajero = listOf("pasajero", "quejas", "alertas", "configuracion")
                    val pantallasConductor = listOf(
                        "conductor_inicio",
                        "conductor_cambios",
                        "conductor_avisos",
                        "conductor_sanciones",
                        "conductor_configuracion"
                    )
                    //val pantallasAdmin = listOf("admin_inicio", "admin_sancionar", "admin_aviso", "admin_reportes", "admin_tokens", "admin_catalogo", "admin_configuracion")
                    val pantallasAdmin = listOf(
                        "admin_inicio",
                        "admin_sancionar",
                        "admin_aviso",
                        "admin_reportes",
                        "admin_tokens",
                        "admin_catalogo",
                        "admin_configuracion",
                        "admin_control_rutas",
                        "admin_control_operativo"
                    )
                    when {
                        //////////////////////////////////////////
                        // Flujo Pasajeros
                        //////////////////////////////////////////
                        pantallaActual in pantallasPasajero -> {
                            MenuLateral(
                                drawerState = drawerState,
                                scope = scope,
                                rol = "pasajero",
                                onNavigate = { destino -> pantallaActual = destino }
                            ) {
                                when (pantallaActual) {
                                    "pasajero" -> PasajeroScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                    "quejas" -> QuejasScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                    "alertas" -> AlertasScreen(
                                        zonasDesdeBD = listaZonas,
                                        rutasFiltradasDesdeBD = emptyList(),
                                        alertasDesdeBD = emptyList(),
                                        onZonaSeleccionada = {},
                                        onMenuClick = { scope.launch { drawerState.open() } }
                                    )

                                    "configuracion" -> ConfiguracionScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                }
                            }
                        }

                        //////////////////////////////////////////
                        // Flujo Conductores
                        //////////////////////////////////////////
                        pantallaActual in pantallasConductor -> {
                            MenuLateral(
                                drawerState = drawerState,
                                scope = scope,
                                rol = "conductor",
                                onNavigate = { destino -> pantallaActual = destino }
                            ) {
                                when (pantallaActual) {
                                    "conductor_inicio" -> ConductoresScreen(
                                        datosUnidad = datosUnidadChofer, // <--- Aquí inyectas los datos
                                        onMenuClick = { scope.launch { drawerState.open() } }
                                    )
                                    "conductor_cambios" -> CambioRutaScreen(
                                        zonasDesdeBD = listaZonas,
                                        rutasFiltradasDesdeBD = emptyList(),
                                        onZonaSeleccionada = {},
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onEnviarCambioSector = { _, _, _ -> }
                                    )

                                    "conductor_avisos" -> AvisosScreen(
                                        cambiosRutaDesdeBD = emptyList(),
                                        onMenuClick = { scope.launch { drawerState.open() } }
                                    )

                                    "conductor_sanciones" -> SancionesScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                    // En MainActivity, al llamar a ConductorConfiguracionScreen:
                                    "conductor_configuracion" -> ConductorConfiguracionScreen(
                                        idChoferActual = idChoferLogueado,
                                        listaRutas = listaRutas,
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onBajaExitosa = {
                                            datosUnidadChofer = null // Esto limpia el inicio
                                            pantallaActual = "conductor_inicio" // Esto te regresa al inicio
                                        }

                                    )
                                }
                            }
                        }

                        //////////////////////////////////////////
                        // Flujo Administrador
                        //////////////////////////////////////////
                        pantallaActual in pantallasAdmin -> {
                            MenuLateral(
                                drawerState = drawerState,
                                scope = scope,
                                rol = "admin",
                                onNavigate = { destino -> pantallaActual = destino }
                            ) {
                                when (pantallaActual) {
                                    "admin_inicio" -> {
                                        val rutasMapeadas = rutasDeLaZona.map { rutaBD ->
                                            RutaModelo(
                                                id = rutaBD.id,
                                                codigo = rutaBD.codigo,
                                                nombre = rutaBD.nombre,
                                                estaActiva = rutaBD.estaActiva
                                            )
                                        }

                                        AdminInicioScreen(
                                            zonasDesdeBD = listaZonas,
                                            rutasDesdeBD = rutasMapeadas,
                                            zonaTrabajoActual = zonaTrabajoActual,
                                            rutaTrabajoActual = rutaTrabajoActual,
                                            onZonaSeleccionada = { zona ->
                                                zonaTrabajoActual = zona
                                                rutaTrabajoActual =
                                                    null // Reset de la ruta seleccionada al cambiar zona

                                                RetrofitClient.apiService.obtenerRutasPorZona(zona)
                                                    .enqueue(object :
                                                        retrofit2.Callback<List<RutaResponse>> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<List<RutaResponse>>,
                                                            response: retrofit2.Response<List<RutaResponse>>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                rutasDeLaZona =
                                                                    response.body() ?: emptyList()
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: retrofit2.Call<List<RutaResponse>>,
                                                            t: Throwable
                                                        ) {
                                                            Toast.makeText(
                                                                contexto,
                                                                "Error al traer rutas de la zona",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    })
                                            },
                                            onRutaSeleccionada = { rutaSelected ->
                                                rutaTrabajoActual = rutaSelected
                                            },
                                            alertasDesdeBD = emptyList(), // Tu lista original de alertas
                                            onMenuClick = { scope.launch { drawerState.open() } },
                                            onEstadoRutaChanged = { idRuta, nuevoEstado ->
                                                RetrofitClient.apiService.cambiarEstadoRuta(
                                                    idRuta,
                                                    EstadoRutaRequest(nuevoEstado)
                                                )
                                                    .enqueue(object :
                                                        retrofit2.Callback<GenericResponse> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            response: retrofit2.Response<GenericResponse>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                val msg = response.body()?.mensaje
                                                                    ?: "Estado actualizado"
                                                                Toast.makeText(
                                                                    contexto,
                                                                    msg,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                                // Sincronizamos la lista global en memoria
                                                                rutasDeLaZona = rutasDeLaZona.map {
                                                                    if (it.id == idRuta) it.copy(
                                                                        estaActiva = nuevoEstado
                                                                    ) else it
                                                                }

                                                                // Si la ruta modificada es la asignada hoy, cambiamos su estado también
                                                                if (rutaTrabajoActual?.id == idRuta) {
                                                                    rutaTrabajoActual =
                                                                        rutaTrabajoActual?.copy(
                                                                            estaActiva = nuevoEstado
                                                                        )
                                                                }
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            t: Throwable
                                                        ) {
                                                        }
                                                    })
                                            }
                                        )
                                    }
                                    "admin_control_operativo" -> {
                                        AdminControlOperativoScreen(
                                            zonaTrabajoActual = zonaTrabajoActual,
                                            rutasDeLaZona = rutasDeLaZona,
                                            // combisYChoferesDisponibles = listaChoferesUnidades, // <--- ¡BORRA ESTA LÍNEA!
                                            viajesActivosDesdeBD = viajesActivosDesdeBD,
                                            idAdminActual = idChoferLogueado,

                                            onRegistrarSalida = { idAsignacion, idAdmin, idRuta, horaSalida ->
                                                val request = SalidaRequest(
                                                    id_asignacion = idAsignacion,
                                                    id_admin = idAdmin,
                                                    id_ruta = idRuta, // Ahora sí lo tienes disponible aquí
                                                    hora_salida = horaSalida
                                                )

                                                RetrofitClient.apiService.registrarSalida(request).enqueue(object : retrofit2.Callback<GenericResponse> {
                                                    override fun onResponse(call: retrofit2.Call<GenericResponse>, response: retrofit2.Response<GenericResponse>) {
                                                        if (response.isSuccessful) {
                                                            Toast.makeText(contexto, "Salida registrada exitosamente", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            Toast.makeText(contexto, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    override fun onFailure(call: retrofit2.Call<GenericResponse>, t: Throwable) {
                                                        Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            },

                                            onRegistrarLlegada = { idControl, horaLlegada ->
                                                val request = LlegadaRequest(idControl, horaLlegada)
                                                RetrofitClient.apiService.registrarLlegada(request).enqueue(object : retrofit2.Callback<GenericResponse> {
                                                    override fun onResponse(call: retrofit2.Call<GenericResponse>, response: retrofit2.Response<GenericResponse>) {
                                                        if (response.isSuccessful) {
                                                            Toast.makeText(contexto, "Llegada confirmada", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            Toast.makeText(contexto, "Error al confirmar: ${response.code()}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    override fun onFailure(call: retrofit2.Call<GenericResponse>, t: Throwable) {
                                                        Toast.makeText(contexto, "Error de conexión", Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            },
                                            onMenuClick = { scope.launch { drawerState.open() } }
                                        )
                                    }
                                    "admin_sancionar" -> {
                                        AdminSancionarScreen(
                                            reporteProcedente = null,
                                            onMenuClick = { scope.launch { drawerState.open() } },
                                            onEnviarSancionChofer = { _, _ -> }
                                        )
                                    }

                                    "admin_aviso" -> {
                                        AdminAvisoScreen(onMenuClick = { scope.launch { drawerState.open() } })
                                    }

                                    "admin_reportes" -> {
                                        AdminReportesScreen(
                                            reporteActual = null,
                                            onSiguienteReporte = {},
                                            onIrASancionar = { _ -> },
                                            onMenuClick = { scope.launch { drawerState.open() } }
                                        )
                                    }

                                    "admin_tokens" -> {
                                        GenerarTokenScreen(
                                            tokenGeneradoDesdeBD = tokenGeneradoDesdeBD,
                                            mensajeError = mensajeErrorToken,
                                            estaCargando = estaCargandoToken,
                                            onMenuClick = { scope.launch { drawerState.open() } },
                                            onSolicitarToken = { rol, folio ->
                                                estaCargandoToken = true
                                                mensajeErrorToken = ""

                                                // 🚀 CORRECCIÓN 1: Usar los nombres exactos de tu DataModels.kt
                                                val request = TokenRegistroRequest(
                                                    folio_solicitante = folio,
                                                    rol_destino = rol
                                                )

                                                RetrofitClient.apiService.generarTokenRegistro(request)
                                                    .enqueue(object : retrofit2.Callback<TokenRegistroResponse> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<TokenRegistroResponse>,
                                                            response: retrofit2.Response<TokenRegistroResponse>
                                                        ) {
                                                            estaCargandoToken = false
                                                            if (response.isSuccessful) {
                                                                // 🚀 CORRECCIÓN 2: Cachar 'token_autorizacion' que es el que declaraste
                                                                tokenGeneradoDesdeBD = response.body()?.token_autorizacion ?: ""
                                                                Toast.makeText(contexto, "¡Token generado con éxito!", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                mensajeErrorToken = "Error del servidor al generar el token"
                                                            }
                                                        }

                                                        override fun onFailure(call: retrofit2.Call<TokenRegistroResponse>, t: Throwable) {
                                                            estaCargandoToken = false
                                                            mensajeErrorToken = "Error de red: No se pudo conectar a Flask"
                                                        }
                                                    })
                                            }
                                        )
                                    }
                                    "admin_catalogo" -> {
                                        AdminCatalogosScreen(
                                            rutasDesdeBD = rutasDeLaZona,
                                            zonasDesdeBD = listaZonas,
                                            onMenuClick = { scope.launch { drawerState.open() } },
                                            onGuardarZona = { nombreDeZona ->
                                                RetrofitClient.apiService.altaZona(
                                                    ZonaRequest(
                                                        nombre = nombreDeZona
                                                    )
                                                )
                                                    .enqueue(object :
                                                        retrofit2.Callback<GenericResponse> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            response: retrofit2.Response<GenericResponse>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                val msg = response.body()?.mensaje
                                                                    ?: "Zona registrada"
                                                                Toast.makeText(
                                                                    contexto,
                                                                    msg,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                                RetrofitClient.apiService.obtenerZonas()
                                                                    .enqueue(object :
                                                                        retrofit2.Callback<List<String>> {
                                                                        override fun onResponse(
                                                                            call: retrofit2.Call<List<String>>,
                                                                            resZonas: retrofit2.Response<List<String>>
                                                                        ) {
                                                                            if (resZonas.isSuccessful) {
                                                                                listaZonas =
                                                                                    resZonas.body()
                                                                                        ?: emptyList()
                                                                            }
                                                                        }

                                                                        override fun onFailure(
                                                                            call: retrofit2.Call<List<String>>,
                                                                            t: Throwable
                                                                        ) {
                                                                        }
                                                                    })
                                                            } else {
                                                                Toast.makeText(
                                                                    contexto,
                                                                    "Error del servidor al guardar zona",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            t: Throwable
                                                        ) {
                                                            Toast.makeText(
                                                                contexto,
                                                                "Error al conectar con el servidor",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    })
                                            },
                                            onGuardarRuta = { destinoRuta, zonaAsignada ->
                                                val request = RutaRequest(
                                                    nombre = destinoRuta,
                                                    zona = zonaAsignada
                                                )
                                                RetrofitClient.apiService.altaRuta(request)
                                                    .enqueue(object :
                                                        retrofit2.Callback<GenericResponse> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            response: retrofit2.Response<GenericResponse>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                val msg = response.body()?.mensaje
                                                                    ?: "Ruta registrada con éxito"
                                                                Toast.makeText(
                                                                    contexto,
                                                                    msg,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            } else {
                                                                val codigoError = response.code()
                                                                Toast.makeText(
                                                                    contexto,
                                                                    "Flask rechazó la ruta (Código $codigoError)",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            t: Throwable
                                                        ) {
                                                            Toast.makeText(
                                                                contexto,
                                                                "Fallo total de red: No se llegó a Flask",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    })
                                            },
                                            onGuardarParada = { nombreDeParada, idRuta -> // 🚀 AHORA RECIBE DOS PARÁMETROS
                                                RetrofitClient.apiService.altaParada(
                                                    ParadaRequest(
                                                        nombre = nombreDeParada,
                                                        id_ruta = idRuta
                                                    )
                                                ).enqueue(object : retrofit2.Callback<GenericResponse> {
                                                    override fun onResponse(call: retrofit2.Call<GenericResponse>, response: retrofit2.Response<GenericResponse>) {
                                                        if (response.isSuccessful) {
                                                            Toast.makeText(contexto, "Parada registrada con éxito", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            Toast.makeText(contexto, "Error al registrar", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    override fun onFailure(call: retrofit2.Call<GenericResponse>, t: Throwable) {
                                                        Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            },
                                            onZonaSeleccionada = { zona ->
                                                zonaTrabajoActual = zona
                                                RetrofitClient.apiService.obtenerRutasPorZona(zona)
                                                    .enqueue(object : retrofit2.Callback<List<RutaResponse>> {
                                                        override fun onResponse(call: retrofit2.Call<List<RutaResponse>>, response: retrofit2.Response<List<RutaResponse>>) {
                                                            if (response.isSuccessful) {
                                                                val nuevasRutas = response.body() ?: emptyList()
                                                                rutasDeLaZona = nuevasRutas // <--- ¡Esto es lo que llena la pantalla!

                                                                if (nuevasRutas.isEmpty()) {
                                                                    Toast.makeText(contexto, "No hay rutas en esta zona", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                        override fun onFailure(call: retrofit2.Call<List<RutaResponse>>, t: Throwable) {
                                                            Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show()
                                                        }
                                                    })
                                            }
                                        )
                                    }

                                    "admin_control_rutas" -> {
                                        AdminControlRutasScreen(
                                            zonasDesdeBD = listaZonas,
                                            rutasFiltradas = rutasDeLaZona,
                                            zonaTrabajoActual = zonaTrabajoActual, // 🚀 SE AGREGO: Esto quita el error de la línea 303
                                            onZonaSeleccionada = { zona ->
                                                zonaTrabajoActual = zona

                                                RetrofitClient.apiService.obtenerRutasPorZona(zona)
                                                    .enqueue(object :
                                                        retrofit2.Callback<List<RutaResponse>> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<List<RutaResponse>>,
                                                            response: retrofit2.Response<List<RutaResponse>>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                rutasDeLaZona =
                                                                    response.body() ?: emptyList()
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: retrofit2.Call<List<RutaResponse>>,
                                                            t: Throwable
                                                        ) {
                                                            Toast.makeText(
                                                                contexto,
                                                                "Error al traer rutas de esta zona",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    })
                                            },
                                            onEstadoRutaChanged = { idRuta, nuevoEstado ->
                                                RetrofitClient.apiService.cambiarEstadoRuta(
                                                    idRuta,
                                                    EstadoRutaRequest(nuevoEstado)
                                                )
                                                    .enqueue(object :
                                                        retrofit2.Callback<GenericResponse> {
                                                        override fun onResponse(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            response: retrofit2.Response<GenericResponse>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                Toast.makeText(
                                                                    contexto,
                                                                    "Estado modificado con éxito",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                                // Sincronizamos la lista global
                                                                rutasDeLaZona = rutasDeLaZona.map {
                                                                    if (it.id == idRuta) it.copy(
                                                                        estaActiva = nuevoEstado
                                                                    ) else it
                                                                }

                                                                // Sincronizamos la tarjeta de inicio por si es la misma ruta
                                                                if (rutaTrabajoActual?.id == idRuta) {
                                                                    rutaTrabajoActual =
                                                                        rutaTrabajoActual?.copy(
                                                                            estaActiva = nuevoEstado
                                                                        )
                                                                }
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: retrofit2.Call<GenericResponse>,
                                                            t: Throwable
                                                        ) {
                                                            Toast.makeText(
                                                                contexto,
                                                                "Error de red al actualizar estado",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    })
                                            },
                                            onMenuClick = { scope.launch { drawerState.open() } }
                                        )
                                    }

                                } // Fin del when (pantallaActual) interno del admin
                            } // Fin del MenuLateral
                        } // Fin del flujo pantallasAdmin

                        //////////////////////////////////////////
                        // Bloque General Fuera de Menús Laterales
                        //////////////////////////////////////////
                        else -> {
                            when (pantallaActual) {

                                "inicio" -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "SIRMEX",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(40.dp))
                                        Button(
                                            onClick = { pantallaActual = "login" },
                                            modifier = Modifier.fillMaxWidth().height(50.dp)
                                        ) {
                                            Text("INICIAR SESIÓN")
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedButton(
                                            onClick = { pantallaActual = "registro" }, // 🚀 Salta directo al registro ordenado
                                            modifier = Modifier.fillMaxWidth().height(50.dp)
                                        ) {
                                            Text("REGISTRARSE")
                                        }
                                    }
                                }

                                "login" -> {
                                    LoginScreen(
                                        onLoginSuccess = { correo, password ->
                                            RetrofitClient.apiService.login(LoginRequest(correo, password))
                                                .enqueue(object : retrofit2.Callback<LoginResponse> {
                                                    override fun onResponse(
                                                        call: retrofit2.Call<LoginResponse>,
                                                        response: retrofit2.Response<LoginResponse>
                                                    ) {
                                                        if (response.isSuccessful) {
                                                            val respuesta = response.body()
                                                            idChoferLogueado = respuesta?.id_chofer ?: respuesta?.id_usuario ?: 0

                                                            // Normalizamos el rol a mayúsculas y quitamos espacios
                                                            val rolLimpio = respuesta?.rol?.trim()?.uppercase() ?: "PASAJERO"

                                                            // ESTO TE AYUDARÁ A VER QUÉ ESTÁ LLEGANDO REALMENTE
                                                            Toast.makeText(contexto, "DEBUG: Rol detectado: $rolLimpio", Toast.LENGTH_LONG).show()

                                                            pantallaActual = when {
                                                                // Aquí aseguramos que cualquier variante de ADMIN te lleve a admin_inicio
                                                                rolLimpio == "ADMIN" || rolLimpio.contains("ADMIN") -> "admin_inicio"
                                                                // Aquí aseguramos que cualquier variante de CHOFER te lleve a conductor_inicio
                                                                rolLimpio == "CHOFER" || rolLimpio.contains("CHOFER") -> "conductor_inicio"
                                                                else -> "pasajero"
                                                            }
                                                        } else {
                                                            Toast.makeText(contexto, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }

                                                    override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                                                        Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                        },
                                        onVolver = { pantallaActual = "inicio" }
                                    )
                                }
                                // =====================================================
                                // CASO CONFIGURADO AL 100%: REGISTRO CON CARD DE TOKEN
                                // =====================================================
                                "registro" -> {
                                    RegistroScreen(
                                        onRegistroCompleto = { nom, apPat, apMat, corr, tel, pass, rolSeleccionado, tokenAcceso ->

                                            rolPostRegistro = rolSeleccionado.lowercase()

                                            val nuevoRegistro = RegistroRequest(
                                                nombre = nom,
                                                apellido_paterno = apPat,
                                                apellido_materno = apMat,
                                                correo = corr,
                                                telefono = tel,
                                                password = pass,
                                                rol = rolSeleccionado,
                                                token_acceso = if (rolSeleccionado == "PASAJERO") null else tokenAcceso
                                            )

                                            RetrofitClient.apiService.registro(nuevoRegistro)
                                                .enqueue(object : retrofit2.Callback<RegistroResponse> {
                                                    override fun onResponse(
                                                        call: retrofit2.Call<RegistroResponse>,
                                                        response: retrofit2.Response<RegistroResponse>
                                                    ) {
                                                        if (response.isSuccessful) {
                                                            val body = response.body()

                                                            // Si el servidor nos mandó un folio, lo guardamos y navegamos
                                                            if (body?.folio_asignado != null) {
                                                                folioAsignado = body.folio_asignado
                                                                pantallaActual = "resultado_folio"
                                                            } else {
                                                                pantallaActual = "terminos"
                                                            }
                                                        } else {
                                                            val errorMsg = response.errorBody()?.string() ?: "Error en el registro"
                                                            Toast.makeText(contexto, errorMsg, Toast.LENGTH_LONG).show()
                                                        }
                                                    }

                                                    override fun onFailure(call: retrofit2.Call<RegistroResponse>, t: Throwable) {
                                                        Toast.makeText(contexto, "Error de red: No se pudo conectar a Flask", Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                        },
                                        onVolver = { pantallaActual = "inicio" }
                                    )
                                }
                                "resultado_folio" -> {
                                    ResultadoRegistroScreen(
                                        folio = folioAsignado,
                                        onContinuar = { pantallaActual = "terminos" }
                                    )
                                }

                                "terminos" -> {
                                    TerminosScreen(
                                        rol = rolPostRegistro,
                                        onAceptar = {
                                            pantallaActual = when (rolPostRegistro) {
                                                "pasajero" -> "pasajero"
                                                "conductor", "chofer" -> "conductor_inicio"
                                                "admin", "administrador" -> "admin_inicio"
                                                else -> "pasajero"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    } // Fin del when general
                } // Fin del Surface
            } // Fin del Theme
        } // Fin del setContent
    } // Fin del onCreate
} // Fin de la clase MainActivity

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioPreview() {
    Sistema_TransporteTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "SIRMEX", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
