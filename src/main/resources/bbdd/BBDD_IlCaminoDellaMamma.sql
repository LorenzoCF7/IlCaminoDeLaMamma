-- =============================================
-- BASE DE DATOS UNIFICADA: IL CAMINO DELLA MAMMA
-- Versión compatible con los modelos Java/Hibernate
-- =============================================

DROP DATABASE IF EXISTS IlCaminoDeLaMamma;
CREATE DATABASE IlCaminoDeLaMamma;
USE IlCaminoDeLaMamma;

-- =============================================
-- TABLAS PRINCIPALES
-- =============================================

-- TABLA: MESA
CREATE TABLE Mesa (
    id_mesa INT AUTO_INCREMENT PRIMARY KEY,
    estado ENUM('LIBRE', 'OCUPADA', 'RESERVADA') NOT NULL DEFAULT 'LIBRE'
);

-- TABLA: USUARIO (simplificada para coincidir con el modelo Java)
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'COCINERO', 'CAMARERO') NOT NULL DEFAULT 'CAMARERO'
);

-- TABLA: RECETA
CREATE TABLE Receta (
    id_receta INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio INT NOT NULL COMMENT 'Precio en céntimos',
    tiempo_preparacion INT,
    categoria VARCHAR(50),
    disponible BOOLEAN DEFAULT TRUE,
    imagen LONGBLOB,
    pasos TEXT
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
    FOREIGN KEY (id_receta) REFERENCES Receta(id_receta) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (id_ingrediente) REFERENCES Ingrediente(id_ingrediente) ON UPDATE CASCADE ON DELETE CASCADE
);

-- TABLA: COMANDA
CREATE TABLE Comanda (
    id_comanda INT AUTO_INCREMENT PRIMARY KEY,
    id_mesa INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) DEFAULT 0,
    estado_comanda ENUM('POR_HACER', 'EN_PROCESO', 'FINALIZADA') DEFAULT 'POR_HACER',
    FOREIGN KEY (id_mesa) REFERENCES Mesa(id_mesa) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- TABLA: DETALLE_COMANDA
