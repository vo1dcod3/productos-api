# Productos API

API REST desarrollada con Spring Boot para gestionar un catálogo de productos.

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA + Hibernate
- PostgreSQL 16
- Maven
- Docker + Docker Compose

## Funcionalidades

- CRUD completo de productos
- Borrado lógico (los productos eliminados no se pierden)
- DTOs para separar la capa de datos de la API
- Validaciones con Bean Validation
- Manejo global de excepciones
- Endpoint de categorías únicas desde la BD
- CORS configurado globalmente

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/productos` | Listar productos activos |
| GET | `/api/productos/{id}` | Obtener producto por id |
| GET | `/api/productos/buscar?categoria=...` | Filtrar por categoría |
| GET | `/api/productos/categorias` | Listar categorías únicas activas |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Borrado lógico |

## Cómo ejecutar

### Opción 1 — Docker Compose (recomendado)

Levanta PostgreSQL + Spring Boot con un solo comando:

```bash
docker compose up
```

La API queda disponible en `http://localhost:8080`

### Opción 2 — Local

#### Requisitos
- Java 21
- Maven
- Docker

#### 1. Levantar PostgreSQL

```bash
docker run --name postgres-fullstack \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -e POSTGRES_DB=productosdb \
  -p 5432:5432 \
  -d postgres:16
```

#### 2. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

## Ejemplo de uso

**Crear producto:**
```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Laptop Dell", "categoria": "electronica", "precio": 899990.0, "stock": 15}'
```

**Listar productos:**
```bash
curl http://localhost:8080/api/productos
```

**Listar categorías:**
```bash
curl http://localhost:8080/api/productos/categorias
```
