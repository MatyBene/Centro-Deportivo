# Centro Deportivo

Sistema de gestión integral para centros deportivos que centraliza la administración de socios, instructores, actividades, inscripciones y rutinas de entrenamiento. La solución combina una API REST escalable, una aplicación web y una aplicación móvil, ofreciendo funcionalidades según el rol de cada usuario: desde la consulta pública del catálogo de actividades hasta la gestión completa de usuarios, la creación de rutinas de entrenamiento y un asistente virtual con inteligencia artificial.

El repositorio es un monorepo que agrupa el backend, la aplicación web, la aplicación móvil y toda la documentación del proyecto. 

El sistema fue desarrollado como Trabajo Integrador Final de la Tecnicatura Universitaria en Programación.

## Modulos del repositorio

| Modulo | Descripcion |
|---|---|
| `centro-deportivo-api` | API RESTful desarrollada con Spring Boot y Java. |
| `centro-deportivo-front` | Aplicacion web SPA desarrollada con Angular. |
| `centro-deportivo-mobile` | Aplicacion movil desarrollada con Ionic y Capacitor. |
| `documentacion` | Documentacion del proyecto: diagramas, requisitos, pruebas y presentaciones. |

## Caracteristicas Principales

El sistema esta diseñado para atender las necesidades de distintos tipos de usuarios, cada uno con funcionalidades especificas para su rol.

### Roles de Usuario

- **Publico (No autenticado):**

  - Consultar el listado de actividades disponibles.
  - Ver los detalles de cada actividad (horario, instructor, precio).
  - Registrarse como nuevo socio o iniciar sesion.

- **Socio (MEMBER):**

  - Gestionar su perfil: editar informacion personal y eliminar su cuenta.
  - Buscar actividades por nombre o rango horario.
  - Inscribirse y darse de baja de las actividades.
  - Consultar el listado de clases en las que esta inscripto.

- **Instructor (INSTRUCTOR):**

  - Ver el listado de todas las actividades del sistema y en detalle las que tiene asignadas.
  - Listar y consultar informacion de todos los socios.
  - Inscribir y dar de baja a socios en las actividades que dicta.

- **Administrador (ADMIN):**

  - Gestion total de usuarios: puede crear, listar, ver detalles y eliminar socios e instructores.
  - Inscribir o dar de baja a cualquier socio en cualquier actividad del sistema.
  - Control granular mediante `PermissionLevel`.

### Niveles de Permiso de Administrador (`PermissionLevel`)

El rol de ADMIN se complementa con niveles de permiso para un control de acceso mas detallado:

- `USER_MANAGER`: Permisos para gestionar usuarios de menor jerarquia.
  - Puede crear y eliminar socios e instructores.
  - Puede gestionar las inscripciones de los socios.
- `SUPER_ADMIN`: El nivel mas alto de permisos.
  - Hereda todos los permisos de USER_MANAGER.
  - Adicionalmente, puede crear y eliminar otros administradores (excepto a otros SUPER_ADMIN).

### Modulo de Rutinas

Gestion de rutinas de entrenamiento para los socios:

- Crear, editar y eliminar rutinas compuestas por dias de entrenamiento y ejercicios.
- Asignar rutinas a socios y controlar su estado (activa/inactiva).
- Registro de historial de entrenamiento con seguimiento del progreso por ejercicio.
- Graficos de progreso para visualizar la evolucion del entrenamiento.

### Asistente Virtual (ChatBum)

La API integra un chatbot con inteligencia artificial (Google Gemini) que responde consultas sobre las actividades del centro:

- Consultar horarios, instructores, precios y disponibilidad de las actividades.
- Para usuarios autenticados, consultar su perfil y las inscripciones vigentes.

### Vencimiento de Inscripciones

Las inscripciones a actividades tienen una vigencia de 30 dias. Un proceso programado (job) que se ejecuta diariamente se encarga de:

- Eliminar las inscripciones vencidas y liberar los cupos correspondientes.
- Pasar a estado INACTIVO a los socios que quedan sin ninguna inscripcion activa.

## Tecnologias Utilizadas

### Backend (`centro-deportivo-api`)

- Java 21, Spring Boot 3.4.5.
- Base de datos: MySQL (dos bases de datos: `API_centro_deportivo` y `routines_db`).
- Persistencia de datos: Spring Data JPA (Hibernate).
- Seguridad: Spring Security, JSON Web Tokens (JWT).
- Servidor web: Spring Web (Tomcat embebido).
- Validaciones: Jakarta Bean Validation.
- Documentacion de API: OpenAPI 3 (Swagger).
- Gestion de dependencias: Maven.
- Utilitarios: Lombok.

