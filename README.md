# personapp-hexa-spring-boot
Plantilla Laboratorio Arquitectura Limpia

Instalar MariaDB en puerto 3307
Instalar MongoDB en puerto 27017

Ejecutar los scripts en las dbs

el adaptador rest corre en el puerto 3000
el swagger en http://localhost:3000/swagger-ui.html

Son dos adaptadores de entrada, 2 SpringApplication diferentes

Deben configurar el lombok en sus IDEs

## API REST de personas

La API permite seleccionar `MARIA`/`MARIADB` o `MONGO`/`MONGODB` para las operaciones
de lectura, actualización y eliminación:

| Operación | Método y ruta |
| --- | --- |
| Crear persona | `POST /api/v1/persona` |
| Listar personas | `GET /api/v1/persona/{database}` |
| Buscar por identificación | `GET /api/v1/persona/{database}/{dni}` |
| Contar personas | `GET /api/v1/persona/{database}/count` |
| Actualizar persona | `PUT /api/v1/persona/{database}/{dni}` |
| Eliminar persona | `DELETE /api/v1/persona/{database}/{dni}` |

El cuerpo de creación y actualización usa JSON con `dni`, `firstName`, `lastName`,
`age`, `sex` y `database`. `age` puede omitirse o ser `null`; `sex` debe ser `M` o
`F` (también se aceptan `MALE` y `FEMALE`). Para actualizar, la identificación de
la ruta es la que se usa, aunque el cuerpo incluya otro valor en `dni`.

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

## CRUD del esquema relacional MariaDB

Las operaciones para las tablas `persona`, `profesion`, `estudios` y `telefono`
se exponen bajo `/api/v1/tablas`. Cada recurso permite crear (`POST`), listar
(`GET`), buscar por clave (`GET`), actualizar (`PUT`), eliminar (`DELETE`) y
contar (`GET /count`). Estas rutas usan únicamente las columnas del esquema:

| Tabla | Crear / listar / contar | Buscar / actualizar / eliminar |
| --- | --- | --- |
| `persona` | `/api/v1/tablas/persona` y `/api/v1/tablas/persona/count` | `/api/v1/tablas/persona/{cc}` |
| `profesion` | `/api/v1/tablas/profesion` y `/api/v1/tablas/profesion/count` | `/api/v1/tablas/profesion/{id}` |
| `estudios` | `/api/v1/tablas/estudios` y `/api/v1/tablas/estudios/count` | `/api/v1/tablas/estudios/{id_prof}/{cc_per}` |
| `telefono` | `/api/v1/tablas/telefono` y `/api/v1/tablas/telefono/count` | `/api/v1/tablas/telefono/{num}` |

Los cuerpos JSON usan los nombres de columnas del DDL. `estudios.fecha` usa
formato ISO (`YYYY-MM-DD`). Las referencias de `telefono.duenio`, `estudios.cc_per`
y `estudios.id_prof` se validan contra sus tablas correspondientes; una referencia
inexistente responde `404`. Los intentos de borrar registros que aún estén
referenciados respetan las restricciones de la base y responden `409`.