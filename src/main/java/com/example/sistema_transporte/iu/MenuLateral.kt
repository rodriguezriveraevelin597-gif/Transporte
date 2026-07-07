package com.example.sistema_transporte.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuLateral(
    drawerState: DrawerState,
    scope: CoroutineScope,
    rol: String, // 🌟 CORRECCIÓN: Cambiado de esPasajero: Boolean a rol: String
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.statusBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                when (rol) {
                    //////////////////////////////////////////
                    // MENÚ DEL PASAJERO
                    //////////////////////////////////////////
                    "pasajero" -> {
                        NavigationDrawerItem(
                            label = { Text("Inicio") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("pasajero") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Quejas") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("quejas") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Alertas") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("alertas") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Configuración") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("configuracion") } }
                        )
                    }

                    //////////////////////////////////////////
                    // MENÚ DEL CONDUCTOR (OPERADOR)
                    //////////////////////////////////////////
                    "conductor" -> {
                        NavigationDrawerItem(
                            label = { Text("Inicio") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("conductor_inicio") } }

                        )
                        NavigationDrawerItem(
                            label = { Text("Registrar unidad") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("conductor_configuracion") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Cambios de ruta") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("conductor_cambios") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Avisos") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("conductor_avisos") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Sanciones") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("conductor_sanciones") } }
                        )

                    }

                    //////////////////////////////////////////
                    // MENÚ DEL ADMINISTRADOR 💻
                    //////////////////////////////////////////
                    "admin" -> {
                        NavigationDrawerItem(
                            label = { Text("Inicio") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_inicio") } }

                        )
                        NavigationDrawerItem(
                            label = { Text("Control de rutas") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_control_operativo") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Sancionar") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_sancionar") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Nuevo Aviso") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_aviso") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Buzón de reportes") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_reportes") } }
                        )

                        NavigationDrawerItem(
                            label = { Text("Agragar destinos") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_catalogo") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Estado de las rutas") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_control_rutas") } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Tokens de Acceso") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close(); onNavigate("admin_tokens") } }
                        )
                        //"admin_control_operativo"
                       // NavigationDrawerItem(
                          //  label = { Text("Configuración") },
                          //  selected = false,
                            //onClick = { scope.launch { drawerState.close(); onNavigate("admin_configuracion") } }
                       // )
                    }
                }
            }
        }
    ) {
        content()
    }
}