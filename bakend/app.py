from flask import Flask, jsonify, request
from flask_cors import CORS
import pymysql
import random          
from datetime import datetime  

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
            sql = "SELECT id_usuario, nombre FROM usuario WHERE correo = %s AND password = %s"
            cursor.execute(sql, (correo, password))
            usuario = cursor.fetchone()

            if usuario:
                id_user = usuario['id_usuario']
                
                # Buscamos si es Administrador o Chofer
                cursor.execute("SELECT id_administrador FROM administrador WHERE id_usuario = %s", (id_user,))
                admin = cursor.fetchone()
                
                if admin:
                    return jsonify({"id_usuario": id_user, "rol": "ADMIN"}), 200
                
                cursor.execute("SELECT id_chofer FROM chofer WHERE id_usuario = %s", (id_user,))
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
                    CONCAT('ruta ', r.id_ruta) AS codigo,
                    r.nombre AS nombre,
                    er.nombre AS estado
                FROM Ruta r
                INNER JOIN estado_ruta er ON r.id_estado_ruta = er.id_estado_ruta
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
            sql = "UPDATE ruta SET id_estado_ruta = %s WHERE id_ruta = %s"
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
            cursor.execute("SELECT nombre FROM zona")
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
                    CONCAT('ruta ', r.id_ruta) AS codigo,
                    r.nombre AS nombre,
                    er.nombre AS estado
                FROM ruta r
                INNER JOIN zona z ON r.id_zona = z.id_zona
                INNER JOIN estado_ruta er ON r.id_estado_ruta = er.id_estado_ruta
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
            cursor.execute("INSERT INTO zona (nombre) VALUES (%s)", (nombre,))
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
            cursor.execute("SELECT id_zona FROM zona WHERE nombre = %s", (nombre_zona,))
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
            sql = "INSERT INTO parada (nombre_parada, id_ruta) VALUES (%s, %s)"
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
@app.route('/api/reportes', methods=['POST'])
def crear_reporte():
    conexion = None
    try:
        datos = request.get_json()
        id_pasajero = datos.get('id_pasajero')
        id_unidad = datos.get('id_unidad')
        id_tipo_reporte = datos.get('id_tipo_reporte')
        descripcion = datos.get('descripcion')

        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql = """
                INSERT INTO reporte (id_pasajero, id_unidad, id_tipo_reporte, fecha_reporte, descripcion)
                VALUES (%s, %s, %s, NOW(), %s)
            """
            cursor.execute(sql, (id_pasajero, id_unidad, id_tipo_reporte, descripcion))
            conexion.commit()
        return jsonify({"mensaje": "Reporte registrado y enviado al administrador"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

@app.route('/api/reportes/pendientes', methods=['GET'])
def ver_reportes():
    conexion = None
    try:
        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql = """
                SELECT 
                    r.id_reporte AS id,
                    CONCAT(u.nombre, ' ', u.apellido_paterno) AS chofer,
                    uni.numero_economico AS unidad,
                    r.descripcion AS motivo
                FROM Reporte r
                INNER JOIN unidad uni ON r.id_unidad = uni.id_unidad
                INNER JOIN chofer_unidad cu ON uni.id_unidad = cu.id_unidad
                INNER JOIN chofer ch ON cu.id_chofer = ch.id_chofer
                INNER JOIN usuario u ON ch.id_usuario = u.id_usuario
            """
            cursor.execute(sql)
            reportes = cursor.fetchall()
        return jsonify(reportes), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

@app.route('/api/sanciones', methods=['POST'])
def aplicar_sancion():
    conexion = None
    try:
        datos = request.get_json()
        id_unidad = datos.get('id_unidad')
        id_administrador = datos.get('id_administrador')
        motivo = datos.get('motivo')

        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql = """
                INSERT INTO sancion (id_unidad, id_administrador, motivo, fecha_inicio, fecha_fin)
                VALUES (%s, %s, %s, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY))
            """
            cursor.execute(sql, (id_unidad, id_administrador, motivo))
            conexion.commit()
        return jsonify({"mensaje": "Sanción registrada correctamente"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion:
            conexion.close()

@app.route('/api/cambios-ruta', methods=['POST'])
def registrar_cambio_ruta():
    conexion = None
    try:
        datos = request.get_json()
        id_ruta = datos.get('id_ruta')
        descripcion = datos.get('descripcion')

        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            sql_cambio = "INSERT INTO cambio_ruta (id_ruta, fecha_inicio, descripcion) VALUES (%s, CURDATE(), %s)"
            cursor.execute(sql_cambio, (id_ruta, descripcion))
            
            sql_notif = "INSERT INTO notificacion (titulo, fecha_mensaje, id_ruta) VALUES (%s, NOW(), %s)"
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
        nombre = datos.get('nombre')
        apellido_p = datos.get('apellido_paterno')
        apellido_m = datos.get('apellido_materno')
        correo = datos.get('correo')
        telefono = datos.get('telefono')
        password = datos.get('password')
        rol = datos.get('rol')
        token_enviado = datos.get('token_acceso')

        if not all([nombre, correo, password, rol]):
            return jsonify({"error": "Faltan campos obligatorios"}), 400

        rol_normalizado = rol.upper().strip()
        conexion = conectar_bd()
        
        with conexion.cursor() as cursor:
            # 1. Validación de Token solo para Admin/Chofer
            if rol_normalizado in ["ADMIN", "ADMINISTRADOR", "CHOFER", "CONDUCTOR"]:
                if not token_enviado:
                    return jsonify({"error": "Se requiere token para este rol"}), 400
                
                rol_buscar = "ADMIN" if rol_normalizado in ["ADMIN", "ADMINISTRADOR"] else "CHOFER"
                sql_token = "SELECT id FROM tokens_registro WHERE token_autorizacion = %s AND rol_destino = %s AND usado = 0"
                cursor.execute(sql_token, (token_enviado.strip(), rol_buscar))
                if not cursor.fetchone():
                    return jsonify({"error": "Token inválido o ya utilizado"}), 400

            # 2. Insertar Usuario base
            sql_usuario = "INSERT INTO usuario (nombre, apellido_paterno, apellido_materno, correo, telefono, password) VALUES (%s, %s, %s, %s, %s, %s)"
            cursor.execute(sql_usuario, (nombre, apellido_p, apellido_m, correo, telefono, password))
            id_usuario_creado = cursor.lastrowid

            # 3. Lógica según rol
            folio_asignado = None
            
            if rol_normalizado == "PASAJERO":
                cursor.execute("INSERT INTO pasajero (id_usuario) VALUES (%s)", (id_usuario_creado,))
            
            elif rol_normalizado in ["CONDUCTOR", "CHOFER"]:
                cursor.execute("INSERT INTO chofer (id_usuario) VALUES (%s)", (id_usuario_creado,))
                cursor.execute("UPDATE tokens_registro SET usado = 1 WHERE token_autorizacion = %s", (token_enviado.strip(),))
            
            elif rol_normalizado in ["ADMIN", "ADMINISTRADOR"]:
                folio_asignado = f"ADM-{random.randint(1000, 9999)}"
                cursor.execute("INSERT INTO administrador (id_usuario, folio_unico) VALUES (%s, %s)", (id_usuario_creado, folio_asignado))
                cursor.execute("UPDATE usuario SET folio_unico = %s WHERE id_usuario = %s", (folio_asignado, id_usuario_creado))
                cursor.execute("UPDATE tokens_registro SET usado = 1 WHERE token_autorizacion = %s", (token_enviado.strip(),))

            conexion.commit()
            
            # 4. Respuesta inteligente
            respuesta = {"mensaje": "Registro exitoso"}
            if folio_asignado:
                respuesta["folio_asignado"] = folio_asignado
                
            return jsonify(respuesta), 201

    except Exception as e:
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
            sql_unidad = "UPDATE unidad SET numero_economico = %s, placas = %s WHERE id_chofer = %s"
            cursor.execute(sql_unidad, (data['numero_economico'], data['placas'], data['id_chofer']))
            
            # 2. Actualizar ruta (en la tabla de Asignación)
            sql_ruta = "UPDATE asignacion_ruta SET id_ruta = %s WHERE id_chofer = %s"
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
                JOIN unidad u ON cu.id_unidad = u.id_unidad
                JOIN asignacion_ruta ar ON cu.id_chofer = ar.id_chofer
                JOIN ruta r ON ar.id_ruta = r.id_ruta
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
                JOIN asignacion_ruta ar ON c.id_asignacion = ar.id_asignacion
                JOIN ruta r ON ar.id_ruta = r.id_ruta
                JOIN chofer ch ON ar.id_chofer = ch.id_chofer
                JOIN usuario us ON ch.id_usuario = us.id_usuario
                JOIN chofer_Unidad cu ON ch.id_chofer = cu.id_chofer
                JOIN unidad u ON cu.id_unidad = u.id_unidad
                JOIN zona z ON r.id_zona = z.id_zona
                WHERE z.nombre = %s AND c.hora_llegada IS NULL
            """
            cursor.execute(sql, (zona.strip(),))
            resultados = cursor.fetchall()
            
            # Log de depuración para la consola de Flask
            print(f"DEBUG: Viajes activos para {zona}: {len(resultados)} encontrados.")
            
            return jsonify(resultados), 200
    except Exception as e:
        print(f"ERROR crítico en viajes_activos: {e}")
        return jsonify({"error": "Error interno al consultar viajes"}), 500
    finally:
        if conexion:
            conexion.close()
# 2. Registrar Salida y Llegada (Tus botones de inicio y fin de jornada)
@app.route('/api/control_operativo/salida', methods=['POST'])
def registrar_salida():
    data = request.json
    # Imprime los datos para verificar qué llega realmente
    print(f"DEBUG: Datos recibidos en Flask: {data}")
    
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            # Asegúrate de que los nombres de columna coincidan con los de tu tabla (la que mostraste en la imagen)
            # Tu imagen muestra: id_control, id_ruta, hora_salida, hora_llegada, id_asignacion, fecha
            sql = """INSERT INTO control_operativo (id_asignacion, id_ruta, hora_salida, fecha) 
                     VALUES (%s, %s, %s, CURDATE())"""
            
            # Usamos .get para evitar que el programa se caiga si falta algún campo
            cursor.execute(sql, (
                data.get('id_asignacion'),
                data.get('id_ruta'),
                data.get('hora_salida')
            ))
            conexion.commit()
        return jsonify({"mensaje": "Salida registrada exitosamente"}), 201
    except Exception as e:
        print(f"ERROR: {e}")
        return jsonify({"error": str(e)}), 500
    finally:
        conexion.close()
@app.route('/api/control_operativo/llegada', methods=['POST'])
def registrar_llegada():
    data = request.json
    id_control = data.get('id_control')
    
    if not id_control:
        return jsonify({"error": "Falta el ID del control"}), 400
        
    conexion = conectar_bd()
    try:
        with conexion.cursor() as cursor:
            sql = "UPDATE control_operativo SET hora_llegada = NOW(), estado_viaje = 'FINALIZADO' WHERE id_control = %s"
            filas_afectadas = cursor.execute(sql, (id_control,))
            conexion.commit()
            
            if filas_afectadas == 0:
                return jsonify({"error": "No se encontró un viaje activo con ese ID"}), 404
                
        return jsonify({"mensaje": "Llegada registrada exitosamente"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
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
                cursor.execute("INSERT INTO unidad (placa, numero_economico) VALUES (%s, %s)", (data.get('placas', 'SIN-PLACAS'), data['numero_economico']))
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
                FROM asignacion_ruta ar
                JOIN chofer ch ON ar.id_chofer = ch.id_chofer
                JOIN usuario us ON ch.id_usuario = us.id_usuario
                JOIN chofer_unidad cu ON ch.id_chofer = cu.id_chofer
                JOIN unidad u ON cu.id_unidad = u.id_unidad
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
            sql = "INSERT INTO asignacion_ruta (id_chofer, id_ruta, fecha_asignacion) VALUES (%s, %s, CURDATE())"
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
    conexion = None
    try:
        datos = request.get_json()
        folio_admin = datos.get('folio_solicitante') # Este viene de Android
        rol_destino = datos.get('rol_destino')        

        if not folio_admin or not rol_destino:
            return jsonify({"error": "Faltan datos obligatorios"}), 400

        conexion = conectar_bd()
        with conexion.cursor() as cursor:
            # CORRECCIÓN 1: Usamos 'folio_unico' porque así se llama en tu tabla
            sql_buscar = "SELECT id_administrador FROM administrador WHERE folio_unico = %s"
            cursor.execute(sql_buscar, (folio_admin,))
            resultado = cursor.fetchone()

            if not resultado:
                return jsonify({"error": "Folio de administrador inválido"}), 401
            
            id_admin_real = resultado['id_administrador']

            # 2. GENERAR TOKEN
            nuevo_token = f"TK-{random.randint(1000, 9999)}"

            # CORRECCIÓN 2: Eliminamos 'folio_solicitante' porque NO existe en tu tabla 'tokens_registro'
            sql_insertar = """
                INSERT INTO tokens_registro (id_administrador, rol_destino, token_autorizacion, usado)
                VALUES (%s, %s, %s, 0)
            """
            cursor.execute(sql_insertar, (id_admin_real, rol_destino.upper().strip(), nuevo_token))
            conexion.commit()

        return jsonify({
            "mensaje": "Token generado con éxito",
            "token_autorizacion": nuevo_token
        }), 201

    except Exception as e:
        print(f"Error al generar token: {e}")
        return jsonify({"error": str(e)}), 500
    finally:
        if conexion: conexion.close()
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


# ===================
# ARRANQUE DEL SERVIDOR
# =====================================================
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
