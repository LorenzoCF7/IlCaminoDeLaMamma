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
-- ENTRANTES (9 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Bruschetta clásica', 'Pan tostado con tomate, ajo y albahaca.', 650, 10, 'Entrante'),
('Ensalada caprese', 'Tomate, mozzarella fresca y albahaca.', 890, 8, 'Entrante'),
('Carpaccio de ternera', 'Láminas finas de ternera con rúcula y parmesano.', 1200, 12, 'Entrante'),
('Tabla de quesos italianos', 'Selección de quesos tradicionales italianos.', 1450, 5, 'Entrante'),
('Sopa minestrone', 'Sopa italiana de verduras con pasta.', 720, 35, 'Entrante'),
('Calamares fritos', 'Calamares rebozados y fritos.', 1180, 20, 'Entrante'),
('Provolone al horno', 'Queso provolone fundido con especias.', 950, 12, 'Entrante'),
('Tartar de salmón', 'Dados de salmón marinado con cítricos.', 1390, 15, 'Entrante'),
('Antipasto mixto', 'Variedad de embutidos, quesos y encurtidos italianos.', 1500, 10, 'Entrante');

-- PASTA (9 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Spaghetti Carbonara', 'Spaghetti con salsa de huevo, queso y panceta.', 1290, 20, 'Pasta'),
('Penne Arrabbiata', 'Pasta corta con salsa de tomate picante.', 1150, 15, 'Pasta'),
('Tagliatelle al pesto', 'Pasta con salsa de albahaca, piñones y parmesano.', 1320, 18, 'Pasta'),
('Lasagna boloñesa', 'Láminas de pasta con carne y bechamel.', 1450, 45, 'Pasta'),
('Ravioli de ricotta y espinacas', 'Raviolis rellenos con queso ricotta y espinacas.', 1380, 25, 'Pasta'),
('Gnocchi a la sorrentina', 'Ñoquis de patata con salsa de tomate y mozzarella.', 1200, 30, 'Pasta'),
('Fettuccine Alfredo', 'Pasta con salsa cremosa de mantequilla y parmesano.', 1390, 20, 'Pasta'),
('Tortellini panna e prosciutto', 'Tortellini con nata y jamón.', 1400, 22, 'Pasta'),
('Spaghetti marinara', 'Spaghetti con mariscos en salsa de tomate.', 1530, 30, 'Pasta');

-- PIZZA (9 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Margherita', 'Tomate, mozzarella y albahaca fresca.', 900, 15, 'Pizza'),
('Pepperoni', 'Tomate, mozzarella y salami picante.', 1150, 15, 'Pizza'),
('Cuatro quesos', 'Mozzarella, gorgonzola, parmesano y fontina.', 1250, 15, 'Pizza'),
('Hawaiana', 'Tomate, mozzarella, jamón y piña.', 1100, 15, 'Pizza'),
('BBQ Pollo', 'Pollo marinado en salsa BBQ con cebolla y mozzarella.', 1320, 18, 'Pizza'),
('Prosciutto e funghi', 'Jamón cocido, champiñones y mozzarella.', 1280, 15, 'Pizza'),
('Vegetariana', 'Verduras asadas de temporada con mozzarella.', 1190, 15, 'Pizza'),
('Diavola', 'Tomate, mozzarella y salami picante extra.', 1220, 15, 'Pizza'),
('Calzone clásico', 'Pizza cerrada rellena de jamón, mozzarella y champiñones.', 1350, 20, 'Pizza');

-- PESCADO (9 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Salmón a la plancha con limón', 'Filete de salmón con aceite de oliva y limón.', 1790, 20, 'Pescado'),
('Lubina al horno', 'Lubina entera al horno con hierbas aromáticas.', 1950, 35, 'Pescado'),
('Bacalao con tomate', 'Bacalao en salsa de tomate casera.', 1680, 30, 'Pescado'),
('Atún a la parrilla', 'Atún fresco a la parrilla con especias.', 2100, 15, 'Pescado'),
('Merluza en salsa verde', 'Merluza con salsa de perejil y ajo.', 1590, 25, 'Pescado'),
('Dorada a la espalda', 'Dorada asada con aceite y limón.', 1820, 30, 'Pescado'),
('Pulpo a la brasa', 'Pulpo tierno a la brasa con pimentón.', 2250, 40, 'Pescado'),
('Calamares en su tinta', 'Calamares guisados en su propia tinta.', 1580, 35, 'Pescado'),
('Fritura mixta de mar', 'Selección de pescado y marisco frito.', 1750, 20, 'Pescado');

-- CARNE (9 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Pollo a la parrilla con hierbas', 'Pechuga de pollo con hierbas mediterráneas.', 1450, 25, 'Carne'),
('Solomillo de cerdo a la mostaza', 'Solomillo en salsa de mostaza y miel.', 1690, 30, 'Carne'),
('Entrecot de ternera', 'Corte premium de ternera a la parrilla.', 2200, 20, 'Carne'),
('Costillas BBQ', 'Costillas de cerdo con salsa barbacoa.', 1850, 45, 'Carne'),
('Carrillera de ternera', 'Carrillera guisada en vino tinto.', 1920, 120, 'Carne'),
('Albóndigas en salsa casera', 'Albóndigas de ternera en salsa de tomate.', 1350, 30, 'Carne'),
('Filete de pollo empanado', 'Pechuga de pollo empanada y frita.', 1280, 20, 'Carne'),
('Hamburguesa gourmet', 'Hamburguesa de carne premium con guarnición.', 1590, 15, 'Carne'),
('Cordero asado', 'Pierna de cordero al horno con patatas.', 2300, 90, 'Carne');

-- POSTRES (9 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Tiramisú clásico', 'Clásico italiano con café, mascarpone y cacao.', 650, 15, 'Postre'),
('Panna cotta con frutos rojos', 'Postre de nata cocida con coulis de frutas.', 680, 20, 'Postre'),
('Helado artesanal (2 bolas)', 'Helado artesanal de vainilla o pistacho.', 480, 5, 'Postre'),
('Brownie con helado', 'Brownie de chocolate caliente con helado de vainilla.', 690, 10, 'Postre'),
('Tarta de queso al horno', 'Cheesecake cremoso con base de galleta.', 670, 60, 'Postre'),
('Coulant de chocolate', 'Bizcocho con centro de chocolate fundido.', 720, 12, 'Postre'),
('Fruta fresca de temporada', 'Selección de frutas frescas de temporada.', 450, 5, 'Postre'),
('Cannoli sicilianos', 'Dulce crujiente relleno de ricotta y frutas confitadas.', 580, 20, 'Postre'),
('Gelato affogato', 'Helado de vainilla con espresso caliente.', 590, 3, 'Postre');

-- VINOS (9 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Rioja Crianza', 'Vino tinto Rioja con crianza en barrica.', 1800, 1, 'Vino'),
('Albariño Rías Baixas', 'Vino blanco gallego aromático y fresco.', 1780, 1, 'Vino'),
('Chianti DOCG', 'Vino tinto italiano de la Toscana.', 1890, 1, 'Vino'),
('Ribera del Duero Crianza', 'Vino tinto con denominación de origen Ribera del Duero.', 2600, 1, 'Vino'),
('Godello sobre lías (Valdeorras)', 'Vino blanco con crianza sobre lías.', 2450, 1, 'Vino'),
('Barolo joven (Piamonte)', 'Vino tinto italiano premium del Piamonte.', 3200, 1, 'Vino'),
('Ribera del Duero Reserva', 'Vino tinto reserva de Ribera del Duero.', 4500, 1, 'Vino'),
('Chablis Premier Cru', 'Vino blanco francés de Borgoña.', 4800, 1, 'Vino'),
('Brunello di Montalcino', 'Vino tinto italiano de alta gama.', 6200, 1, 'Vino');

-- MENÚ INFANTIL (2 recetas) - Precios en céntimos
INSERT INTO Receta (nombre, descripcion, precio, tiempo_preparacion, categoria) VALUES
('Mini hamburguesa + patatas + bebida + helado', 'Menú completo infantil con hamburguesa.', 990, 15, 'Menu Infantil'),
('Pasta corta con tomate + bebida + fruta', 'Menú infantil con pasta y postre saludable.', 850, 12, 'Menu Infantil');

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