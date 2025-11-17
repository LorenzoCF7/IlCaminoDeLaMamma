-- CREACIÓN DE BASE DE DATOS
DROP DATABASE IF EXISTS IlCaminoDeLaMamma;
CREATE DATABASE IlCaminoDeLaMamma;
USE IlCaminoDeLaMamma;

-- TABLA: ROL
CREATE TABLE Rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150)
);

-- TABLA: USUARIO
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50),
    correo VARCHAR(100) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- TABLA: MESA
CREATE TABLE Mesa (
    id_mesa INT AUTO_INCREMENT PRIMARY KEY,
    estado ENUM('libre', 'ocupada', 'reservada') NOT NULL DEFAULT 'libre'
);

-- TABLA: RECETA
CREATE TABLE Receta (
    id_receta INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(8,2) NOT NULL,
    tiempo_preparacion INT,
    categoria VARCHAR(50),
    disponible BOOLEAN DEFAULT TRUE
);

-- TABLA: INGREDIENTE
CREATE TABLE Ingrediente (
    id_ingrediente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    unidad_medida VARCHAR(20),
    cantidad_stock DECIMAL(10,2) DEFAULT 0
);

-- TABLA INTERMEDIA: RECETA_INGREDIENTE
CREATE TABLE Receta_Ingrediente (
    id_receta INT NOT NULL,
    id_ingrediente INT NOT NULL,
    cantidad_usada DECIMAL(8,2) NOT NULL,
    PRIMARY KEY (id_receta, id_ingrediente),
    FOREIGN KEY (id_receta) REFERENCES Receta(id_receta)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (id_ingrediente) REFERENCES Ingrediente(id_ingrediente)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- TABLA: COMANDA
CREATE TABLE Comanda (
    id_comanda INT AUTO_INCREMENT PRIMARY KEY,
    id_mesa INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(8,2) DEFAULT 0,
    FOREIGN KEY (id_mesa) REFERENCES Mesa(id_mesa)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- TABLA: DETALLE_COMANDA
CREATE TABLE Detalle_Comanda (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda INT NOT NULL,
    id_receta INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(8,2) NOT NULL,
    subtotal DECIMAL(8,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
    FOREIGN KEY (id_comanda) REFERENCES Comanda(id_comanda)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (id_receta) REFERENCES Receta(id_receta)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- INSERCIÓN DE DATOS PREDETERMINADOS

-- ---- ROLES ----
INSERT INTO Rol (nombre_rol, descripcion) VALUES
('Administrador', 'Acceso total al sistema'),
('Cocinero', 'Gestiona recetas e ingredientes'),
('Camarero', 'Gestiona comandas y mesas');

-- ---- USUARIOS ----
INSERT INTO Usuario (nombre, apellido, correo, contraseña, id_rol) VALUES
('Mario', 'Sanchez', 'mario@camino.com', 'admin123', 1),
('Lorenzo', 'Cruz', 'lorenzo@camino.com', 'cocinero123', 2),
('Luis', 'Capel', 'luis@camino.com', 'camarero1', 3),
('Marco', 'Cardo', 'marco@camino.com', 'camarero2', 3);

-- ---- MESAS ----
INSERT INTO Mesa (estado) VALUES
('libre'),
('ocupada'),
('reservada'),
('libre'),
('ocupada'),
('libre');

-- ---- INGREDIENTES ----
INSERT INTO Ingrediente (nombre, unidad_medida, cantidad_stock) VALUES
('Pasta', 'g', 8000),
('Tomate', 'ml', 5000),
('Queso parmesano', 'g', 2000),
('Aceite de oliva', 'ml', 2500),
('Ajo', 'dientes', 300),
('Albahaca', 'hojas', 400),
('Pan', 'rebanadas', 100),
('Carne de res', 'g', 3000),
('Pollo', 'g', 2500),
('Harina', 'g', 4000),
('Azúcar', 'g', 3000),
('Huevos', 'unidades', 100),
('Leche', 'ml', 5000),
('Cacao', 'g', 800),
('Café', 'ml', 4000),
('Vino tinto', 'ml', 2000);

-- ---- RECETAS ----
-- Entrantes
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Bruschetta al pomodoro', 'Pan tostado con tomate, ajo y aceite de oliva.', 5.00, 10, 'Entrante'),
('Ensalada caprese', 'Tomate, mozzarella y albahaca con aceite de oliva.', 6.50, 10, 'Entrante');

-- Pastas
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Spaghetti al pomodoro', 'Pasta italiana con salsa de tomate natural.', 9.50, 20, 'Pasta'),
('Penne al pesto', 'Pasta con salsa de albahaca, piñones y parmesano.', 10.00, 25, 'Pasta'),
('Lasagna alla bolognese', 'Láminas de pasta con carne y bechamel.', 12.00, 35, 'Pasta');

-- Segundos platos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Pollo alla cacciatora', 'Pollo guisado con vino tinto y verduras.', 13.00, 40, 'Segundo'),
('Bistecca alla fiorentina', 'Corte de carne de res a la parrilla.', 18.00, 30, 'Segundo');

-- Postres
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Tiramisù', 'Postre italiano con café, cacao y mascarpone.', 6.00, 15, 'Postre'),
('Panna cotta', 'Postre de nata con salsa de frutas.', 5.50, 20, 'Postre');

-- Bebidas
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Vino tinto', 'Copa de vino tinto italiano.', 3.50, 1, 'Bebida'),
('Café espresso', 'Café italiano fuerte y corto.', 2.00, 2, 'Bebida');

-- ---- RECETA_INGREDIENTE ----
-- ENTRANTES
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Bruschetta al pomodoro', 'Pan tostado con tomate, ajo, aceite de oliva y albahaca fresca.', 5.00, 10, 'Entrante'),
('Ensalada caprese', 'Rodajas de tomate, mozzarella y albahaca con aceite de oliva virgen extra.', 6.50, 10, 'Entrante'),
('Carpaccio di manzo', 'Finísimas láminas de ternera con parmesano, rúcula y limón.', 8.50, 15, 'Entrante'),
('Antipasto misto', 'Selección italiana de embutidos, quesos y verduras marinadas.', 9.00, 15, 'Entrante');

-- CARNES
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Pollo alla cacciatora', 'Pollo guisado con vino tinto, tomate, cebolla y hierbas.', 13.00, 40, 'Carne'),
('Saltimbocca alla romana', 'Ternera con jamón y salvia en salsa de vino blanco.', 16.50, 35, 'Carne'),
('Scaloppine al limone', 'Finas láminas de ternera en salsa de limón y mantequilla.', 15.00, 25, 'Carne'),
('Filetto al pepe verde', 'Solomillo de ternera con salsa cremosa de pimienta verde.', 18.50, 35, 'Carne');

-- PESCADOS
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Branzino al forno', 'Lubina al horno con limón, vino blanco y hierbas aromáticas.', 17.00, 35, 'Pescado'),
('Salmone alla griglia', 'Salmón a la parrilla con aceite de oliva y verduras asadas.', 16.50, 25, 'Pescado'),
('Calamari ripieni', 'Calamares rellenos de pan rallado, ajo y perejil.', 15.50, 30, 'Pescado'),
('Fritto misto di mare', 'Selección de mariscos y pescado frito al estilo italiano.', 18.00, 20, 'Pescado');

-- PASTAS
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Spaghetti alla carbonara', 'Pasta con salsa de huevo, guanciale, queso pecorino y pimienta.', 11.00, 20, 'Pasta'),
('Penne al pesto', 'Pasta corta con salsa de albahaca, piñones y parmesano.', 10.00, 25, 'Pasta'),
('Lasagna alla bolognese', 'Láminas de pasta con carne, tomate y bechamel gratinada.', 12.50, 35, 'Pasta'),
('Ravioli ai funghi', 'Raviolis rellenos de setas con salsa cremosa de trufa.', 13.00, 30, 'Pasta');

-- PIZZAS
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Pizza Margherita', 'Tomate, mozzarella y albahaca fresca.', 8.50, 15, 'Pizza'),
('Pizza Diavola', 'Tomate, mozzarella y salami picante.', 9.50, 15, 'Pizza'),
('Pizza Quattro Formaggi', 'Mozzarella, gorgonzola, parmesano y fontina.', 10.50, 15, 'Pizza'),
('Pizza Prosciutto e Funghi', 'Jamón cocido, champiñones y mozzarella.', 9.80, 15, 'Pizza');

-- MENÚ INFANTIL
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Mini pizza margherita', 'Pizza pequeña con tomate y mozzarella.', 6.00, 10, 'Menú infantil'),
('Spaghetti al pomodoro', 'Pasta con salsa de tomate suave y parmesano rallado.', 6.50, 10, 'Menú infantil'),
('Nuggets con patatas', 'Porción de pollo empanado con patatas fritas.', 7.00, 10, 'Menú infantil'),
('Mini lasagna', 'Porción infantil de lasaña boloñesa casera.', 7.50, 15, 'Menú infantil');

-- POSTRES
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Tiramisù', 'Clásico italiano con café, mascarpone y cacao.', 6.00, 15, 'Postre'),
('Panna cotta', 'Postre de nata cocida con coulis de frutas del bosque.', 5.50, 20, 'Postre'),
('Cannoli siciliani', 'Dulce crujiente relleno de ricotta y frutas confitadas.', 6.50, 25, 'Postre'),
('Gelato artigianale', 'Helado artesanal de vainilla o pistacho.', 4.50, 10, 'Postre');

-- BEBIDAS
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Vino tinto della casa', 'Copa de vino tinto italiano de la casa.', 3.50, 1, 'Bebida'),
('Cerveza italiana', 'Cerveza artesanal italiana, 33 cl.', 3.00, 1, 'Bebida'),
('Café espresso', 'Café italiano intenso y aromático.', 2.00, 2, 'Bebida'),
('Agua mineral', 'Botella de agua sin gas 50 cl.', 1.50, 1, 'Bebida');

-- ---- COMANDAS ----
INSERT INTO Comanda (id_mesa, id_usuario, total) VALUES
(2, 3, 24.50),
(5, 4, 20.00);

-- ---- DETALLE_COMANDA ----
INSERT INTO Detalle_Comanda (id_comanda, id_receta, cantidad, precio_unitario) VALUES
(1, 3, 1, 9.50),
(1, 1, 1, 5.00),
(1, 11, 1, 2.00),
(2, 5, 1, 12.00),
(2, 9, 1, 5.50),
(2, 10, 1, 3.50);

-- =============================================
-- CONSULTAS DE VERIFICACIÓN
-- =============================================
SELECT 'ROLES' AS Tabla; SELECT * FROM Rol;
SELECT 'USUARIOS' AS Tabla; SELECT * FROM Usuario;
SELECT 'MESAS' AS Tabla; SELECT * FROM Mesa;
SELECT 'RECETAS' AS Tabla; SELECT * FROM Receta;
SELECT 'INGREDIENTES' AS Tabla; SELECT * FROM Ingrediente;
SELECT 'COMANDAS' AS Tabla; SELECT * FROM Comanda;
SELECT 'DETALLES' AS Tabla; SELECT * FROM Detalle_Comanda;