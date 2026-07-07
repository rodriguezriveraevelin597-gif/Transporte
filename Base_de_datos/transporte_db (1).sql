-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 06-07-2026 a las 17:29:05
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `transporte_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `administrador`
--

CREATE TABLE `administrador` (
  `id_administrador` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `folio_unico` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `administrador`
--

INSERT INTO `administrador` (`id_administrador`, `id_usuario`, `folio_unico`) VALUES
(9, 13, 'ADM-2412');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `administrador_ruta`
--

CREATE TABLE `administrador_ruta` (
  `id_administrador_ruta` int(11) NOT NULL,
  `id_ruta` int(11) DEFAULT NULL,
  `id_administrador` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignacion_ruta`
--

CREATE TABLE `asignacion_ruta` (
  `id_asignacion` int(11) NOT NULL,
  `id_chofer` int(11) DEFAULT NULL,
  `id_ruta` int(11) DEFAULT NULL,
  `fecha_asignacion` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `asignacion_ruta`
--

INSERT INTO `asignacion_ruta` (`id_asignacion`, `id_chofer`, `id_ruta`, `fecha_asignacion`) VALUES
(1, 1, 3, '2026-07-02'),
(2, 1, 4, '2026-07-02'),
(3, 2, 5, '2026-07-02'),
(4, 2, 3, '2026-07-02'),
(5, 2, 4, '2026-07-02'),
(6, 2, 5, '2026-07-02');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cambio_ruta`
--

CREATE TABLE `cambio_ruta` (
  `id_cambio` int(11) NOT NULL,
  `id_ruta` int(11) DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chofer`
--

CREATE TABLE `chofer` (
  `id_chofer` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `chofer`
--

INSERT INTO `chofer` (`id_chofer`, `id_usuario`) VALUES
(1, 4),
(2, 14);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chofer_unidad`
--

CREATE TABLE `chofer_unidad` (
  `id_chofer_unidad` int(11) NOT NULL,
  `id_chofer` int(11) DEFAULT NULL,
  `id_unidad` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `chofer_unidad`
--

INSERT INTO `chofer_unidad` (`id_chofer_unidad`, `id_chofer`, `id_unidad`) VALUES
(16, 1, 8),
(20, 2, 12);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `control_operativo`
--

CREATE TABLE `control_operativo` (
  `id_control` int(11) NOT NULL,
  `id_ruta` int(11) NOT NULL,
  `hora_salida` time DEFAULT NULL,
  `hora_llegada` datetime DEFAULT NULL,
  `id_asignacion` int(11) NOT NULL,
  `fecha` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `control_operativo`
--

INSERT INTO `control_operativo` (`id_control`, `id_ruta`, `hora_salida`, `hora_llegada`, `id_asignacion`, `fecha`) VALUES
(2, 3, '00:00:00', NULL, 4, NULL),
(4, 3, '00:00:00', NULL, 1, '2026-07-03'),
(5, 3, '00:00:00', NULL, 1, '2026-07-03'),
(6, 3, '00:00:00', NULL, 1, '2026-07-03'),
(7, 3, '06:45:00', NULL, 1, '2026-07-03'),
(8, 3, '09:35:00', NULL, 1, '2026-07-03');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado_ruta`
--

CREATE TABLE `estado_ruta` (
  `id_estado_ruta` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `estado_ruta`
--

INSERT INTO `estado_ruta` (`id_estado_ruta`, `nombre`) VALUES
(1, 'Activa'),
(2, 'Inactiva');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificacion`
--

CREATE TABLE `notificacion` (
  `id_notificacion` int(11) NOT NULL,
  `titulo` varchar(200) DEFAULT NULL,
  `fecha_mensaje` datetime DEFAULT NULL,
  `id_ruta` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `parada`
--

CREATE TABLE `parada` (
  `id_parada` int(11) NOT NULL,
  `nombre_parada` varchar(150) DEFAULT NULL,
  `id_ruta` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `parada`
--

INSERT INTO `parada` (`id_parada`, `nombre_parada`, `id_ruta`) VALUES
(1, 'calle claveles', 3),
(2, 'los reyes', NULL),
(3, 'nunca me voy', NULL),
(4, 'uno', NULL),
(5, 'dos ', 3),
(6, 'yoo', 6);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pasajero`
--

CREATE TABLE `pasajero` (
  `id_pasajero` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pasajero`
--

INSERT INTO `pasajero` (`id_pasajero`, `id_usuario`) VALUES
(1, 2),
(2, 3),
(3, 11);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reporte`
--

CREATE TABLE `reporte` (
  `id_reporte` int(11) NOT NULL,
  `id_pasajero` int(11) DEFAULT NULL,
  `id_unidad` int(11) DEFAULT NULL,
  `id_tipo_reporte` int(11) DEFAULT NULL,
  `fecha_reporte` datetime DEFAULT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ruta`
--

CREATE TABLE `ruta` (
  `id_ruta` int(11) NOT NULL,
  `nombre` varchar(150) DEFAULT NULL,
  `id_estado_ruta` int(11) DEFAULT NULL,
  `id_zona` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ruta`
--

INSERT INTO `ruta` (`id_ruta`, `nombre`, `id_estado_ruta`, `id_zona`) VALUES
(3, '34:Chocolin-piedras', 1, 1),
(4, '56:Chimalhuacán-palacio', 1, 4),
(5, '45:Gómez farias- flores', 2, 5),
(6, '9:Reyes-La paz', 1, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sancion`
--

CREATE TABLE `sancion` (
  `id_sancion` int(11) NOT NULL,
  `id_unidad` int(11) DEFAULT NULL,
  `id_administrador` int(11) DEFAULT NULL,
  `motivo` text DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_reporte`
--

CREATE TABLE `tipo_reporte` (
  `id_tipo_reporte` int(11) NOT NULL,
  `nombre` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tokens_registro`
--

CREATE TABLE `tokens_registro` (
  `id` int(11) NOT NULL,
  `rol_destino` varchar(20) NOT NULL,
  `token_autorizacion` varchar(100) NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `usado` tinyint(1) DEFAULT 0,
  `id_administrador` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tokens_registro`
--

INSERT INTO `tokens_registro` (`id`, `rol_destino`, `token_autorizacion`, `fecha_creacion`, `usado`, `id_administrador`) VALUES
(2, 'ADMIN', 'TK-3390', '2026-06-30 01:56:24', 1, 1),
(3, 'CHOFER', 'TK-2948', '2026-06-30 23:07:14', 0, 1),
(4, 'ADMIN', 'TK-1717', '2026-06-30 23:13:18', 1, 1),
(5, 'ADMIN', 'TK-8437', '2026-07-01 14:44:51', 1, 1),
(6, 'ADMIN', 'TK-6487', '2026-07-01 14:51:18', 1, 1),
(7, 'ADMIN', 'TK-1485', '2026-07-01 14:51:21', 0, 1),
(8, 'CHOFER', 'TK-1187', '2026-07-02 09:09:44', 0, 1),
(9, 'CHOFER', 'TK-1193', '2026-07-03 01:03:20', 1, 1),
(10, 'CHOFER', 'TK-7854', '2026-07-03 03:11:07', 0, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `trayecto`
--

CREATE TABLE `trayecto` (
  `id_trayecto` int(11) NOT NULL,
  `id_ruta` int(11) DEFAULT NULL,
  `id_parada_inicio` int(11) DEFAULT NULL,
  `id_parada_fin` int(11) DEFAULT NULL,
  `costo` decimal(10,2) DEFAULT NULL,
  `tiempo_estimado` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `unidad`
--

CREATE TABLE `unidad` (
  `id_unidad` int(11) NOT NULL,
  `placa` varchar(30) DEFAULT NULL,
  `numero_economico` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `unidad`
--

INSERT INTO `unidad` (`id_unidad`, `placa`, `numero_economico`) VALUES
(2, 'tyu-56', '67'),
(3, 'werr', 'qyyeu'),
(4, 'gjju', '56'),
(5, '', ''),
(6, 'trex-tym', '1000'),
(7, 'pñn-678', '456'),
(8, '45', 'tyu'),
(9, 'tui-6882', '678'),
(10, 'yurb', '568'),
(11, 'rtet', '234'),
(12, 'gdhdhh', 'gsjdj');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `apellido_paterno` varchar(100) DEFAULT NULL,
  `apellido_materno` varchar(100) DEFAULT NULL,
  `correo` varchar(150) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `folio_unico` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `apellido_paterno`, `apellido_materno`, `correo`, `telefono`, `password`, `folio_unico`) VALUES
(1, 'eve', 'rod', 'riv', 'rore@gmail.com', '1234567823', 'qwer', 'abc-1234'),
(2, 'vero', 'dias', 'perez', 'vere@gmail.com', '1234896430', '1234', NULL),
(3, 'NombreUsuario', 'Paterno', 'Materno', 'correo@correo.com', '1234567890', 'password123', NULL),
(4, 'sara', 'rodriguez', 'maldonado', 'maldonado@gmail.com', '5668531651', '1234', NULL),
(9, 'yolanda', 'perez', 'sanchez', 'cool@gmail.com', '5660521478', '1234', 'ADM-1939'),
(10, 'evelin', 'roc', 'ric', 'rodriguezriveraevelin847@gmail.com', '1234567893', 'qwer', 'ADM-6950'),
(11, 'boris', 'casto', 'ocampo', 'eseboris@gmail.com', '1234567890', 'qwer', NULL),
(12, 'felipe', 'cano', 'alfaro', 'akd@gmail.con', '1234567876', 'qwer', 'ADM-2167'),
(13, 'Evelin', 'Rodriguez', 'Rivera', 'Evelin@gmail.com', '84964328055', 'qwer', 'ADM-2412'),
(14, 'jaqui', 'diaz', 'solis', 'solis@gmail.com', '5626048751', '1234', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_notificacion`
--

CREATE TABLE `usuario_notificacion` (
  `id_usuario_notificacion` int(11) NOT NULL,
  `id_notificacion` int(11) DEFAULT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `leido` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `zona`
--

CREATE TABLE `zona` (
  `id_zona` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `zona`
--

INSERT INTO `zona` (`id_zona`, `nombre`) VALUES
(1, 'el centro '),
(2, 'san Pedro '),
(3, 'piedras'),
(4, 'la paz'),
(5, 'san pablo\r\n'),
(6, 'santa rosa'),
(7, 'ladera'),
(8, 'san isidro');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `administrador`
--
ALTER TABLE `administrador`
  ADD PRIMARY KEY (`id_administrador`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `administrador_ruta`
--
ALTER TABLE `administrador_ruta`
  ADD PRIMARY KEY (`id_administrador_ruta`),
  ADD KEY `id_ruta` (`id_ruta`),
  ADD KEY `id_administrador` (`id_administrador`);

--
-- Indices de la tabla `asignacion_ruta`
--
ALTER TABLE `asignacion_ruta`
  ADD PRIMARY KEY (`id_asignacion`),
  ADD KEY `id_chofer` (`id_chofer`),
  ADD KEY `id_ruta` (`id_ruta`);

--
-- Indices de la tabla `cambio_ruta`
--
ALTER TABLE `cambio_ruta`
  ADD PRIMARY KEY (`id_cambio`),
  ADD KEY `id_ruta` (`id_ruta`);

--
-- Indices de la tabla `chofer`
--
ALTER TABLE `chofer`
  ADD PRIMARY KEY (`id_chofer`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `chofer_unidad`
--
ALTER TABLE `chofer_unidad`
  ADD PRIMARY KEY (`id_chofer_unidad`),
  ADD KEY `id_chofer` (`id_chofer`),
  ADD KEY `id_unidad` (`id_unidad`);

--
-- Indices de la tabla `control_operativo`
--
ALTER TABLE `control_operativo`
  ADD PRIMARY KEY (`id_control`),
  ADD KEY `id_ruta` (`id_ruta`),
  ADD KEY `fk_asignacion_ruta` (`id_asignacion`);

--
-- Indices de la tabla `estado_ruta`
--
ALTER TABLE `estado_ruta`
  ADD PRIMARY KEY (`id_estado_ruta`);

--
-- Indices de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD PRIMARY KEY (`id_notificacion`),
  ADD KEY `id_ruta` (`id_ruta`);

--
-- Indices de la tabla `parada`
--
ALTER TABLE `parada`
  ADD PRIMARY KEY (`id_parada`),
  ADD KEY `fk_parada_ruta` (`id_ruta`);

--
-- Indices de la tabla `pasajero`
--
ALTER TABLE `pasajero`
  ADD PRIMARY KEY (`id_pasajero`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD PRIMARY KEY (`id_reporte`),
  ADD KEY `id_pasajero` (`id_pasajero`),
  ADD KEY `id_unidad` (`id_unidad`),
  ADD KEY `id_tipo_reporte` (`id_tipo_reporte`);

--
-- Indices de la tabla `ruta`
--
ALTER TABLE `ruta`
  ADD PRIMARY KEY (`id_ruta`),
  ADD KEY `id_estado_ruta` (`id_estado_ruta`),
  ADD KEY `id_zona` (`id_zona`);

--
-- Indices de la tabla `sancion`
--
ALTER TABLE `sancion`
  ADD PRIMARY KEY (`id_sancion`),
  ADD KEY `id_unidad` (`id_unidad`),
  ADD KEY `id_administrador` (`id_administrador`);

--
-- Indices de la tabla `tipo_reporte`
--
ALTER TABLE `tipo_reporte`
  ADD PRIMARY KEY (`id_tipo_reporte`);

--
-- Indices de la tabla `tokens_registro`
--
ALTER TABLE `tokens_registro`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `token_autorizacion` (`token_autorizacion`),
  ADD KEY `fk_token_administrador` (`id_administrador`);

--
-- Indices de la tabla `trayecto`
--
ALTER TABLE `trayecto`
  ADD PRIMARY KEY (`id_trayecto`),
  ADD KEY `id_ruta` (`id_ruta`),
  ADD KEY `id_parada_inicio` (`id_parada_inicio`),
  ADD KEY `id_parada_fin` (`id_parada_fin`);

--
-- Indices de la tabla `unidad`
--
ALTER TABLE `unidad`
  ADD PRIMARY KEY (`id_unidad`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`);

--
-- Indices de la tabla `usuario_notificacion`
--
ALTER TABLE `usuario_notificacion`
  ADD PRIMARY KEY (`id_usuario_notificacion`),
  ADD KEY `id_notificacion` (`id_notificacion`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `zona`
--
ALTER TABLE `zona`
  ADD PRIMARY KEY (`id_zona`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `administrador`
--
ALTER TABLE `administrador`
  MODIFY `id_administrador` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `administrador_ruta`
--
ALTER TABLE `administrador_ruta`
  MODIFY `id_administrador_ruta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `asignacion_ruta`
--
ALTER TABLE `asignacion_ruta`
  MODIFY `id_asignacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `cambio_ruta`
--
ALTER TABLE `cambio_ruta`
  MODIFY `id_cambio` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `chofer`
--
ALTER TABLE `chofer`
  MODIFY `id_chofer` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `chofer_unidad`
--
ALTER TABLE `chofer_unidad`
  MODIFY `id_chofer_unidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de la tabla `control_operativo`
--
ALTER TABLE `control_operativo`
  MODIFY `id_control` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `estado_ruta`
--
ALTER TABLE `estado_ruta`
  MODIFY `id_estado_ruta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  MODIFY `id_notificacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `parada`
--
ALTER TABLE `parada`
  MODIFY `id_parada` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `pasajero`
--
ALTER TABLE `pasajero`
  MODIFY `id_pasajero` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `reporte`
--
ALTER TABLE `reporte`
  MODIFY `id_reporte` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ruta`
--
ALTER TABLE `ruta`
  MODIFY `id_ruta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `sancion`
--
ALTER TABLE `sancion`
  MODIFY `id_sancion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tipo_reporte`
--
ALTER TABLE `tipo_reporte`
  MODIFY `id_tipo_reporte` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tokens_registro`
--
ALTER TABLE `tokens_registro`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `trayecto`
--
ALTER TABLE `trayecto`
  MODIFY `id_trayecto` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `unidad`
--
ALTER TABLE `unidad`
  MODIFY `id_unidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `usuario_notificacion`
--
ALTER TABLE `usuario_notificacion`
  MODIFY `id_usuario_notificacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `zona`
--
ALTER TABLE `zona`
  MODIFY `id_zona` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `administrador`
--
ALTER TABLE `administrador`
  ADD CONSTRAINT `administrador_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `administrador_ruta`
--
ALTER TABLE `administrador_ruta`
  ADD CONSTRAINT `administrador_ruta_ibfk_1` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`),
  ADD CONSTRAINT `administrador_ruta_ibfk_2` FOREIGN KEY (`id_administrador`) REFERENCES `administrador` (`id_administrador`);

--
-- Filtros para la tabla `asignacion_ruta`
--
ALTER TABLE `asignacion_ruta`
  ADD CONSTRAINT `asignacion_ruta_ibfk_1` FOREIGN KEY (`id_chofer`) REFERENCES `chofer` (`id_chofer`),
  ADD CONSTRAINT `asignacion_ruta_ibfk_2` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`);

--
-- Filtros para la tabla `cambio_ruta`
--
ALTER TABLE `cambio_ruta`
  ADD CONSTRAINT `cambio_ruta_ibfk_1` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`);

--
-- Filtros para la tabla `chofer`
--
ALTER TABLE `chofer`
  ADD CONSTRAINT `chofer_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `chofer_unidad`
--
ALTER TABLE `chofer_unidad`
  ADD CONSTRAINT `chofer_unidad_ibfk_1` FOREIGN KEY (`id_chofer`) REFERENCES `chofer` (`id_chofer`),
  ADD CONSTRAINT `chofer_unidad_ibfk_2` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`);

--
-- Filtros para la tabla `control_operativo`
--
ALTER TABLE `control_operativo`
  ADD CONSTRAINT `control_operativo_ibfk_1` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`),
  ADD CONSTRAINT `fk_asignacion_ruta` FOREIGN KEY (`id_asignacion`) REFERENCES `asignacion_ruta` (`id_asignacion`);

--
-- Filtros para la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD CONSTRAINT `notificacion_ibfk_1` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`);

--
-- Filtros para la tabla `parada`
--
ALTER TABLE `parada`
  ADD CONSTRAINT `fk_parada_ruta` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`);

--
-- Filtros para la tabla `pasajero`
--
ALTER TABLE `pasajero`
  ADD CONSTRAINT `pasajero_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `reporte`
--
ALTER TABLE `reporte`
  ADD CONSTRAINT `reporte_ibfk_1` FOREIGN KEY (`id_pasajero`) REFERENCES `pasajero` (`id_pasajero`),
  ADD CONSTRAINT `reporte_ibfk_2` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`),
  ADD CONSTRAINT `reporte_ibfk_3` FOREIGN KEY (`id_tipo_reporte`) REFERENCES `tipo_reporte` (`id_tipo_reporte`);

--
-- Filtros para la tabla `ruta`
--
ALTER TABLE `ruta`
  ADD CONSTRAINT `ruta_ibfk_1` FOREIGN KEY (`id_estado_ruta`) REFERENCES `estado_ruta` (`id_estado_ruta`),
  ADD CONSTRAINT `ruta_ibfk_2` FOREIGN KEY (`id_zona`) REFERENCES `zona` (`id_zona`);

--
-- Filtros para la tabla `sancion`
--
ALTER TABLE `sancion`
  ADD CONSTRAINT `sancion_ibfk_1` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`),
  ADD CONSTRAINT `sancion_ibfk_2` FOREIGN KEY (`id_administrador`) REFERENCES `administrador` (`id_administrador`);

--
-- Filtros para la tabla `tokens_registro`
--
ALTER TABLE `tokens_registro`
  ADD CONSTRAINT `fk_token_administrador` FOREIGN KEY (`id_administrador`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `trayecto`
--
ALTER TABLE `trayecto`
  ADD CONSTRAINT `trayecto_ibfk_1` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`),
  ADD CONSTRAINT `trayecto_ibfk_2` FOREIGN KEY (`id_parada_inicio`) REFERENCES `parada` (`id_parada`),
  ADD CONSTRAINT `trayecto_ibfk_3` FOREIGN KEY (`id_parada_fin`) REFERENCES `parada` (`id_parada`);

--
-- Filtros para la tabla `usuario_notificacion`
--
ALTER TABLE `usuario_notificacion`
  ADD CONSTRAINT `usuario_notificacion_ibfk_1` FOREIGN KEY (`id_notificacion`) REFERENCES `notificacion` (`id_notificacion`),
  ADD CONSTRAINT `usuario_notificacion_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
