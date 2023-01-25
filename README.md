# DGA Sync

Este proyecto es un software creado para sincronizar la información de extracción obtenida desde el software de monitoreo PCWin

## Como compilar

La compilación del proyecto se hace vía Maven ejecutando en la carpeta del proyecto el comando

```shell
mvn clean instal -DskipTests
```

## Uso

### Informacion general

Este proyecto genera un archivo ".jar" que debe ser ejecutado en la misma máquina en la que se encuentra la base de datos 
de PCWin, con el objetivo de usar la conexión de seguridad embebida en Windows, de lo contrario se pueden cambiar las variables
de conexión en [application.properties](src/main/resources/application.properties) (sección DATA SOURCE) para apuntar a la máquina requerida

Tener en cuenta que de ser necesario conectarse a la base de datos usando hostname, usuario y contraseña es necesario configurar
sql server para que acepte dichas conexiones.

### Dependencias

Para ejecutar el software es necesario tener el compilado en forma ".jar" y tener instalado un algún JRE o JDK en la máquina
en la que se ejecutará, yo recomiendo descargar [Amazon Corretto 11](https://corretto.aws/downloads/latest/amazon-corretto-11-x64-windows-jdk.msi),
que es el que usé, pero cualquiera de 11 en adelante debería estar bien.

### Variables

En el archivo [application.properties](src/main/resources/application.properties) hay muchas variables de entorno que se
usan para el correcto funcionamiento de la aplicación, y algunas de ellas es necesario que se sobreescriban en el ambiente
productivo. 

Los nombres de las varibles de entorno a sobreescribir son las que están en SCREAMING_SNAKE_CASE y entre "${" y ":",
por lo que para el ejemplo de querer sobreescribir la variable de tipo de entorno, definida en el application.properties como
"app.environment=${ENVIRONMENT:LOCAL}" se debe crear una variable de nombre "ENVIRONMENT"

#### Varibles de entorno necesarias

```properties
#Define el tipo de ambbiente, hay partes que solo se ejecutan si su valor es PRODUCTION
app.environment=${ENVIRONMENT:LOCAL}
#Rut del usuario en el portal de extracciones efectivas
dga.login.user=${DGA_USER:1-9}
#Contraseña del usuario en el portal de extracciones efectivas
dga.login.password=${DGA_PASSWORD:*****}
#Id del proyecto en AirBrake para manejar notificaciones
airbrake.project.id=${AIRBRAKE_PROJECT_ID:*****}
#Api Key del proyecto en AirBrake para manejar notificaciones
airbrake.project.key=${AIRBRAKE_PROJECT_KEY:*****}
```

#### Varibles de entorno opcionales

```properties
#Valores que forman el string de conexion a BD
db.url=jdbc:sqlserver://${DB_HOSTNAME:MYHOTEL-PC\\MSSQLSERVER}:${DB_PORT:1433};databaseName=${DB_DB_NAME:ScadaNetDb;integratedSecurity=true;}
#Usuario de BD de PCWin (en el archivo están comentado, hay que descomentarlo y recompilar el proyecto si se quiere usar)
db.username=${DB_USERNAME:*****}
#Contraseña de BD de PCWin (en el archivo están comentado, hay que descomentarlo y recompilar el proyecto si se quiere usar)
db.password=${DB_PASSWORD:*****}
#Tamaño de registros a procesar simultaneamente
job.batch.chunk.size=${CHUNK_SIZE:10}
#Nombre del archivo que contiene las configuraciones de las extracciones a registrar en la DGA
properties.filename=${PROPERTIES_FILENAME:dga_sync_properties.json}
#Cantidad de horas que se intentará enviar retroactivamente en caso de que haya habido tiempo sin envíos a la DGA
hours.regression.retry=${HOURS_REGRESSION_RETRY:24}
#Cantidad de horas sin envíos a la DGA antes de que se gatille una alerta
hours.regression.trigger=${HOURS_REGRESSION_TRIGGER:2}
#Cantidad de horas de espera antes de enviar registros a la DGA (para asegurar integridad de la información enviada)
hours.search.offset=${HOURS_OFFSET:1}
#Número de intentos de conexión a la DGa antes de gatillar un error
dga.webservice.max.attempts=${DGA_WEBSERVICE_MAX_ATTEMPTS:3}
#Delay en segundos entre una llamada y otra al WS de la DGA
dga.webservice.seconds.delay=${DGA_WEBSERVICE_SECONDS_DELAY:10}
```

# Ejecución

Para ejecutar el software efectivamente es necesario que se encuentre junto al jar a ejecutarse un archivo llamado 
"dga_sync_properties.json" con la configuración de mapeo entre los datos de el pozo manejados por la DGA y la información
en PCWin. El archivo debe estar en formato json, con tantos objetos como pozos se deban registrar.

EJ:
```json
[
  {
    "numero_estacion": 1,
    "codigo_obra": "EXAMPLE1",
    "numero_informacion_nivel_freatico": 1,
    "numero_informacion_totalizador": 2,
    "numero_informacion_caudal": 3
  },
  {
    "numero_estacion": 2,
    "codigo_obra": "EXAMPLE2",
    "numero_informacion_nivel_freatico": 1,
    "numero_informacion_totalizador": 2,
    "numero_informacion_caudal": 3
  },
  {
    "numero_estacion": 3,
    "codigo_obra": "EXAMPLE3",
    "numero_informacion_nivel_freatico": 1,
    "numero_informacion_totalizador": 2,
    "numero_informacion_caudal": 3
  }
]
```

Finalmente, una vez que se hayan configurado las variables de entorno, se tenga el JDK o JRE instalado y se encuentre el 
archivo de configuración junto al .jar, el comando de ejecución es (usar el nuevo nombre del archivo si es que se cambia):

```shell
    java -jar Sil_Synchronizer-0.1.0.jar
```

## Licencia

[MIT](https://choosealicense.com/licenses/mit/)