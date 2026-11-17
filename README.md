# personapp-hexa-spring-boot

Plantilla del Laboratorio de Arquitectura Limpia (hexagonal). Aplicación multimódulo con Spring Boot 2.7.11 y Java 11, que guarda personas en **MariaDB** y en **MongoDB**.

Tiene **dos adaptadores de entrada**, cada uno con su propia `SpringApplication`:

| Adaptador | Clase principal | Qué es |
|---|---|---|
| `rest-input-adapter` | `PersonAppRestApi` | API REST (puerto 3000) |
| `cli-input-adapter` | `PersonAppCli` | Menú por consola |

## Estructura del proyecto

```
personapp-hexa-spring-boot
├── common               -> utilidades y anotaciones compartidas
├── domain               -> modelos del dominio (sin dependencias de frameworks)
├── application          -> puertos de entrada/salida y casos de uso
├── maria-output-adapter -> persistencia con JPA (MariaDB)
├── mongo-output-adapter -> persistencia con Spring Data (MongoDB)
├── rest-input-adapter   -> controladores REST + Swagger
├── cli-input-adapter    -> menú por consola
└── scripts              -> scripts SQL y JS para crear las bases de datos
```

## 1. Requisitos

| Herramienta | Versión | Para qué |
|---|---|---|
| JDK | 11 | compilar y ejecutar |
| Maven | 3.6 o superior | compilar (el proyecto no trae `mvnw`) |
| MariaDB | cualquier versión reciente | base relacional |
| MongoDB | cualquier versión reciente (con `mongosh`) | base documental |
| Lombok | plugin en el IDE | el código usa anotaciones de Lombok |

Revisa que todo esté instalado:

```bash
java -version
mvn -version
```

### Lombok en el IDE

- **IntelliJ IDEA:** instalar el plugin *Lombok* y activar *Settings → Build → Compiler → Annotation Processors → Enable annotation processing*.
- **Eclipse / STS:** ejecutar el instalador `lombok.jar` y seleccionar el IDE, luego reiniciarlo.
- **VS Code:** instalar *Language Support for Java* y *Lombok Annotations Support*.

## 2. Configurar las bases de datos

### MariaDB (puerto **3307**)

El servidor debe estar escuchando en el puerto **3307**, no en el 3306 que viene por defecto. Se cambia con `port=3307` en el archivo de configuración de MariaDB (`my.ini` en Windows, `my.cnf` en Linux/Mac) y reiniciando el servicio.

Ejecuta los scripts en este orden, con un usuario administrador (por ejemplo `root`):

```bash
mariadb -u root -p -P 3307 -h 127.0.0.1 < scripts/persona_ddl_maria.sql
mariadb -u root -p -P 3307 -h 127.0.0.1 < scripts/persona_dml_maria.sql
```

- `persona_ddl_maria.sql`: crea el usuario `persona_db`, el esquema `persona_db` y las tablas.
- `persona_dml_maria.sql`: inserta datos de prueba.

> Ojo: el DDL **borra y vuelve a crear** el esquema `persona_db`. No lo corras sobre datos que quieras conservar.

### MongoDB (puerto **27017**)

Con MongoDB en el puerto 27017, ejecuta los scripts en este orden:

```bash
mongosh scripts/persona_ddl_mongo.js
mongosh scripts/persona_dml_mongo.js
```

- `persona_ddl_mongo.js`: crea el usuario `persona_db` en la base `admin`.
- `persona_dml_mongo.js`: inserta datos de prueba en la base `persona_db`.

## 3. Configuración de la aplicación

Cada adaptador de entrada tiene su archivo de configuración:

- `rest-input-adapter/src/main/resources/application.properties`
- `cli-input-adapter/src/main/resources/application.properties`

Valores por defecto (coinciden con los scripts):

| Propiedad | Valor |
|---|---|
| `server.port` (solo REST) | `3000` |
| `spring.datasource.url` | `jdbc:mariadb://localhost:3307/persona_db` |
| `spring.datasource.username` / `password` | `persona_db` / `persona_db` |
| `spring.data.mongodb.host` / `port` | `localhost` / `27017` |
| `spring.data.mongodb.database` | `persona_db` |
| `spring.data.mongodb.username` / `password` | `persona_db` / `persona_db` |
| `spring.data.mongodb.authentication-database` | `admin` |
| `logging.file.name` | `logs/persona.log` |

