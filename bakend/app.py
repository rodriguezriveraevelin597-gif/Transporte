from flask import Flask, jsonify, request
from flask_cors import CORS
import pymysql
import random          
from datetime import datetime  
import datetime

app = Flask(__name__)
CORS(app)  # Permite que Android Studio se conecte sin bloqueos de red de origen cruzado

# =====================================================
# CONFIGURACIÓN DE LA CONEXIÓN A LA BASE DE DATOS
# =====================================================
def conectar_bd():
    return pymysql.connect(
        host='hayabusa.proxy.rlwy.net', 
        port=48735,
        user='root',
        password='yjKxlDkeRyQBoFHxYGneCKJeHYAvpeeQ',
        database='railway',
        cursorclass=pymysql.cursors.DictCursor
    )




# =====================================================
# ENDPOINT: LOGIN (Validación de Credenciales y Roles)
# =====================================================
@app.route('/api/login', methods=['POST'])
def login():
    conexion = None
    try:
        datos = request.get_json()
        correo = datos.get('correo')
        password = datos.get('password')

        conexion = conectar_bd()
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            sql = "SELECT id_usuario, nombre FROM Usuario WHERE correo = %s AND password = %s"
            cursor.execute(sql, (correo, password))
            usuario = cursor.fetchone()

            if usuario:
                id_user = usuario['id_usuario']
                
                # Buscamos si es Administrador o Chofer
                cursor.execute("SELECT id_administrador FROM Administrador WHERE id_usuario = %s", (id_user,))
                admin = cursor.fetchone()
                
                if admin:
                    return jsonify({"id_usuario": id_user, "rol": "ADMIN"}), 200
                
                cursor.execute("SELECT id_chofer FROM Chofer WHERE id_usuario = %s", (id_user,))
                chofer = cursor.fetchone()
                
                if chofer:
                    return jsonify({
                        "id_usuario": id_user, 
                        "id_chofer": chofer['id_chofer'], 
                        "rol": "CHOFER"
                    }), 200
                
                return jsonify({"id_usuario": id_user, "rol": "PASAJERO"}), 200
            else:
                return jsonify({"error": "Credenciales incorrectas"}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

# =====================================================
# ENDPOINTS: MONITOREO Y CONTROL DE RUTAS
# =====================================================
@app.route('/api/rutas', methods=['GET'])
def obtener_rutas():
    conexion = None
    try:
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql = """
                SELECT 
                    r.id_ruta AS id,
                    CONCAT('Ruta ', r.id_ruta) AS codigo,
                    r.nombre AS nombre,
                    er.nombre AS estado
                FROM Ruta r
                INNER JOIN Estado_Ruta er ON r.id_estado_ruta = er.id_estado_ruta
            """
            cursor.execute(sql)
            rutas = cursor.fetchall()
            
            lista_limpia = []
            for r in rutas:
                lista_limpia.append({
                    "id": r["id"],
                    "codigo": r["codigo"],
                    "nombre": r["nombre"],
                    "estaActiva": True if r["estado"].upper() == "ACTIVA" else False
                })
        return jsonify(lista_limpia), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

@app.route('/api/rutas/<int:id_ruta>/estado', methods=['PUT'])
def cambiar_estado_ruta(id_ruta): 
    conexion = None
    try:
        datos = request.get_json()
        nuevo_estado_bool = datos.get('estaActiva')
        
        id_estado = 1 if nuevo_estado_bool else 2
        
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql = "UPDATE Ruta SET id_estado_ruta = %s WHERE id_ruta = %s"
            cursor.execute(sql, (id_estado, id_ruta))
            conexion.commit()
        return jsonify({"mensaje": "Estado de ruta actualizado con éxito"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

# =====================================================
# ENDPOINTS: FILTROS EN CASCADA (Zonas y Rutas Relacionadas)
# =====================================================
@app.route('/api/zonas', methods=['GET'])
def obtener_zonas():
    conexion = None
    try:
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            cursor.execute("SELECT nombre FROM Zona")
            zonas = cursor.fetchall()
            lista_zonas = [z['nombre'] for z in zonas]
        return jsonify(lista_zonas), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

@app.route('/api/obtener_rutas_por_zona/<string:nombre_zona>', methods=['GET'])
def obtener_rutas_por_zona(nombre_zona):
    conexion = None
    try:
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql = """
                SELECT 
                    r.id_ruta AS id,
                    CONCAT('Ruta ', r.id_ruta) AS codigo,
                    r.nombre AS nombre,
                    er.nombre AS estado
                FROM Ruta r
                INNER JOIN Zona z ON r.id_zona = z.id_zona
                INNER JOIN Estado_Ruta er ON r.id_estado_ruta = er.id_estado_ruta
                WHERE z.nombre = %s
            """
            cursor.execute(sql, (nombre_zona,))
            rutas = cursor.fetchall()
            
            lista_limpia = []
            for r in rutas:
                lista_limpia.append({
                    "id": r["id"],
                    "codigo": r["codigo"],
                    "nombre": r["nombre"],
                    "estaActiva": True if r["estado"].upper() == "ACTIVA" else False
                })
                
        return jsonify(lista_limpia), 200
    except Exception as e:
        print(f"Error en obtener_rutas_por_zona: {e}")
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

# =====================================================
# ENDPOINTS: TRANSACCIONALES (Altas de Catálogos)
# =====================================================
@app.route('/api/alta/zona', methods=['POST'])
def alta_zona():
    conexion = None
    try:
        datos = request.get_json()
        nombre = datos.get('nombre')
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            cursor.execute("INSERT INTO Zona (nombre) VALUES (%s)", (nombre,))
            conexion.commit()
        return jsonify({"mensaje": "Zona registrada correctamente"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

@app.route('/api/alta_ruta', methods=['POST'])
def alta_ruta():
    conexion = None
    try:
        datos = request.get_json()
        nombre_ruta = datos.get('nombre')
        nombre_zona = datos.get('zona')

        if not nombre_ruta or not nombre_zona:
            return jsonify({'mensaje': 'Faltan campos obligatorios'}), 400

        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            cursor.execute("SELECT id_zona FROM Zona WHERE nombre = %s", (nombre_zona,))
            resultado_zona = cursor.fetchone()

            if not resultado_zona:
                return jsonify({'mensaje': f'La zona "{nombre_zona}" no existe en la base de datos'}), 400

            id_zona_real = resultado_zona['id_zona']

            query = """
                INSERT INTO Ruta (nombre, id_estado_ruta, id_zona) 
                VALUES (%s, %s, %s)
            """
            cursor.execute(query, (nombre_ruta, 1, id_zona_real))
            conexion.commit()

        return jsonify({'mensaje': f'Ruta "{nombre_ruta}" registrada con éxito'}), 201
    except Exception as e:
        print(f"Error al insertar ruta: {e}")
        return jsonify({'error': str(e)}), 500
    finally:
        if conexion:
            conexion.close()


@app.route('/api/alta/parada', methods=['POST'])
def alta_parada():
    conexion = None
    try:
        datos = request.get_json()
        nombre = datos.get('nombre')
        id_ruta = datos.get('id_ruta') # Este es el dato clave que te faltaba
        
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            # Esta es la consulta correcta para tu tabla
            sql = "INSERT INTO Parada (nombre_parada, id_ruta) VALUES (%s, %s)"
            cursor.execute(sql, (nombre, id_ruta))
            conexion.commit()
            
        return jsonify({"mensaje": "Parada guardada en ruta correctamente"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()
# =====================================================
# ENDPOINTS: REPORTE DE INCIDENCIAS, SANCIONES Y CAMBIOS
# =====================================================


@app.route('/api/cambios-ruta', methods=['POST'])
def registrar_cambio_ruta():
    conexion = None
    try:
        datos = request.get_json()
        id_ruta = datos.get('id_ruta')
        descripcion = datos.get('descripcion')

        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql_cambio = "INSERT INTO Cambio_Ruta (id_ruta, fecha_inicio, descripcion) VALUES (%s, CURDATE(), %s)"
            cursor.execute(sql_cambio, (id_ruta, descripcion))
            
            sql_notif = "INSERT INTO Notificacion (titulo, fecha_mensaje, id_ruta) VALUES (%s, NOW(), %s)"
            cursor.execute(sql_notif, (f"Cambio Operativo: {descripcion}", id_ruta))
            
            conexion.commit()
        return jsonify({"mensaje": "Cambio de sector registrado y notificado"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

# Este endpoint le sirve a TODOS los administradores, 
# sin importar si están en el Punto A o B
@app.route('/api/control_operativo/pendientes', methods=['GET'])
def viajes_pendientes():
    conexion = conectar_bd()
    with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
        # Trae todo lo que no haya llegado, para que cualquiera pueda recibirlo
        sql = """SELECT * FROM control_operativo WHERE hora_llegada IS NULL"""
        cursor.execute(sql)
        return jsonify(cursor.fetchall()), 200
# =====================================================
# ENDPOINT: REGISTRO FINAL (Asegúrate de copiar esto tal cual)
# =====================================================
@app.route('/api/registro', methods=['POST'])
def registro():
    conexion = None
    try:
        datos = request.get_json()
        # 1. Normalización de entradas para evitar errores de llaves
        nombre = datos.get('nombre')
        apellido_p = datos.get('apellido_paterno')
        apellido_m = datos.get('apellido_materno')
        correo = datos.get('correo')
        telefono = datos.get('telefono')
        password = datos.get('password')
        rol = datos.get('rol', '').upper().strip()
        
        # Aceptamos 'token_acceso' o 'token' para ser flexibles
        token_enviado = datos.get('token_acceso') or datos.get('token')

        # Validación detallada para saber qué campo falta
        campos = {"nombre": nombre, "correo": correo, "password": password, "rol": rol}
        for campo, valor in campos.items():
            if not valor:
                return jsonify({"error": f"El campo {campo} es obligatorio"}), 400

        conexion = conectar_bd()
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            # 2. Validación de Token
            if rol in ["ADMIN", "ADMINISTRADOR", "CHOFER", "CONDUCTOR"]:
                if not token_enviado:
                    return jsonify({"error": "Se requiere token para este rol"}), 400
                
                # CORRECCIÓN: Usamos "ADMINISTRADOR" para que coincida con tu base de datos
                rol_buscar = "ADMINISTRADOR" if rol in ["ADMIN", "ADMINISTRADOR"] else "CHOFER"
                
                sql_token = """SELECT id FROM tokens_registro 
                               WHERE token_autorizacion = %s AND rol_destino = %s AND usado = 0"""
                
                # Imprimimos para depurar en consola qué estamos buscando
                print(f"DEBUG: Buscando token {token_enviado.strip()} con rol {rol_buscar}")
                
                cursor.execute(sql_token, (token_enviado.strip(), rol_buscar))
                
                if not cursor.fetchone():
                    return jsonify({"error": "Token inválido o ya utilizado"}), 400
            # 3. Insertar Usuario base
            sql_usuario = """INSERT INTO Usuario (nombre, apellido_paterno, apellido_materno, correo, telefono, password) 
                             VALUES (%s, %s, %s, %s, %s, %s)"""
            cursor.execute(sql_usuario, (nombre, apellido_p, apellido_m, correo, telefono, password))
            id_usuario_creado = cursor.lastrowid

            # 4. Lógica según rol
            folio_asignado = None
            if rol == "PASAJERO":
                cursor.execute("INSERT INTO Pasajero (id_usuario) VALUES (%s)", (id_usuario_creado,))
            
            elif rol in ["CONDUCTOR", "CHOFER"]:
                cursor.execute("INSERT INTO Chofer (id_usuario) VALUES (%s)", (id_usuario_creado,))
                cursor.execute("UPDATE tokens_registro SET usado = 1 WHERE token_autorizacion = %s", (token_enviado.strip(),))
            
            elif rol in ["ADMIN", "ADMINISTRADOR"]:
                folio_asignado = f"ADM-{random.randint(1000, 9999)}"
                cursor.execute("INSERT INTO Administrador (id_usuario, folio_unico) VALUES (%s, %s)", (id_usuario_creado, folio_asignado))
                cursor.execute("UPDATE Usuario SET folio_unico = %s WHERE id_usuario = %s", (folio_asignado, id_usuario_creado))
                cursor.execute("UPDATE tokens_registro SET usado = 1 WHERE token_autorizacion = %s", (token_enviado.strip(),))

            conexion.commit()
            
            respuesta = {"mensaje": "Registro exitoso"}
            if folio_asignado: respuesta["folio_asignado"] = folio_asignado
            return jsonify(respuesta), 201

    except Exception as e:
        print(f"ERROR CRÍTICO: {e}") # Mira esto en la consola si falla
        if conexion: conexion.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion: conexion.close()


@app.route('/api/chofer/actualizar_registro', methods=['POST'])
def actualizar_registro():
    data = request.json
    # data debe traer: id_chofer, nuevo_numero_economico, nuevas_placas, nuevo_id_ruta
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # 1. Actualizar Unidad
            sql_unidad = "UPDATE Unidad SET numero_economico = %s, placas = %s WHERE id_chofer = %s"
            cursor.execute(sql_unidad, (data['numero_economico'], data['placas'], data['id_chofer']))
            
            # 2. Actualizar Ruta (en la tabla de Asignación)
            sql_ruta = "UPDATE Asignacion_Ruta SET id_ruta = %s WHERE id_chofer = %s"
            cursor.execute(sql_ruta, (data['id_ruta'], data['id_chofer']))
            
            conexion.commit()
        return jsonify({"mensaje": "Registros actualizados correctamente"}), 200
    finally:
        conexion.close()
@app.route('/api/chofer/estado/<int:id_chofer>', methods=['GET'])
def obtener_estado_chofer(id_chofer):
    conexion = conectar_bd()
    try:
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            # Esta consulta busca el chofer, su unidad y su ruta asignada
            sql = """
                SELECT 
                    u.numero_economico, 
                    u.placa, 
                    r.nombre AS nombre_ruta
                FROM Chofer_Unidad cu
                JOIN Unidad u ON cu.id_unidad = u.id_unidad
                JOIN Asignacion_Ruta ar ON cu.id_chofer = ar.id_chofer
                JOIN Ruta r ON ar.id_ruta = r.id_ruta
                WHERE cu.id_chofer = %s
            """
            cursor.execute(sql, (id_chofer,))
            resultado = cursor.fetchone()
            
            if resultado:
                return jsonify(resultado), 200
            else:
                return jsonify({"error": "No hay registro"}), 404
    finally:
        conexion.close()
# =====================================================
# CONTROL OPERATIVO: VIAJES Y ASIGNACIONES
# =====================================================



@app.route('/api/control_operativo/activos/<string:zona>', methods=['GET'])
def viajes_activos(zona):
    conexion = conectar_bd()
    try:
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            sql = """
                SELECT 
                    c.id_control, c.hora_salida, r.nombre AS nombreRuta, 
                    u.numero_economico AS numeroEconomico, us.nombre AS choferNombre
                FROM control_operativo c
                JOIN Asignacion_Ruta ar ON c.id_asignacion = ar.id_asignacion
                JOIN Ruta r ON ar.id_ruta = r.id_ruta
                JOIN Chofer ch ON ar.id_chofer = ch.id_chofer
                JOIN Usuario us ON ch.id_usuario = us.id_usuario
                JOIN Chofer_Unidad cu ON ch.id_chofer = cu.id_chofer
                JOIN Unidad u ON cu.id_unidad = u.id_unidad
                JOIN Zona z ON r.id_zona = z.id_zona
                WHERE z.nombre = %s AND c.hora_llegada IS NULL
            """
            cursor.execute(sql, (zona.strip(),))
            resultados = cursor.fetchall()
            
            # --- CORRECCIÓN DEL ERROR TIMEDELTA ---
            for fila in resultados:
                for clave, valor in fila.items():
                    if isinstance(valor, (datetime.timedelta, datetime.time, datetime.datetime)):
                        fila[clave] = str(valor)
            
            return jsonify(resultados), 200
    except Exception as e:
        print(f"ERROR crítico en viajes_activos: {e}")
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion: conexion.close()


# 2. Registrar Salida y Llegada (Tus botones de inicio y fin de jornada)


@app.route('/api/admin/sesion_trabajo', methods=['POST'])
def sesion_trabajo():
    datos = request.json
    id_usuario = datos.get('id_usuario') # Recibe el 13
    id_ruta = datos.get('id_ruta')
    
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # Buscamos el admin (9) a partir del usuario (13)
            cursor.execute("SELECT id_administrador FROM administrador WHERE id_usuario = %s", (id_usuario,))
            admin = cursor.fetchone()
            
            if admin:
                id_admin_real = admin['id_administrador']
                sql = "REPLACE INTO administrador_ruta (id_administrador, id_ruta) VALUES (%s, %s)"
                cursor.execute(sql, (id_admin_real, id_ruta))
                conexion.commit()
                return jsonify({"mensaje": "Ruta guardada"}), 200
            else:
                return jsonify({"error": "No se encontró administrador vinculado"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        conexion.close()
            
   

# --- SALIDA: Registra quién despachó ---
# 1. Definimos la función con un nombre consistente
def obtener_id_admin_valido(id_usuario):
    conexion = conectar_bd()
    try:
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            sql = "SELECT id_administrador FROM administrador WHERE id_usuario = %s"
            cursor.execute(sql, (id_usuario,))
            resultado = cursor.fetchone()
            return resultado['id_administrador'] if resultado else id_usuario 
    except Exception as e:
        print(f"Error en traducción de ID: {e}")
        return id_usuario
    finally:
        conexion.close()

# 2. Ruta de SALIDA
@app.route('/api/control_operativo/salida', methods=['POST'])
def registrar_salida():
    data = request.json
    id_asignacion = data.get('id_asignacion')
    id_ruta = data.get('id_ruta')
    hora_salida = data.get('hora_salida')
    
    # 🚀 LLAMAMOS A LA FUNCIÓN CORRECTA
    id_admin = obtener_id_admin_valido(data.get('id_admin'))
    
    conexion = conectar_bd()
    cursor = None
    try:
        cursor = conexion.cursor(pymysql.cursors.DictCursor)
        cursor.execute("SELECT id_administrador FROM administrador WHERE id_administrador = %s", (id_admin,))
        if not cursor.fetchone():
            return jsonify({"error": "ID de administrador no válido"}), 400

        sql = """INSERT INTO control_operativo 
                 (id_asignacion, id_ruta, hora_salida, fecha, id_admin_salida) 
                 VALUES (%s, %s, %s, CURDATE(), %s)"""
        cursor.execute(sql, (id_asignacion, id_ruta, hora_salida, id_admin))
        conexion.commit()
        return jsonify({"mensaje": "Salida registrada"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cursor: cursor.close()
        conexion.close()

# 3. Ruta de LLEGADA
@app.route('/api/control_operativo/llegada', methods=['POST'])
def registrar_llegada():
    data = request.json
    id_control = data.get('id_control')
    hora_llegada = data.get('hora_llegada')
    
    # 🚀 LLAMAMOS A LA FUNCIÓN CORRECTA
    id_admin = obtener_id_admin_valido(data.get('id_admin'))
    
    conexion = conectar_bd()
    cursor = None
    try:
        cursor = conexion.cursor(pymysql.cursors.DictCursor)
        cursor.execute("SELECT hora_llegada FROM control_operativo WHERE id_control = %s", (id_control,))
        viaje = cursor.fetchone()
        
        if viaje and viaje['hora_llegada'] is not None:
            return jsonify({"error": "Llegada ya registrada"}), 403

        sql = """UPDATE control_operativo 
                 SET hora_llegada = %s, id_admin_llegada = %s 
                 WHERE id_control = %s"""
        cursor.execute(sql, (hora_llegada, id_admin, id_control))
        conexion.commit()
        return jsonify({"mensaje": "Llegada finalizada"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cursor: cursor.close()
        conexion.close()




# 3. Gestión de choferes (Vinculación y Disponibilidad)
@app.route('/api/chofer/vincular_unidad', methods=['POST'])
def vincular_unidad():
    data = request.json
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # Busca si la unidad existe, si no, la crea
            cursor.execute("SELECT id_unidad FROM Unidad WHERE numero_economico = %s", (data['numero_economico'],))
            unidad = cursor.fetchone()
            id_unidad = unidad['id_unidad'] if unidad else None
            
            if not id_unidad:
                cursor.execute("INSERT INTO Unidad (placa, numero_economico) VALUES (%s, %s)", (data.get('placas', 'SIN-PLACAS'), data['numero_economico']))
                id_unidad = cursor.lastrowid
            
            cursor.execute("DELETE FROM chofer_unidad WHERE id_chofer = %s", (data['id_chofer'],))
            cursor.execute("INSERT INTO chofer_unidad (id_chofer, id_unidad) VALUES (%s, %s)", (data['id_chofer'], id_unidad))
            conexion.commit()
            return jsonify({"mensaje": "Vinculado correctamente"}), 200
    finally:
        conexion.close()
# Nuevo: Obtener paradas filtradas por ruta
@app.route('/api/paradas_por_ruta/<int:id_ruta>', methods=['GET'])
def obtener_paradas_por_ruta(id_ruta):
    conexion = conectar_bd()
    try:
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            sql = "SELECT id_parada, nombre_parada FROM Parada WHERE id_ruta = %s"
            cursor.execute(sql, (id_ruta,))
            return jsonify(cursor.fetchall()), 200
    finally:
        conexion.close()

# Nuevo: Obtener choferes/unidades disponibles de una ruta específica
@app.route('/api/choferes_por_ruta/<int:id_ruta>', methods=['GET'])
def choferes_por_ruta(id_ruta):
    conexion = conectar_bd()
    try:
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            # Observa el "AS infoDespliegue". Esto obliga a Flask a enviar 
            # el JSON con ese nombre exacto para que Kotlin lo reconozca.
            sql = """
                SELECT 
                    ar.id_asignacion,
                    CONCAT('Unidad: ', u.numero_economico, ' - Chofer: ', us.nombre) AS infoDespliegue
                FROM Asignacion_Ruta ar
                JOIN Chofer ch ON ar.id_chofer = ch.id_chofer
                JOIN Usuario us ON ch.id_usuario = us.id_usuario
                JOIN Chofer_Unidad cu ON ch.id_chofer = cu.id_chofer
                JOIN Unidad u ON cu.id_unidad = u.id_unidad
                WHERE ar.id_ruta = %s
            """
            cursor.execute(sql, (id_ruta,))
            return jsonify(cursor.fetchall()), 200
    finally:
        conexion.close()
@app.route('/api/chofer/asignar_ruta', methods=['POST'])
def asignar_ruta_chofer():
    data = request.json
    try:
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            # Insertamos una nueva asignación con la fecha de hoy
            sql = "INSERT INTO Asignacion_Ruta (id_chofer, id_ruta, fecha_asignacion) VALUES (%s, %s, CURDATE())"
            cursor.execute(sql, (data['id_chofer'], data['id_ruta']))
            conexion.commit()
        return jsonify({"mensaje": "Ruta asignada exitosamente"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion: conexion.close()


# =====================================================
# ENDPOINT: GENERAR TOKENS DE ACCESO (AJUSTADO)
# =====================================================
@app.route('/api/generar_token_registro', methods=['POST'])
def generar_token():
    try:
        datos = request.get_json()
        folio_admin = datos.get('folio_solicitante')
        rol_destino = datos.get('rol_destino', '').upper().strip()

        # 2. Normalización de rol para evitar confusiones ADMIN vs ADMINISTRADOR
        if rol_destino in ["ADMIN", "ADMINISTRADOR"]:
            rol_destino = "ADMINISTRADOR"
        elif rol_destino == "CHOFER":
            rol_destino = "CHOFER"

        if not folio_admin or not rol_destino:
            return jsonify({"error": "Faltan datos"}), 400

        conexion = conectar_bd()
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor: # Usamos DictCursor
            # 3. Asegúrate de que el nombre de la tabla sea minúsculas si así está en tu BD
            sql_buscar = "SELECT id_administrador FROM administrador WHERE folio_unico = %s"
            cursor.execute(sql_buscar, (folio_admin,))
            resultado = cursor.fetchone()

            if not resultado:
                return jsonify({"error": "Folio de administrador inválido"}), 401
            
            id_admin_real = resultado['id_administrador']
            nuevo_token = f"TK-{random.randint(1000, 9999)}"

            sql_insertar = """INSERT INTO tokens_registro 
                              (id_administrador, rol_destino, token_autorizacion, usado) 
                              VALUES (%s, %s, %s, 0)"""
            cursor.execute(sql_insertar, (id_admin_real, rol_destino, nuevo_token))
            conexion.commit()

        return jsonify({
            "mensaje": "Token generado con éxito",
            "token_autorizacion": nuevo_token
        }), 201

    except Exception as e:
        print(f"Error: {e}")
        return jsonify({"error": str(e)}), 500
#---------------------------
@app.route('/api/pasajero/tarifa/<int:id_ruta>', methods=['GET'])
def obtener_tarifa(id_ruta):
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # Selecciona el costo de la tabla trayecto
            sql = "SELECT costo FROM trayecto WHERE id_ruta = %s LIMIT 1"
            cursor.execute(sql, (id_ruta,))
            resultado = cursor.fetchone()
            # Si no hay tarifa, devuelve 0
            return jsonify(resultado if resultado else {"costo": 0}), 200
    finally:
        conexion.close()

@app.route('/api/admin/enviar_notificacion', methods=['POST'])
def enviar_notificacion():
    data = request.json
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # 1. Insertar en tabla notificacion
            sql_not = "INSERT INTO notificacion (titulo, fecha_mensaje) VALUES (%s, NOW())"
            cursor.execute(sql_not, (data['titulo'],))
            id_not = cursor.lastrowid
            
            # 2. Vincular con el usuario (Chofer)
            sql_user = "INSERT INTO usuario_notificacion (id_notificacion, id_usuario, leido) VALUES (%s, %s, 0)"
            cursor.execute(sql_user, (id_not, data['id_usuario_chofer']))
            
            conexion.commit()
            return jsonify({"mensaje": "Enviado"}), 201
    finally:
        conexion.close()


#=============Tarifa==============

# Para el botón Registrar


@app.route('/registrar_tarifas', methods=['POST'])
def registrar_tarifas():
    data = request.json
    # Aquí definimos la conexión localmente, ¡ya no dará error de "not defined"!
    connection = conectar_bd() 
    cursor = None
    try:
        cursor = connection.cursor()
        query = "INSERT INTO Configuracion_Tarifa (id_parametro, valor_actual) VALUES ('tarifa_base', %s), ('incremento_por_km', %s)"
        cursor.execute(query, (data['tarifa_base'], data['incremento_km']))
        connection.commit()
        return jsonify({"mensaje": "Registrado exitosamente"}), 201
    except Exception as e:
        print(f"Error: {e}")
        return jsonify({"error": str(e)}), 400
    finally:
        if cursor: cursor.close()
        if connection: connection.close() # Cerramos la conexión local

@app.route('/modificar_tarifas', methods=['PUT'])
def modificar_tarifas():
    data = request.json
    connection = conectar_bd() # Nueva conexión local
    cursor = None
    try:
        cursor = connection.cursor()
        cursor.execute("UPDATE Configuracion_Tarifa SET valor_actual = %s WHERE id_parametro = 'tarifa_base'", (data['tarifa_base'],))
        cursor.execute("UPDATE Configuracion_Tarifa SET valor_actual = %s WHERE id_parametro = 'incremento_por_km'", (data['incremento_km'],))
        connection.commit()
        return jsonify({"mensaje": "Modificado exitosamente"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cursor: cursor.close()
        if connection: connection.close()
#====================================================
@app.route('/calcular_trayecto', methods=['POST'])
def calcular_trayecto():
    data = request.json
    # Esperamos: ruta_nombre, inicio_nombre, final_nombre
    ruta_nombre = data.get('ruta_nombre')
    inicio_nombre = data.get('inicio_nombre')
    final_nombre = data.get('final_nombre')
    
    connection = conectar_bd()
    cursor = connection.cursor(pymysql.cursors.DictCursor)
    
    try:
        # 1. Obtener IDs usando los nombres de tus tablas reales (Ruta y Parada)
        # Asegúrate de que las columnas sean 'nombre' y 'nombre_parada' según tus otros endpoints
        cursor.execute("SELECT id_ruta FROM Ruta WHERE nombre = %s", (ruta_nombre,))
        id_ruta = cursor.fetchone()['id_ruta']
        
        cursor.execute("SELECT id_parada FROM Parada WHERE nombre_parada = %s", (inicio_nombre,))
        id_inicio = cursor.fetchone()['id_parada']
        
        cursor.execute("SELECT id_parada FROM Parada WHERE nombre_parada = %s", (final_nombre,))
        id_final = cursor.fetchone()['id_parada']
        
        # 2. Obtener Tarifas de tu tabla Configuracion_Tarifa
        cursor.execute("SELECT valor_actual FROM Configuracion_Tarifa WHERE id_parametro = 'tarifa_base'")
        base = float(cursor.fetchone()['valor_actual'])
        
        cursor.execute("SELECT valor_actual FROM Configuracion_Tarifa WHERE id_parametro = 'incremento_por_km'")
        incremento = float(cursor.fetchone()['valor_actual'])
        
        # 3. Calcular costo (Distancia = diferencia de IDs de parada)
        num_paradas = abs(id_final - id_inicio)
        costo_total = base + (num_paradas * incremento)
        
        # 4. Registrar en 'trayecto' (Tu tabla de aprendizaje)
        # Nota: Asegúrate que esta tabla tenga UNIQUE KEY en (id_ruta, id_parada_inicio, id_parada_fin)
        query = """
            INSERT INTO trayecto (id_ruta, id_parada_inicio, id_parada_fin, costo, frecuencia)
            VALUES (%s, %s, %s, %s, 1)
            ON DUPLICATE KEY UPDATE frecuencia = frecuencia + 1, costo = %s
        """
        cursor.execute(query, (id_ruta, id_inicio, id_final, costo_total, costo_total))
        connection.commit()
        
        return jsonify({"costo": costo_total}), 200
        
    except Exception as e:
        print(f"Error en calcular_trayecto: {e}")
        return jsonify({"error": str(e)}), 500
    finally:
        cursor.close()
        connection.close()


#====================Reportes===================
@app.route('/api/tipos_reporte', methods=['GET'])
def obtener_tipos():
    conexion = conectar_bd()
    try:
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute("SELECT * FROM Tipo_Reporte")
            return jsonify(cursor.fetchall())
    finally:
        conexion.close()
    
@app.route('/api/unidad/buscar/<string:numero>', methods=['GET'])
def buscar_unidad(numero):
    conexion = conectar_bd()
    try:
        with conexion.cursor(pymysql.cursors.DictCursor) as cursor:
            # Buscamos el ID por el número económico
            sql = "SELECT id_unidad FROM Unidad WHERE numero_economico = %s"
            cursor.execute(sql, (numero,))
            resultado = cursor.fetchone()
            
            if resultado:
                return jsonify(resultado), 200
            else:
                return jsonify({"error": "Unidad no encontrada"}), 404
    finally:
        conexion.close()

@app.route('/api/guardar_reporte', methods=['POST'])
def guardar_reporte():
    datos = request.json
    print(f"DEBUG: Datos recibidos en Flask: {datos}") # Esto debe salir en tu terminal
    
    id_pasajero = datos.get('id_pasajero')
    id_unidad = datos.get('id_unidad')
    id_tipo_reporte = datos.get('id_tipo_reporte')
    descripcion = datos.get('descripcion')
    
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # Asegúrate que los nombres de columnas sean los mismos que en tu BD (id_pasajero, id_unidad, etc.)
            sql = "INSERT INTO reporte (id_pasajero, id_unidad, id_tipo_reporte, descripcion) VALUES (%s, %s, %s, %s)"
            cursor.execute(sql, (id_pasajero, id_unidad, id_tipo_reporte, descripcion))
            conexion.commit() 
            print("DEBUG: Commit realizado con éxito")
        return jsonify({"mensaje": "Reporte guardado"}), 201
    except Exception as e:
        print(f"DEBUG: ERROR EN MYSQL: {e}")
        return jsonify({"error": str(e)}), 500
    finally:
        conexion.close()
#--------------------------------------------
@app.route('/api/reportes_pendientes/<int:id_admin>', methods=['GET'])
def obtener_reportes(id_admin):
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # JOIN mágico: Traemos reportes que coincidan con la ruta del admin
            sql = """
                SELECT r.* FROM reportes r
                JOIN administrador_ruta ar ON r.id_ruta = ar.id_ruta
                WHERE ar.id_administrador = %s AND r.estado = 'PENDIENTE'
            """
            cursor.execute(sql, (id_admin,))
            reportes = cursor.fetchall()
            return jsonify(reportes), 200
    finally:
        conexion.close()
@app.route('/api/sanciones', methods=['POST'])
def aplicar_sancion():
    datos = request.json
    
    # Usamos los nombres exactos definidos en tu Data Class de Kotlin
    id_unidad = datos.get('id_unidad') 
    id_admin = datos.get('id_administrador') # Cambiado de 'id_admin'
    motivo = datos.get('motivo')
    fecha_inicio = datos.get('fecha_inicio') # Nuevo
    fecha_fin = datos.get('fecha_fin')       # Nuevo

    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # Asegúrate de que tu tabla 'sanciones' tenga estas columnas
            sql = """INSERT INTO sanciones 
                     (id_unidad, id_administrador, motivo, fecha_inicio, fecha_fin, fecha) 
                     VALUES (%s, %s, %s, %s, %s, NOW())"""
            cursor.execute(sql, (id_unidad, id_admin, motivo, fecha_inicio, fecha_fin))
            conexion.commit()
            
        return jsonify({"mensaje": "Sanción aplicada exitosamente"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        conexion.close()
# =====================================================
# ARRANQUE DEL SERVIDOR
# =====================================================
if __name__ == '__main__':
# Cambia tu app.run a esto:

    app.run(host='0.0.0.0', port=5000, debug=True)
