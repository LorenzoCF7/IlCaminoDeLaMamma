# 🍝 Il Camino Della Mamma

Proyecto creado por Lorenzo Cruz Fernandez, Marco Antonio Cardo Caballero, Luis Capel Velázquez y Mario Sanchez Ruiz.

**Il Camino Della Mamma** es una aplicación de escritorio desarrollada en **Java** que combina el uso de **JavaFX**, **JPA/Hibernate** y **MySQL**, con soporte para **JSON y XML**.  
Su objetivo es gestionar las **recetas y comandas** de un restaurante de forma moderna, ordenada y visualmente elegante.

---

## 🎯 Objetivo del proyecto
El sistema permite administrar las **recetas**, **usuarios**, **roles**, **mesas** y **comandas** de un restaurante.  
Además, ofrece una vista para los **clientes**, quienes pueden acceder a la carta mediante **códigos QR**, sin necesidad de autenticación.

---

## 👥 Roles del sistema

| Rol | Descripción | Permisos principales |
|-----|--------------|----------------------|
| 👨‍🍳 **Administrador / Chef** | Gestiona usuarios, recetas y comandas. | CRUD completo de recetas y usuarios, exportar reportes, estadísticas. |
| 👨‍🍳 **Cocinero / Ayudante** | Consulta recetas y actualiza el estado de las comandas. | Leer recetas, marcar comandas como “en preparación”, “listas” o “entregadas”. |
| 🧑‍🍽️ **Camarero** | Crea y gestiona las comandas. | Crear, editar y cerrar comandas; consultar el menú disponible. |
| 🍷 **Cliente** | Accede a la carta del restaurante mediante QR. | Consultar recetas y buscar por nombre o categoría. |

---

## 🧩 Requisitos funcionales principales

1. **Gestión de usuarios y roles**  
   - Crear, editar y eliminar usuarios.  
   - Asignar roles (Administrador, Cocinero, Camarero, Cliente).  

2. **Gestión de recetas**  
   - CRUD de recetas (nombre, ingredientes, pasos, categoría, tiempo, precio).  
   - Consultar recetas por categoría o palabra clave.  
   - Activar o desactivar recetas del menú.

3. **Gestión de comandas**  
   - Crear comandas asociadas a mesas.  
   - Añadir o quitar recetas y unidades.  
   - Cerrar comandas y calcular el total.  

4. **Exportación y reportes**  
   - Exportar todas las comandas del día en formato **JSON**.  
   - Consultar estadísticas por día, plato o categoría.

5. **Interfaz de cliente (QR)**  
   - Acceso directo a la carta mediante QR.  
   - Sin autenticación ni cambio de rol.  

---

## 🧱 Arquitectura del proyecto

