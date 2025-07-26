📚 Documentación de Microservicios (Products & Inventory)

[![codecov](https://codecov.io/github/kisashin/Prueba-Tecnica/graph/badge.svg?token=OMUUDMOGQY)](https://codecov.io/github/kisashin/Prueba-Tecnica)

Este proyecto comprende dos microservicios desarrollados con Spring Boot que interactúan entre sí mediante REST APIs con JSON. La documentación está elaborada para facilitar la instalación, ejecución y comprensión de la arquitectura implementada.

🚀 1. Instrucciones de instalación y ejecución

Prerrequisitos:
•	Java 17 o superior
•	Docker y Docker Compose
•	Maven

Instalación:
1.	Clona el repositorio:
git clone https://github.com/kisashin/Prueba-Tecnica
2.	Navega al directorio del proyecto:
cd products-inventory
3.	Compila los microservicios:
mvn clean install
4.	Ejecuta los contenedores Docker:
docker-compose up

Los microservicios estarán disponibles en:
•	Products Service: http://localhost:8080
•	Inventory Service: http://localhost:8081

🏗️ 2. Descripción de la arquitectura

La solución implementa una arquitectura basada en microservicios independientes, comunicados a través de APIs REST. Cada microservicio mantiene su propia base de datos aislada para facilitar la escalabilidad, resiliencia y mantenimiento. Las peticiones entre servicios son gestionadas mediante RestTemplate, con validaciones de existencia de recursos en servicios externos.

Componentes principales:
•	Products Service: Gestiona información básica de productos.
•	Inventory Service: Gestiona inventario de productos y maneja operaciones de compra y actualización de cantidades.

Ambos servicios tienen autenticación básica usando claves API.

📐 3. Decisiones técnicas y justificaciones
•	Framework: Spring Boot por su rápida implementación y soporte robusto para desarrollo basado en microservicios.
•	Bases de datos independientes: Cada servicio usa su propia base de datos (PostgreSQL) para desacoplar servicios y garantizar independencia operativa.
•	Docker: Para facilitar el despliegue y asegurar consistencia en distintos entornos.
•	Endpoint de compra: Implementado en el servicio de inventario (Inventory Service) para mantener la lógica relacionada con cambios de inventario encapsulada en un solo microservicio, evitando dependencias circulares.

📊 4. Diagrama de interacción entre servicios
[Cliente] ---> [Inventory Service] ---Validación producto---> [Products Service]
                 |
                 |-----> [Base de datos Inventario]

### 📦 Colección Postman

La colección de endpoints está disponible en [`docs/postman/Prueba-Collection.postman_collection.json`](docs/postman/Prueba-Collection.postman_collection.json)

### 🖼️ Diagrama de interacción entre servicios

![Diagrama](docs/diagramas/Diagrama de Interacción Entre Servicios.png)


🔄 5. Explicación del flujo de compra implementado

Cuando se recibe una solicitud de compra:
1.	El servicio Inventory Service verifica mediante llamada REST si el producto existe en Products Service.
2.	Se valida la existencia y cantidad suficiente en inventario.
3.	Se actualiza la cantidad disponible del producto en la base de datos del servicio de inventario.
4.	Se retorna una respuesta al cliente indicando el resultado de la operación.

Este flujo garantiza la integridad de los datos y la sincronización precisa entre los servicios involucrados.

🛠️ Documentación de Endpoints

Products Service:

Método	    Endpoint	        Descripción
GET	        /products/{id}	    Obtener producto por ID
POST	    /products	        Crear nuevo producto
PUT	        /products/{id}	    Actualizar producto por ID
DELETE	    /products/{id}	    Eliminar producto por ID

Inventory Service:

Método	     Endpoint	                Descripción
GET	         /inventory/{productId}	    Obtener inventario del producto por ID
POST	     /inventory	                Crear inventario para un nuevo producto
PUT	         /inventory/{productId}	    Actualizar cantidad de inventario por producto
POST	     /inventory/purchase	    Realizar compra de producto

🧠 6. Herramientas de IA utilizadas

Herramienta         	Tarea	                                                            Verificación
ChatGPT	                Documentación técnica y validación de estructura del proyecto	    Revisión manual y validación de contenido

Únicamente se utilizaron herramientas de IA para generar documentación técnica y validar la claridad y completitud del proyecto. Todo el código y las pruebas fueron implementados manualmente.

🧪 7. Pruebas Unitarias y Cobertura

Se implementaron pruebas unitarias exhaustivas utilizando JUnit y Mockito, asegurando la robustez y calidad del código.

Cobertura obtenida con JaCoCo:

Products Service: Cobertura total del 81%

Inventory Service: Cobertura total del 72%

Cómo verificar resultados:

Ejecuta los comandos:

mvn test

• Revisa los resultados generados por JaCoCo en la carpeta:

target/site/jacoco/index.html

Escenarios probados:

• Products Service: creación, actualización y eliminación de productos, manejo de errores de productos no encontrados.

• Inventory Service: creación de inventario, actualización, proceso de compra, validación de productos existentes en otro microservicio, manejo de errores de inventario insuficiente y producto no encontrado

