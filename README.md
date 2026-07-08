# Productos API

API REST construida con **Spring Boot** para gestionar un catálogo de productos y categorías, con **autenticación JWT**, validaciones, manejo centralizado de errores y una suite de pruebas automatizadas. Es el backend del proyecto fullstack junto a [productos-frontend](https://github.com/vo1dcod3/productos-frontend).

## Tecnologías

- **Java 21** + **Spring Boot 3.5**
- **Spring Web** — API REST
- **Spring Data JPA** + Hibernate — persistencia
- **Spring Security 6** + **JWT** (JJWT 0.12) — autenticación
- **Spring Validation** (Bean Validation) — validación de datos
- **PostgreSQL 16** — base de datos
- **Maven** — build
- **Docker** + Docker Compose — contenedores
- **JUnit 5** + **Mockito** + **MockMvc** + **H2** — testing (42 pruebas)

## Funcionalidades

- **Autenticación JWT**: registro y login, contraseñas cifradas con BCrypt.
- **CRUD de productos** con borrado lógico (los eliminados se marcan inactivos, no se pierden).
- **CRUD de categorías** como entidad propia, relacionada con productos (`@ManyToOne`).
- **DTOs** para separar el modelo de datos de la API.
- **Validación** de entrada con Bean Validation.
- **Manejo global de excepciones** con códigos HTTP semánticos (404, 409, 401, 400).
- **Seguridad stateless**: todas las rutas de negocio requieren token; `/api/auth/**` es público.
- **CORS** configurado a nivel de `SecurityFilterChain`.

## Arquitectura

Arquitectura en capas, organizada por dominio:

```
src/main/java/cl/manuel/productosapi/
├── auth/          → registro, login, JWT (controller, service, model, dto, repository)
├── categoria/     → CRUD de categorías (controller, service, model, dto, repository)
├── controller/    → ProductoController
├── service/       → ProductoService
├── repository/    → ProductoRepository
├── model/         → entidad Producto
├── dto/           → ProductoRequestDTO / ProductoResponseDTO
├── exception/     → excepciones personalizadas + GlobalExceptionHandler
└── security/      → JwtService, JwtAuthFilter, SecurityConfig
```

Flujo: **Controller** (HTTP) → **Service** (lógica de negocio) → **Repository** (persistencia). Los controladores nunca contienen lógica de negocio.

## Endpoints

### Autenticación (públicos)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/registro` | Registrar usuario y obtener token |
| POST | `/api/auth/login` | Iniciar sesión y obtener token |

### Productos (requieren token JWT)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/productos` | Listar productos activos |
| GET | `/api/productos/{id}` | Obtener producto por id |
| GET | `/api/productos/buscar?categoria=...` | Filtrar por nombre de categoría |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Borrado lógico |

### Categorías (requieren token JWT)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/categorias` | Listar categorías activas |
| POST | `/api/categorias` | Crear categoría |
| PUT | `/api/categorias/{id}` | Actualizar categoría |
| DELETE | `/api/categorias/{id}` | Borrado lógico |

### Códigos de error

El manejo global de excepciones devuelve códigos HTTP semánticos:

| Código | Situación |
|--------|-----------|
| 400 | Datos inválidos (validación) |
| 401 | Credenciales inválidas en login |
| 404 | Recurso no encontrado |
| 409 | Conflicto (nombre/email ya existe) |

## Cómo ejecutar

### Opción 1 — Docker Compose (recomendado)

Desde la raíz de `productos-api`, levanta PostgreSQL + backend + frontend con un solo comando:

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- Frontend: `http://localhost:4200`

### Opción 2 — Local (para desarrollo)

**Requisitos:** Java 21, Maven (incluido el wrapper `./mvnw`), Docker.

1. Levantar PostgreSQL:
   ```bash
   docker run --name productos-db \
     -e POSTGRES_USER=admin \
     -e POSTGRES_PASSWORD=admin123 \
     -e POSTGRES_DB=productosdb \
     -p 5432:5432 -d postgres:16-alpine
   ```
2. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

## Ejemplo de uso

```bash
# 1. Registrarse y obtener el token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@test.cl","password":"123456"}' | jq -r .token)

# 2. Crear una categoría (con el token)
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"nombre":"Periféricos"}'

# 3. Crear un producto en esa categoría (categoriaId = 1)
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"nombre":"Teclado","categoriaId":1,"precio":25000,"stock":10}'
```

## Testing

42 pruebas automatizadas: unitarias de servicios (Mockito), de la capa web (MockMvc) y de integración de repositorios (H2 en memoria).

```bash
./mvnw test
```

## Variables de entorno

En Docker se configuran vía `docker-compose.yml`; en local usan los valores por defecto de `application.properties`.

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Credenciales de la base |
| `JWT_SECRET` | Clave para firmar los tokens |
| `JWT_EXPIRATION` | Vigencia del token en milisegundos |

## Autor

Manuel Fuentealba — [github.com/vo1dcod3](https://github.com/vo1dcod3)