### Frontend (`centro-deportivo-front`)

- Angular 20.
- Chart.js para los graficos de progreso.
- Swiper para los carruseles de imagenes.
- jwt-decode para la gestion de tokens JWT.

### Aplicacion Movil (`centro-deportivo-mobile`)

- Ionic 8 y Angular 20.
- Capacitor 7 para la compilacion a plataformas nativas (Android).
- Consume la misma API REST que el frontend web.

## Arquitectura

El backend sigue una **arquitectura en capas** para asegurar una correcta separacion de responsabilidades y facilitar su mantenimiento y escalabilidad.

- **Controller:** Expone los endpoints de la API, maneja las solicitudes HTTP y las respuestas.
- **Service:** Contiene la logica de negocio principal y orquesta las operaciones entre los controladores y los repositorios.
- **Repository:** Gestiona la comunicacion con la base de datos a traves de Spring Data JPA.
- **Model:** Define las entidades del dominio (User, Activity, Routine, etc.), DTOs y enumeraciones.

La aplicacion utiliza una configuracion de **doble fuente de datos (datasource)**:

- `API_centro_deportivo`: almacena los datos de usuarios, actividades e inscripciones.
- `routines_db`: almacena los datos del modulo de rutinas.

## Instalacion

Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno local.

### Prerrequisitos

- Java JDK 21 o superior.
- Apache Maven 3.9 o superior.
- Una instancia de MySQL en ejecucion.
- Node.js y npm.

### Backend (`centro-deportivo-api`)

1. **Clonar el repositorio:**

   ```sh
   git clone https://github.com/MatyBene/Centro-Deportivo.git
   cd Centro-Deportivo/centro-deportivo-api
   ```

2. **Configurar las bases de datos:**

   Crea las dos bases de datos en MySQL:

   ```sql
   CREATE DATABASE API_centro_deportivo;
   CREATE DATABASE routines_db;
   ```

3. **Configurar las variables de entorno:**

   El archivo `src/main/resources/application.properties` lee las credenciales de las bases de datos, la clave secreta del JWT y la API key de Gemini desde variables de entorno:

   ```properties
   spring.datasource.users.jdbc-url=jdbc:mysql://localhost:3306/API_centro_deportivo
   spring.datasource.users.username=${USERS_DB_USER}
   spring.datasource.users.password=${USERS_DB_PASSWORD}

   spring.datasource.routines.jdbc-url=jdbc:mysql://localhost:3306/routines_db
   spring.datasource.routines.username=${ROUTINES_DB_USER}
   spring.datasource.routines.password=${ROUTINES_DB_PASSWORD}

   jwt.secret=${JWT_SECRET_KEY}

   gemini.api.key=${GEMINI_API_KEY}
   ```

   Define las siguientes variables de entorno con tus credenciales y claves:

   - `USERS_DB_USER`: usuario de MySQL para la base de usuarios.
   - `USERS_DB_PASSWORD`: contrasena de MySQL para la base de usuarios.
   - `ROUTINES_DB_USER`: usuario de MySQL para la base de rutinas.
   - `ROUTINES_DB_PASSWORD`: contrasena de MySQL para la base de rutinas.
   - `JWT_SECRET_KEY`: clave para firmar los tokens JWT.
   - `GEMINI_API_KEY`: clave de la API de Google Gemini para el asistente virtual.

4. **Ejecutar la API:**

   ```sh
   mvn spring-boot:run
   ```


### Frontend (`centro-deportivo-front`)

1. **Instalar las dependencias:**

   ```sh
   cd Centro-Deportivo/centro-deportivo-front
   npm install
   ```

2. **Ejecutar el servidor de desarrollo:**

   ```sh
   ng serve
   ```



### Aplicacion Movil (`centro-deportivo-mobile`)

1. **Instalar las dependencias:**

   ```sh
   cd Centro-Deportivo/centro-deportivo-mobile
   npm install
   ```

2. **Ejecutar en modo desarrollo:**

   ```sh
   ng serve
   ```



3. **Compilar para Android (opcional):**

   ```sh
   npx cap add android
   npx cap sync android
   npx cap open android
   ```

## Autores

- [Bassi Servant, Tomas](https://github.com/tomasbassi)
- [Benedetti, Matias](https://github.com/MatyBene)
