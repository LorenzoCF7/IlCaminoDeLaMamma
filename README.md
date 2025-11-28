# 🍝 Il Camino Della Mamma - Sistema de Gestión de Restaurante

**Proyecto creado por:** Lorenzo Cruz Fernández, Marco Antonio Cardo Caballero, Luis Capel Velázquez y Mario Sánchez Ruiz.

Sistema completo de gestión para restaurante italiano con múltiples vistas por rol de usuario, desarrollado con JavaFX, Spring Boot y Hibernate.

---

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Ejecución de las Aplicaciones](#-ejecución-de-las-aplicaciones)
- [Vistas del Sistema](#-vistas-del-sistema)
- [Arquitectura](#-arquitectura)
- [Base de Datos](#-base-de-datos)
- [Solución de Problemas](#-solución-de-problemas)

---

## 📖 Descripción del Proyecto

Il Camino Della Mamma es un sistema integral de gestión para restaurantes que incluye:

- **Gestión de Recetas**: Creación, edición y visualización de recetas por categorías
- **Gestión de Comandas**: Control de pedidos desde su creación hasta su preparación
- **Gestión de Ingredientes**: Control de inventario y stock
- **Sistema de Roles**: Diferentes vistas según el rol del usuario
- **Reportes**: Generación de informes y estadísticas

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.1.5** - Framework principal
- **Hibernate 6.6.3** - ORM para persistencia de datos
- **MySQL** - Base de datos relacional
- **Maven** - Gestor de dependencias

### Frontend
- **JavaFX 21** - Framework para interfaces gráficas
- **FXML** - Lenguaje de marcado para interfaces
- **CSS** - Estilos personalizados

### Otras Librerías
- **Jackson** - Procesamiento JSON
- **ZXing** - Generación de códigos QR
- **SLF4J/Logback** - Sistema de logging

---

## 📁 Estructura del Proyecto

```
IlCaminoDeLaMamma/
├── src/main/
│   ├── java/ilcaminodelamamma/
│   │   ├── config/                   # Configuraciones (Spring, DB, Security)
│   │   │   ├── AppConfig.java
│   │   │   ├── DatabaseConfig.java
│   │   │   ├── HibernateUtil.java
│   │   │   └── SecurityConfig.java
│   │   │
│   │   ├── model/                    # Entidades del dominio
│   │   │   ├── Comanda.java
│   │   │   ├── DetalleComanda.java
│   │   │   ├── Ingrediente.java
│   │   │   ├── Mesa.java
│   │   │   ├── Receta.java
│   │   │   ├── RecetaIngrediente.java
│   │   │   ├── Rol.java
│   │   │   └── Usuario.java
│   │   │
│   │   ├── repository/               # Acceso a datos
│   │   │   ├── ComandaRepository.java
│   │   │   ├── IngredienteRepository.java
│   │   │   ├── MesaRepository.java
│   │   │   ├── RecetaRepository.java
│   │   │   ├── RolRepository.java
│   │   │   └── UsuarioRepository.java
│   │   │
│   │   ├── service/                  # Lógica de negocio
│   │   │   ├── ComandaService.java
│   │   │   ├── IngredienteService.java
│   │   │   ├── MesaService.java
│   │   │   ├── RecetaService.java
│   │   │   ├── ReporteService.java
│   │   │   ├── RolService.java
│   │   │   └── UsuarioService.java
│   │   │
│   │   ├── controller/               # Controladores REST
│   │   │   ├── ComandaController.java
│   │   │   ├── IngredienteController.java
│   │   │   ├── MesaController.java
│   │   │   ├── RecetaController.java
│   │   │   ├── RolController.java
│   │   │   └── UsuarioController.java
│   │   │
│   │   └── view/                     # Vistas JavaFX
│   │       ├── components/           # Componentes reutilizables
│   │       │   ├── Header.java       # Cabecera con logo y buscador
│   │       │   └── Footer.java       # Pie de página
│   │       │
│   │       ├── chef/                 # Vista Jefe de Cocina
│   │       │   ├── ChefApp.java
│   │       │   └── ChefViewController.java
│   │       │
│   │       ├── waiter/               # Vista Camarero
│   │       │   ├── WaiterApp.java
│   │       │   └── WaiterViewController.java
│   │       │
│   │       └── assistant/            # Vista Ayudante
│   │           ├── KitchenAssistantApp.java
│   │           └── KitchenAssistantViewController.java
│   │
│   └── resources/
│       ├── fxml/                     # Archivos de interfaz
│       │   ├── chef/
│       │   │   └── chef-view.fxml
│       │   ├── waiter/
│       │   │   └── waiter-view.fxml
│       │   ├── assistant/
│       │   │   └── assistant-view.fxml
│       │   └── login/
│       │       └── login.fxml
│       │
│       ├── css/                      # Estilos
│       │   ├── chef-view.css
│       │   ├── waiter-view.css
│       │   └── assistant-view.css
│       │
│       ├── img/                      # Imágenes
│       │   ├── logo.png
│       │   ├── Entrantes.jpg
│       │   ├── Postres.jpg
│       │   ├── Pasta.png
│       │   ├── Pizza.jpg
│       │   ├── Menu_Infantil.png
│       │   ├── Pescados.png
│       │   └── Carnes.jpg
│       │
│       ├── bbdd/                     # Scripts SQL
│       │   ├── BBDD_IlCaminoDellaMamma.sql
│       │   └── IlCaminoDeLaMamma.sql
│       │
│       └── hibernate.cfg.xml         # Configuración Hibernate
│
├── pom.xml                           # Configuración Maven
└── README.md                         # Este archivo
```

---

## ✅ Requisitos Previos

### Software Necesario

1. **Java Development Kit (JDK) 21**
   - Descargar desde: https://www.oracle.com/java/technologies/downloads/
   - Verificar instalación: `java -version`

2. **Maven 3.x** (Opcional si usas IntelliJ)
   - Descargar desde: https://maven.apache.org/download.cgi
   - Verificar instalación: `mvn -version`

3. **MySQL Server**
   - Descargar desde: https://dev.mysql.com/downloads/mysql/
   - Crear la base de datos usando los scripts en `src/main/resources/bbdd/`

4. **IDE Recomendado**
   - IntelliJ IDEA Community Edition 2024.3.3 o superior
   - O Eclipse con plugin de Maven

---

## 📥 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/LorenzoCF7/IlCaminoDeLaMamma.git
cd IlCaminoDeLaMamma
```

### 2. Configurar Base de Datos

1. Crear la base de datos en MySQL:
```sql
CREATE DATABASE ilcamino_db;
```

2. Ejecutar los scripts SQL:
```bash
mysql -u root -p ilcamino_db < src/main/resources/bbdd/IlCaminoDeLaMamma.sql
```

3. Configurar credenciales en `src/main/resources/hibernate.cfg.xml`:
```xml
<property name="hibernate.connection.username">tu_usuario</property>
<property name="hibernate.connection.password">tu_contraseña</property>
```

### 3. Compilar el Proyecto

#### Usando Maven:
```bash
mvn clean install
```

#### Usando IntelliJ IDEA:
1. Abrir el proyecto
2. Esperar a que Maven descargue las dependencias
3. Build → Build Project

---

## 🚀 Ejecución de las Aplicaciones

### Método 1: Usando Maven (Recomendado)

#### Vista del Jefe de Cocina (Chef)
```bash
mvn javafx:run
```

#### Vista del Camarero
```bash
mvn javafx:run -Pwaiter
```

#### Vista del Ayudante de Cocina
```bash
mvn javafx:run -Passistant
```

### Método 2: Usando IntelliJ IDEA

1. Localizar el dropdown de configuraciones (esquina superior derecha)
2. Seleccionar una de las siguientes opciones:
   - **ChefApp** - Vista del Jefe de Cocina
   - **WaiterApp** - Vista del Camarero
   - **KitchenAssistantApp** - Vista del Ayudante
3. Presionar el botón verde de ejecución ▶️

### Método 3: Desde Visual Studio Code o PowerShell

```powershell
# Vista del Chef
& "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.3\plugins\maven\lib\maven3\bin\mvn.cmd" javafx:run

# Vista del Camarero
& "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.3\plugins\maven\lib\maven3\bin\mvn.cmd" javafx:run -Pwaiter

# Vista del Ayudante
& "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.3\plugins\maven\lib\maven3\bin\mvn.cmd" javafx:run -Passistant
```

---

## 👥 Vistas del Sistema

### 1. 👨‍🍳 Vista del Jefe de Cocina (Chef)

**Permisos:** COMPLETOS - Crear, Editar, Eliminar

**Características:**
- ➕ **Nueva Receta** - Crear recetas nuevas
- 📚 **Libros de Cocina** - Gestionar categorías y colecciones
- 📋 **Lista de Comandas** - Ver y gestionar pedidos
- 🥬 **Ingredientes** - Administrar inventario
- 📊 **Reportes** - Generar informes
- ⚙️ **Configuración** - Ajustes del sistema

**Interfaz:**
- Sidebar izquierdo con menú de navegación
- Grid central con categorías de recetas (Entrantes, Postres, Pasta, Pizza, etc.)
- Panel derecho con recetas vistas recientemente
- Buscador en la cabecera
- Pestañas de filtro: Platos | Categorías | Favoritos

### 2. 🍽️ Vista del Camarero (Waiter)

**Permisos:** Gestión de Comandas

**Características:**
- ➕ **Nueva Comanda** - Crear nuevos pedidos
- 📚 **Lista de Comandas** - Ver todas las comandas
- ❓ **Ayuda** - Acceso a documentación
- ⚙️ **Configuración** - Ajustes personales

**Interfaz:**
- Lista de comandas con imágenes de platos
- Información de mesa para cada comanda
- Botones de acción (flecha →) para ver detalles
- Pestañas de filtro: Todas | Preparación | Listas
- Vista optimizada para gestión rápida de pedidos

### 3. 👨‍🍳 Vista del Ayudante de Cocina (Kitchen Assistant)

**Permisos:** SOLO LECTURA

**Características:**
- 📚 **Ver Recetas** - Consultar recetas sin modificar
- 📋 **Ver Comandas** - Consultar pedidos activos
- 🥬 **Ver Ingredientes** - Consultar inventario
- ❓ **Ayuda** - Acceso a documentación

**Interfaz:**
- Idéntica al Jefe de Cocina pero SIN botones de edición
- NO tiene botón "Nueva Receta"
- NO tiene opciones de crear/editar/eliminar
- Visualización completa de toda la información

---

## 🎨 Paleta de Colores Unificada

Todas las vistas comparten la misma paleta de colores:

| Elemento | Color | Código Hex |
|----------|-------|------------|
| Sidebar | Marrón | #8B7355 → #6B5645 |
| Header | Dorado | #D4A574 |
| Fondo Central | Beige | #F5E6D3 |
| Footer | Marrón Oscuro | #5C4033 |
| Panel Derecho | Beige Claro | #E8D4B8 |
| Texto Principal | Marrón Oscuro | #2C1810 |
| Botón Primario | Dorado | #D4A574 |
| Botón Hover | Dorado Claro | #E5B685 |
| Botón Cerrar Sesión | Rojo | rgba(220, 53, 69, 0.8) |

---

## 🏗️ Arquitectura

### Patrón MVC (Model-View-Controller)

```
┌─────────────────────────────────────┐
│          VISTA (JavaFX)             │
│  - ChefApp / WaiterApp / Assistant  │
│  - FXML + CSS + Controllers         │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      CONTROLADORES (REST)           │
│  - ComandaController                │
│  - RecetaController                 │
│  - IngredienteController            │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       SERVICIOS (Business Logic)    │
│  - ComandaService                   │
│  - RecetaService                    │
│  - IngredienteService               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│     REPOSITORIOS (Data Access)      │
│  - ComandaRepository                │
│  - RecetaRepository                 │
│  - IngredienteRepository            │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│         BASE DE DATOS (MySQL)       │
│  - Comandas, Recetas, Ingredientes  │
└─────────────────────────────────────┘
```

### Componentes Reutilizables

#### Header.java
Componente de cabecera que incluye logo y campo de búsqueda opcional:

```java
// Con buscador
Header header = new Header(true);

// Sin buscador
Header headerSimple = new Header(false);

// Acceder al campo de búsqueda
TextField searchField = header.getSearchField();
```

#### Footer.java
Componente de pie de página con texto personalizable:

```java
// Con texto por defecto
Footer footer = new Footer();

// Con texto personalizado
Footer footer = new Footer("Mi texto personalizado");

// Cambiar texto dinámicamente
footer.setText("Nuevo texto");
```

---

## 💾 Base de Datos

### Tablas Principales

- **usuarios** - Información de usuarios del sistema
- **roles** - Roles del sistema (Chef, Camarero, Ayudante)
- **recetas** - Información de recetas
- **ingredientes** - Inventario de ingredientes
- **recetas_ingredientes** - Relación entre recetas e ingredientes
- **comandas** - Pedidos del restaurante
- **mesas** - Mesas del restaurante
- **detalles_comanda** - Detalles de cada pedido

### Relaciones

```
usuarios (n) ──── (1) roles
recetas (n) ──── (n) ingredientes
comandas (n) ──── (1) mesas
comandas (1) ──── (n) detalles_comanda
detalles_comanda (n) ──── (1) recetas
```

---

## 🔄 Actualizar Recursos

Si modificas archivos en `src/main/resources/` (imágenes, CSS, FXML):

```bash
# Limpiar y recompilar
mvn clean compile

# Ejecutar la aplicación
mvn javafx:run -P[profile]
```

### Desde IntelliJ IDEA

1. Click derecho en el proyecto
2. Maven → Reload Project
3. Build → Rebuild Project
4. Ejecutar normalmente

---

## 🐛 Solución de Problemas

### Error: "JavaFX runtime components are missing"

**Solución:** Usar siempre Maven para ejecutar
```bash
mvn javafx:run
```

### Error: Las imágenes no se cargan

**Solución:** Recompilar el proyecto
```bash
mvn clean compile
mvn javafx:run
```

### Error: No se puede conectar a la base de datos

**Solución:**
1. Verificar que MySQL está corriendo
2. Comprobar credenciales en `hibernate.cfg.xml`
3. Verificar que la base de datos existe

### Error: Compilación fallida

**Solución:**
```bash
mvn clean install -U
```

---

## 📝 Perfiles Maven

El proyecto tiene 3 perfiles configurados:

| Perfil | Clase Principal | Comando |
|--------|----------------|---------|
| chef (defecto) | ChefApp | `mvn javafx:run` |
| waiter | WaiterApp | `mvn javafx:run -Pwaiter` |
| assistant | KitchenAssistantApp | `mvn javafx:run -Passistant` |

---

## 🎯 Estado del Proyecto

✅ **Completado:**
- Configuración base de Spring Boot + JavaFX
- Vista del Jefe de Cocina (completa)
- Vista del Camarero (completa)
- Vista del Ayudante de Cocina (completa)
- Componentes reutilizables (Header, Footer)
- Sistema de perfiles Maven
- Paleta de colores unificada
- Documentación completa

🔄 **Pendiente:**
- Sistema de autenticación (Login)
- Conexión completa con base de datos
- Funcionalidad de creación/edición de recetas
- Sistema de reportes
- Generación de códigos QR

---

**Repositorio:** https://github.com/LorenzoCF7/IlCaminoDeLaMamma  
**Branch Principal:** master  
**Branch Desarrollo:** marco

**Fecha de última actualización:** 28 de Noviembre de 2025  
**Versión:** 1.0-SNAPSHOT