CREATE TABLE Detalle_Comanda (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_comanda INT NOT NULL,
    id_receta INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario INT NOT NULL COMMENT 'Precio en céntimos',
    subtotal INT GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
    estado_plato ENUM('POR_HACER', 'EN_COCINA', 'PREPARADO') DEFAULT 'POR_HACER',
    FOREIGN KEY (id_comanda) REFERENCES Comanda(id_comanda) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (id_receta) REFERENCES Receta(id_receta) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- =============================================
-- DATOS INICIALES
-- =============================================

-- MESAS (6 mesas)
INSERT INTO Mesa (estado) VALUES
('LIBRE'),
('OCUPADA'),
('RESERVADA'),
('LIBRE'),
('OCUPADA'),
('LIBRE');

-- USUARIOS
INSERT INTO Usuario (nombre, contrasena, rol) VALUES
('Mario', 'admin123', 'ADMINISTRADOR'),
('Lorenzo', 'cocinero123', 'COCINERO'),
('Luis', 'camarero1', 'CAMARERO'),
('Marco', 'camarero2', 'CAMARERO');

-- INGREDIENTES
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

-- =============================================
-- RECETAS POR CATEGORÍA (Precios en céntimos)
-- =============================================

-- ENTRANTES (9 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Bruschetta clásica', 'Pan tostado con tomate, ajo y albahaca.', 650, 10, 'Entrante', TRUE),
('Ensalada caprese', 'Tomate, mozzarella fresca y albahaca.', 890, 8, 'Entrante', TRUE),
('Carpaccio de ternera', 'Láminas finas de ternera con rúcula y parmesano.', 1200, 12, 'Entrante', TRUE),
('Tabla de quesos italianos', 'Selección de quesos tradicionales italianos.', 1450, 5, 'Entrante', TRUE),
('Sopa minestrone', 'Sopa italiana de verduras con pasta.', 720, 35, 'Entrante', TRUE),
('Calamares fritos', 'Calamares rebozados y fritos.', 1180, 20, 'Entrante', TRUE),
('Provolone al horno', 'Queso provolone fundido con especias.', 950, 12, 'Entrante', TRUE),
('Tartar de salmón', 'Dados de salmón marinado con cítricos.', 1390, 15, 'Entrante', TRUE),
('Antipasto mixto', 'Variedad de embutidos, quesos y encurtidos italianos.', 1500, 10, 'Entrante', TRUE);

-- PASTA (9 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Spaghetti Carbonara', 'Spaghetti con salsa de huevo, queso y panceta.', 1290, 20, 'Pasta', TRUE),
('Penne Arrabbiata', 'Pasta corta con salsa de tomate picante.', 1150, 15, 'Pasta', TRUE),
('Tagliatelle al pesto', 'Pasta con salsa de albahaca, piñones y parmesano.', 1320, 18, 'Pasta', TRUE),
('Lasagna boloñesa', 'Láminas de pasta con carne y bechamel.', 1450, 45, 'Pasta', TRUE),
('Ravioli de ricotta y espinacas', 'Raviolis rellenos con queso ricotta y espinacas.', 1380, 25, 'Pasta', TRUE),
('Gnocchi a la sorrentina', 'Ñoquis de patata con salsa de tomate y mozzarella.', 1200, 30, 'Pasta', TRUE),
('Fettuccine Alfredo', 'Pasta con salsa cremosa de mantequilla y parmesano.', 1390, 20, 'Pasta', TRUE),
('Tortellini panna e prosciutto', 'Tortellini con nata y jamón.', 1400, 22, 'Pasta', TRUE),
('Spaghetti marinara', 'Spaghetti con mariscos en salsa de tomate.', 1530, 30, 'Pasta', TRUE);

-- PIZZA (9 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Margherita', 'Tomate, mozzarella y albahaca fresca.', 900, 15, 'Pizza', TRUE),
('Pepperoni', 'Tomate, mozzarella y salami picante.', 1150, 15, 'Pizza', TRUE),
('Cuatro quesos', 'Mozzarella, gorgonzola, parmesano y fontina.', 1250, 15, 'Pizza', TRUE),
('Hawaiana', 'Tomate, mozzarella, jamón y piña.', 1100, 15, 'Pizza', TRUE),
('BBQ Pollo', 'Pollo marinado en salsa BBQ con cebolla y mozzarella.', 1320, 18, 'Pizza', TRUE),
('Prosciutto e funghi', 'Jamón cocido, champiñones y mozzarella.', 1280, 15, 'Pizza', TRUE),
('Vegetariana', 'Verduras asadas de temporada con mozzarella.', 1190, 15, 'Pizza', TRUE),
('Diavola', 'Tomate, mozzarella y salami picante extra.', 1220, 15, 'Pizza', TRUE),
('Calzone clásico', 'Pizza cerrada rellena de jamón, mozzarella y champiñones.', 1350, 20, 'Pizza', TRUE);

-- PESCADO (9 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Salmón a la plancha con limón', 'Filete de salmón con aceite de oliva y limón.', 1790, 20, 'Pescado', TRUE),
('Lubina al horno', 'Lubina entera al horno con hierbas aromáticas.', 1950, 35, 'Pescado', TRUE),
('Bacalao con tomate', 'Bacalao en salsa de tomate casera.', 1680, 30, 'Pescado', TRUE),
('Atún a la parrilla', 'Atún fresco a la parrilla con especias.', 2100, 15, 'Pescado', TRUE),
('Merluza en salsa verde', 'Merluza con salsa de perejil y ajo.', 1590, 25, 'Pescado', TRUE),
('Dorada a la espalda', 'Dorada asada con aceite y limón.', 1820, 30, 'Pescado', TRUE),
('Pulpo a la brasa', 'Pulpo tierno a la brasa con pimentón.', 2250, 40, 'Pescado', TRUE),
('Calamares en su tinta', 'Calamares guisados en su propia tinta.', 1580, 35, 'Pescado', TRUE),
('Fritura mixta de mar', 'Selección de pescado y marisco frito.', 1750, 20, 'Pescado', TRUE);

-- CARNE (9 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Pollo a la parrilla con hierbas', 'Pechuga de pollo con hierbas mediterráneas.', 1450, 25, 'Carne', TRUE),
('Solomillo de cerdo a la mostaza', 'Solomillo en salsa de mostaza y miel.', 1690, 30, 'Carne', TRUE),
('Entrecot de ternera', 'Corte premium de ternera a la parrilla.', 2200, 20, 'Carne', TRUE),
('Costillas BBQ', 'Costillas de cerdo con salsa barbacoa.', 1850, 45, 'Carne', TRUE),
('Carrillera de ternera', 'Carrillera guisada en vino tinto.', 1920, 120, 'Carne', TRUE),
('Albóndigas en salsa casera', 'Albóndigas de ternera en salsa de tomate.', 1350, 30, 'Carne', TRUE),
('Filete de pollo empanado', 'Pechuga de pollo empanada y frita.', 1280, 20, 'Carne', TRUE),
('Hamburguesa gourmet', 'Hamburguesa de carne premium con guarnición.', 1590, 15, 'Carne', TRUE),
('Cordero asado', 'Pierna de cordero al horno con patatas.', 2300, 90, 'Carne', TRUE);

-- POSTRES (9 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Tiramisú clásico', 'Clásico italiano con café, mascarpone y cacao.', 650, 15, 'Postre', TRUE),
('Panna cotta con frutos rojos', 'Postre de nata cocida con coulis de frutas.', 680, 20, 'Postre', TRUE),
('Helado artesanal (2 bolas)', 'Helado artesanal de vainilla o pistacho.', 480, 5, 'Postre', TRUE),
('Brownie con helado', 'Brownie de chocolate caliente con helado de vainilla.', 690, 10, 'Postre', TRUE),
('Tarta de queso al horno', 'Cheesecake cremoso con base de galleta.', 670, 60, 'Postre', TRUE),
('Coulant de chocolate', 'Bizcocho con centro de chocolate fundido.', 720, 12, 'Postre', TRUE),
('Fruta fresca de temporada', 'Selección de frutas frescas de temporada.', 450, 5, 'Postre', TRUE),
('Cannoli sicilianos', 'Dulce crujiente relleno de ricotta y frutas confitadas.', 580, 20, 'Postre', TRUE),
('Gelato affogato', 'Helado de vainilla con espresso caliente.', 590, 3, 'Postre', TRUE);

-- VINOS (9 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Rioja Crianza', 'Vino tinto Rioja con crianza en barrica.', 1800, 1, 'Vino', TRUE),
('Albariño Rías Baixas', 'Vino blanco gallego aromático y fresco.', 1780, 1, 'Vino', TRUE),
('Chianti DOCG', 'Vino tinto italiano de la Toscana.', 1890, 1, 'Vino', TRUE),
('Ribera del Duero Crianza', 'Vino tinto con denominación de origen Ribera del Duero.', 2600, 1, 'Vino', TRUE),
('Godello sobre lías (Valdeorras)', 'Vino blanco con crianza sobre lías.', 2450, 1, 'Vino', TRUE),
('Barolo joven (Piamonte)', 'Vino tinto italiano premium del Piamonte.', 3200, 1, 'Vino', TRUE),
('Ribera del Duero Reserva', 'Vino tinto reserva de Ribera del Duero.', 4500, 1, 'Vino', TRUE),
('Chablis Premier Cru', 'Vino blanco francés de Borgoña.', 4800, 1, 'Vino', TRUE),
('Brunello di Montalcino', 'Vino tinto italiano de alta gama.', 6200, 1, 'Vino', TRUE);

-- MENÚ INFANTIL (2 recetas)
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria, disponible) VALUES
('Mini hamburguesa + patatas + bebida + helado', 'Menú completo infantil con hamburguesa.', 990, 15, 'Menu Infantil', TRUE),
('Pasta corta con tomate + bebida + fruta', 'Menú infantil con pasta y postre saludable.', 850, 12, 'Menu Infantil', TRUE);

-- =============================================
-- COMANDAS DE EJEMPLO
-- =============================================
INSERT INTO Comanda (id_mesa, id_usuario, total, estado_comanda) VALUES
(2, 3, 2840, 'EN_PROCESO'),
(5, 4, 2680, 'POR_HACER');

-- Detalles de comandas (precios en céntimos)
INSERT INTO Detalle_Comanda (id_comanda, id_receta, cantidad, precio_unitario, estado_plato) VALUES
(1, 10, 1, 1290, 'EN_COCINA'),  -- Spaghetti Carbonara
(1, 1, 1, 650, 'PREPARADO'),    -- Bruschetta clásica
(1, 19, 1, 900, 'POR_HACER'),   -- Margherita
(2, 28, 1, 1790, 'POR_HACER'),  -- Salmón a la plancha
(2, 2, 1, 890, 'POR_HACER');    -- Ensalada caprese

-- =============================================
-- VERIFICACIÓN DE DATOS
-- =============================================
SELECT '===== VERIFICACIÓN DE DATOS =====' AS Info;
SELECT 'USUARIOS' AS Tabla, COUNT(*) AS Total FROM Usuario;
SELECT 'MESAS' AS Tabla, COUNT(*) AS Total FROM Mesa;
SELECT 'RECETAS' AS Tabla, COUNT(*) AS Total FROM Receta;
SELECT 'INGREDIENTES' AS Tabla, COUNT(*) AS Total FROM Ingrediente;
SELECT 'COMANDAS' AS Tabla, COUNT(*) AS Total FROM Comanda;
SELECT 'DETALLES' AS Tabla, COUNT(*) AS Total FROM Detalle_Comanda;

SELECT '===== COMANDAS CON DETALLES =====' AS Info;
SELECT c.id_comanda, m.id_mesa, u.nombre AS camarero, c.estado_comanda, 
       COUNT(d.id_detalle) AS num_platos
FROM Comanda c
JOIN Mesa m ON c.id_mesa = m.id_mesa
JOIN Usuario u ON c.id_usuario = u.id_usuario
LEFT JOIN Detalle_Comanda d ON c.id_comanda = d.id_comanda
GROUP BY c.id_comanda;
