# Taller diseño y estructuración de aplicaciones distribuidas en internet

## Introducción

En este taller se trabajará en el diseño y la implementación de la arquitectura de aplicaciones distribuidas, 
enfocándose en los servidores web y el protocolo HTTP.
Se desarrollará una aplicación de gestión de tareas que permitirá agregar y buscar tareas. Para ello:

* Se implementará un Servidor Web en Java, con soporte para manejar múltiples solicitudes de forma no concurrente.

* El servidor será responsable de servir los archivos estáticos almacenados en disco, necesarios para cargar correctamente
la aplicación.

* Se incluirá la comunicación asíncrona mediante los métodos HTTP `GET` y `POST` donde el metodo **GET** se encarga de
devolver la lista de tareas basado en el filtro de busqueda y el método **POST** permite crear una nueva tarea y retornar
la nueva información de la tarea generada.

De esta manera, el laboratorio introduce los fundamentos del funcionamiento de un servidor web, la manipulación de 
solicitudes HTTP y la integración entre cliente y servidor. 

## Arquitectura
A continuación, se ilustra el diagrama de clases que refleja como está construido el servidor web

<img src="ReadmeImages/ClasesTaller1AREP.png">

## Funcionamiento

Al correr el servidor, este abre un socket del cliente por el puerto 35000 y espera a recibir solicitudes.
Al entrar al navegador, se realiza un solicitud GET HTTP solicitando por el recurso index.html
el navegador mismo se encarga de hacer las peticiones necesarias para cargar los css y el javascript
que se especifican en el archivo html. Los archivos solicitados se encuentran almacenados en 
`src\main\resources`

<img src="ReadmeImages/FlujoTaller1AREP.png">

### Metodos asincronicos
* **POST**

    Para agregar una tarea, se llena el formulario que se encuentra a la izquierda y al dar click en el boton
    'add' el cliente se encarga de llamar al recurso `http:/localhost:35000/app/tasks` y en el cuerpo del mensaje,
    se envía un JSON con el nombre y descripción de la tarea, la siguiente imagen es un ejemplo de cuando se crea la tarea:
    
    <img src="ReadmeImages/img_2.png">
    
    Como observamos anteriormente, al agregar la tarea el servidor se encarga de retornar la información de la nueva tarea, junto
    con el header application/json para indicar que retorna un JSON y el codigo de respuesta 200 OK. Finalmente 
    se muestra en la aplicación la información de la ultima tarea añadida.
* **GET**

    Para obtener las tareas por nombre, se da click en el botón "search" ubicado junto a la barra de busqueda,
    se hace un petición GET al servidor `http://localhost:35000/app/tasks?name="+value` donde value es el valor del input de busqueda.
    El servidor se encargar de obtener el parametro y filtrar las tareas cuyo nombre contiene ese valor. Finalmente,
    el servidor retorna la lista de tareas junto con el encabezado application/json y el codigo de respuesta 200 OK.
    La siguiente imagen es un ejemplo para obtener las tareas con nombre "AREP":
    <img src="ReadmeImages/img_3.png">
    Para obtener todas las tareas basta con limpiar el input de la busqueda y volver a dar click para buscar:
    <img src="ReadmeImages/img_4.png">

* **Codigos de Respuesta**

    El servidor maneja varios codigos de respuesta según la acción que se realice:
    * 200 OK: Cuando el recurso finaliza correctamente su tarea.
    * 404 Not Found: Se maneja cuando no existe el archivo especificado.
    * 405 Method Not Allowed: Cuando el recurso solicitado no se encontró.
    * 400 Bad Request: Cuando el parametro o cuerpo enviados no son validos o están incompletos. 
    * 500 Internal Server Error: Cuando ocurrió una excepción no controlada.
  Ejemplo cuando se llama a un recurso que no está guardado en disco:
    <img src="ReadmeImages/img_5.png">
  Ejemplo de cuando se llama a un metodo que no existe
    <img src="ReadmeImages/img_6.png">


## Primeros Pasos

### Prerrequisitos

Antes de comenzar, es necesario tener instalado lo siguiente en el entorno:

* **Java Development Kit (JDK) 17 o superior**

    Verifica la versión de java

    ```
    java -version
    ```
* **Maven**

  Facilita la compilación y depuración del proyecto

* **Git**

  Control de versiones

### Instalación

1. Clonar el repositorio
    ```
    git clone https://github.com/CamilaTorres08/Taller1_AREP.git
    cd Taller1_AREP
    ```
2. Compilar el proyecto
    ```
    mvn clean install
    ```
3. Ejecutar el servidor
    ```
    mvn exec:java
    ```
   O directamente en la IDE dando click en *Run* sobre el archivo 

    `Taller1ArepApplication`

4. Abrir la aplicación en el navegador
    ```
    http://localhost:35000
    ```

## Pruebas

Este taller incluye pruebas automatizadas usando JUnit para validar el funcionamiento del servidor web.
El archivo de pruebas se encuentra en: 

`src\test\java\edu\eci\arep\webserver\taller1_arep\HttpServerTests.java`

### Ejecución de pruebas

* **Usando Maven**

    Ejecute todas las pruebas con el siguiente comando:
    ```
    mvn test
    ```
* **Usando la IDE**
    
    Abra el archivo `HttpServerTests` y ejecute directamente las pruebas con el botón *Run Test*

Estas pruebas se encargan de levantar el servidor en el puerto 35001 utilizando hilos para iniciarlo y finalizarlo automáticamente al terminar la ejecución.
El objetivo principal es validar que los archivos esten retornando correctamente,
las tareas puedan consultarse y crearse mediante los métodos GET y POST y lograr que el servidor maneje adecuadamente los códigos de error HTTP en distintos escenarios.

De esta manera, se asegura el correcto funcionamiento del servidor, abarcando tanto los casos de uso comunes como los de error.

<img src="ReadmeImages/img.png">

## Despliegue

Este proyecto está previsto para ejecutarse localmente con fines de desarrollo y pruebas.

## Tecnologías utilizadas

* [Java 21](https://openjdk.org/projects/jdk/21/) - Lenguaje de Programación
* [Maven](https://maven.apache.org/) - Compilaciones y dependencias
* [JUnit](https://junit.org/) - Framework de testeo

## Versionamiento

Actualmente se encuentra en desarrollo y se usa la versión por defecto `0.0.1-SNAPSHOT`.

## Autores

* **Andrea Camila Torres González** 

## Licencia

Este proyecto no cuenta actualmente con una licencia específica.  
Su uso está restringido únicamente a fines académicos.


