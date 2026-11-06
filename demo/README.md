TiendaApp - Backend REST API
API desarrollada en Spring Boot para la gestión de una tienda virtual, aplicando arquitectura en capas, seguridad personalizada con JWT y control de roles mediante interceptores.

Tecnologias Utilizadas
Java 17+
Spring Boot
Spring Data JPA / Hibernate
MySQL
Lombok
JJWT (JSON Web Token)

Roles del Sistema
ADMIN (ID: 1): Gestion total de usuarios, roles, estados, categorias y supervision general.
CLIENTE (ID: 2): Registro publico, consulta del catalogo, creacion de pedidos propios y cancelacion de pedidos pendientes.
VENDEDOR (ID: 3): Gestion de sus propios productos, consulta de ventas y actualizacion del estado de sus pedidos.

Estructura del Proyecto
config: Clases de configuracion (Beans, filtros e interceptores).
controller: Controladores REST con endpoints protegidos por @RequiresRole.
dto: Objetos de transferencia de datos (Request y Response).
entity: Entidades de la base de datos mapeadas con JPA.
enums: Enumeraciones para roles y estados de pedidos.
filter: Filtro de validacion y extraccion de tokens JWT.
repository: Repositorios de Spring Data JPA.
security: Anotacion personalizada e interceptor de control de roles.
service: Logica de negocio de la aplicacion.

Configuracion y Ejecucion
Configura tu base de datos MySQL en el archivo application.yaml.
Asegurate de ejecutar los scripts SQL iniciales para poblar los roles obligatorios (ADMIN, CLIENTE, VENDEDOR).
Ejecuta la aplicacion desde tu entorno de desarrollo corriendo la clase principal DemoApplication.java.