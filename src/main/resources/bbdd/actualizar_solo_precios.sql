-- Script para actualizar SOLO los precios de las recetas existentes
-- Ejecuta este archivo en MySQL Workbench o tu gestor de BD

USE IlCaminoDeLaMamma;

-- Actualizar precios de ENTRANTES (en céntimos)
UPDATE Receta SET precio = 650 WHERE nombre = 'Bruschetta clásica';
UPDATE Receta SET precio = 890 WHERE nombre = 'Ensalada caprese';
UPDATE Receta SET precio = 1200 WHERE nombre = 'Carpaccio de ternera';
UPDATE Receta SET precio = 1450 WHERE nombre = 'Tabla de quesos italianos';
UPDATE Receta SET precio = 720 WHERE nombre = 'Sopa minestrone';
UPDATE Receta SET precio = 1180 WHERE nombre = 'Calamares fritos';
UPDATE Receta SET precio = 950 WHERE nombre = 'Provolone al horno';
UPDATE Receta SET precio = 1390 WHERE nombre = 'Tartar de salmón';
UPDATE Receta SET precio = 1500 WHERE nombre = 'Antipasto mixto';

-- Actualizar precios de PASTA (en céntimos)
UPDATE Receta SET precio = 1290 WHERE nombre = 'Spaghetti Carbonara';
UPDATE Receta SET precio = 1150 WHERE nombre = 'Penne Arrabbiata';
UPDATE Receta SET precio = 1320 WHERE nombre = 'Tagliatelle al pesto';
UPDATE Receta SET precio = 1450 WHERE nombre = 'Lasagna boloñesa';
UPDATE Receta SET precio = 1380 WHERE nombre = 'Ravioli de ricotta y espinacas';
UPDATE Receta SET precio = 1200 WHERE nombre = 'Gnocchi a la sorrentina';
UPDATE Receta SET precio = 1390 WHERE nombre = 'Fettuccine Alfredo';
UPDATE Receta SET precio = 1400 WHERE nombre = 'Tortellini panna e prosciutto';
UPDATE Receta SET precio = 1530 WHERE nombre = 'Spaghetti marinara';

-- Actualizar precios de PIZZA (en céntimos)
UPDATE Receta SET precio = 900 WHERE nombre = 'Margherita';
UPDATE Receta SET precio = 1150 WHERE nombre = 'Pepperoni';
UPDATE Receta SET precio = 1250 WHERE nombre = 'Cuatro quesos';
UPDATE Receta SET precio = 1100 WHERE nombre = 'Hawaiana';
UPDATE Receta SET precio = 1320 WHERE nombre = 'BBQ Pollo';
UPDATE Receta SET precio = 1280 WHERE nombre = 'Prosciutto e funghi';
UPDATE Receta SET precio = 1190 WHERE nombre = 'Vegetariana';
UPDATE Receta SET precio = 1220 WHERE nombre = 'Diavola';
UPDATE Receta SET precio = 1350 WHERE nombre = 'Calzone clásico';

-- Actualizar precios de PESCADO (en céntimos)
UPDATE Receta SET precio = 1790 WHERE nombre = 'Salmón a la plancha con limón';
UPDATE Receta SET precio = 1950 WHERE nombre = 'Lubina al horno';
UPDATE Receta SET precio = 1680 WHERE nombre = 'Bacalao con tomate';
UPDATE Receta SET precio = 2100 WHERE nombre = 'Atún a la parrilla';
UPDATE Receta SET precio = 1590 WHERE nombre = 'Merluza en salsa verde';
UPDATE Receta SET precio = 1820 WHERE nombre = 'Dorada a la espalda';
UPDATE Receta SET precio = 2250 WHERE nombre = 'Pulpo a la brasa';
UPDATE Receta SET precio = 1580 WHERE nombre = 'Calamares en su tinta';
UPDATE Receta SET precio = 1750 WHERE nombre = 'Fritura mixta de mar';

-- Actualizar precios de CARNE (en céntimos)
UPDATE Receta SET precio = 1450 WHERE nombre = 'Pollo a la parrilla con hierbas';
UPDATE Receta SET precio = 1690 WHERE nombre = 'Solomillo de cerdo a la mostaza';
UPDATE Receta SET precio = 2200 WHERE nombre = 'Entrecot de ternera';
UPDATE Receta SET precio = 1850 WHERE nombre = 'Costillas BBQ';
UPDATE Receta SET precio = 1920 WHERE nombre = 'Carrillera de ternera';
UPDATE Receta SET precio = 1350 WHERE nombre = 'Albóndigas en salsa casera';
UPDATE Receta SET precio = 1280 WHERE nombre = 'Filete de pollo empanado';
UPDATE Receta SET precio = 1590 WHERE nombre = 'Hamburguesa gourmet';
UPDATE Receta SET precio = 2300 WHERE nombre = 'Cordero asado';

-- Actualizar precios de POSTRES (en céntimos)
UPDATE Receta SET precio = 650 WHERE nombre = 'Tiramisú clásico';
UPDATE Receta SET precio = 680 WHERE nombre = 'Panna cotta con frutos rojos';
UPDATE Receta SET precio = 480 WHERE nombre = 'Helado artesanal (2 bolas)';
UPDATE Receta SET precio = 690 WHERE nombre = 'Brownie con helado';
UPDATE Receta SET precio = 670 WHERE nombre = 'Tarta de queso al horno';
UPDATE Receta SET precio = 720 WHERE nombre = 'Coulant de chocolate';
UPDATE Receta SET precio = 450 WHERE nombre = 'Fruta fresca de temporada';
UPDATE Receta SET precio = 580 WHERE nombre = 'Cannoli sicilianos';
UPDATE Receta SET precio = 590 WHERE nombre = 'Gelato affogato';

-- Actualizar precios de VINOS (en céntimos)
UPDATE Receta SET precio = 1800 WHERE nombre = 'Rioja Crianza';
UPDATE Receta SET precio = 1780 WHERE nombre = 'Albariño Rías Baixas';
UPDATE Receta SET precio = 1890 WHERE nombre = 'Chianti DOCG';
UPDATE Receta SET precio = 2600 WHERE nombre = 'Ribera del Duero Crianza';
UPDATE Receta SET precio = 2450 WHERE nombre = 'Godello sobre lías (Valdeorras)';
UPDATE Receta SET precio = 3200 WHERE nombre = 'Barolo joven (Piamonte)';
UPDATE Receta SET precio = 4500 WHERE nombre = 'Ribera del Duero Reserva';
UPDATE Receta SET precio = 4800 WHERE nombre = 'Chablis Premier Cru';
UPDATE Receta SET precio = 6200 WHERE nombre = 'Brunello di Montalcino';

-- Actualizar precios de MENÚ INFANTIL (en céntimos)
UPDATE Receta SET precio = 990 WHERE nombre = 'Mini hamburguesa + patatas + bebida + helado';
UPDATE Receta SET precio = 850 WHERE nombre = 'Pasta corta con tomate + bebida + fruta';

-- Verificar que se actualizaron correctamente
SELECT id_receta, nombre, precio, precio/100.0 AS precio_euros FROM Receta ORDER BY categoria, nombre;