Si tus bases usan otro puerto, usuario o clave, cambia estos valores **en los dos archivos**.

## 4. Compilación

Desde la raíz del proyecto (donde está el `pom.xml` principal):

```bash
mvn clean install
```

Esto compila los 7 módulos en orden y genera los `.jar` en la carpeta `target/` de cada uno. Para saltarse las pruebas:

```bash
mvn clean install -DskipTests
```

## 5. Ejecución y despliegue

Antes de ejecutar, confirma que **MariaDB y MongoDB estén corriendo** y que los scripts ya se ejecutaron.

### Adaptador REST

```bash
mvn -pl rest-input-adapter spring-boot:run
```

O con el jar generado:

```bash
java -jar rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar
```

Cuando arranque:

- API: `http://localhost:3000`
- Swagger: `http://localhost:3000/swagger-ui.html`
- Prueba rápida: abre `http://localhost:3000/api/v1/persona/MARIA` en el navegador y debe devolver la lista de personas.

### Adaptador CLI

```bash
mvn -pl cli-input-adapter spring-boot:run
```

O con el jar:

```bash
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
```

Se abre un menú en la consola para trabajar con personas, profesiones, estudios y teléfonos.

> Los dos adaptadores son aplicaciones independientes. Puedes correr uno, el otro o ambos, pero el REST ocupa el puerto 3000.

## 6. API REST de personas

La API permite seleccionar `MARIA`/`MARIADB` o `MONGO`/`MONGODB` para las operaciones de lectura, actualización y eliminación:

| Operación | Método y ruta |
|---|---|
| Crear persona | `POST /api/v1/persona` |
| Listar personas | `GET /api/v1/persona/{database}` |
| Buscar por identificación | `GET /api/v1/persona/{database}/{dni}` |
| Contar personas | `GET /api/v1/persona/{database}/count` |
| Actualizar persona | `PUT /api/v1/persona/{database}/{dni}` |
| Eliminar persona | `DELETE /api/v1/persona/{database}/{dni}` |

El cuerpo de creación y actualización usa JSON con `dni`, `firstName`, `lastName`, `age`, `sex` y `database`. `age` puede omitirse o ser `null`; `sex` debe ser `M` o `F` (también se aceptan `MALE` y `FEMALE`). Para actualizar, la identificación de la ruta es la que se usa, aunque el cuerpo incluya otro valor en `dni`.

Ejemplo de creación:

```json
{
  "dni": "123456789",
  "firstName": "Ana",
  "lastName": "Lopez",
  "age": "30",
  "sex": "F",
  "database": "MONGO"
}
```

## 7. CRUD del esquema relacional MariaDB

Las operaciones para las tablas `persona`, `profesion`, `estudios` y `telefono` se exponen bajo `/api/v1/tablas`. El controlador REST invoca un puerto de entrada en `application`; el caso de uso depende de un puerto de salida basado en el dominio y el adaptador MariaDB implementa la persistencia JPA. Los DTO REST y las entidades JPA permanecen en sus respectivos adaptadores.

Cada recurso permite crear (`POST`), listar (`GET`), buscar por clave (`GET`), actualizar (`PUT`), eliminar (`DELETE`) y contar (`GET /count`). Estas rutas usan únicamente las columnas del esquema:

| Tabla | Crear / listar / contar | Buscar / actualizar / eliminar |
|---|---|---|
| persona | `/api/v1/tablas/persona` y `/api/v1/tablas/persona/count` | `/api/v1/tablas/persona/{cc}` |
| profesion | `/api/v1/tablas/profesion` y `/api/v1/tablas/profesion/count` | `/api/v1/tablas/profesion/{id}` |
| estudios | `/api/v1/tablas/estudios` y `/api/v1/tablas/estudios/count` | `/api/v1/tablas/estudios/{id_prof}/{cc_per}` |
| telefono | `/api/v1/tablas/telefono` y `/api/v1/tablas/telefono/count` | `/api/v1/tablas/telefono/{num}` |

Los cuerpos JSON usan los nombres de columnas del DDL. `estudios.fecha` usa formato ISO (`YYYY-MM-DD`). Las referencias de `telefono.duenio`, `estudios.cc_per` y `estudios.id_prof` se validan contra sus tablas correspondientes; una referencia inexistente responde `404`. Los intentos de borrar registros que aún estén referenciados respetan las restricciones de la base y responden `409`.

