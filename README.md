# CursoENSI

Prototipo de aplicación Android para tomar un curso por unidades. Cada unidad tiene un video, conceptos clave y un parcial. Al aprobar el parcial (70% o más) se desbloquea la siguiente unidad, y al terminar el curso se puede generar un certificado en PDF.

## Funcionalidades

- Registro e inicio de sesión de usuarios
- Inicio de sesión separado para administradores
- Lista de unidades con bloqueo progresivo
- Video de cada unidad (YouTube en WebView)
- Parcial con preguntas de opción múltiple y calificación
- Generación de certificado en PDF con código de seguridad
- Panel de administración para crear, editar y eliminar unidades

## Tecnologías

- Kotlin
- Android SDK (vistas XML, RecyclerView, WebView)
- iText 7 (generación de PDF)
- Almacenamiento en memoria (sin base de datos)

## Requisitos

- Android Studio (versión reciente)
- JDK 17 o 21 para Gradle
- Android 8.0 (API 26) o superior

## Cómo ejecutarlo

1. Clona el repositorio:

```bash
   git clone https://github.com/Andresdroid2/Curso.git
```

2. Abre la carpeta en Android Studio y espera a que termine el sync de Gradle.
3. Ejecuta la app en un emulador o en un dispositivo físico.

## Cuentas de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@correo.com | admin123 |
| Usuario nuevo | maria@correo.com | maria123 |
| Usuario con progreso | carlos@correo.com | carlos123 |
| Casi terminando | laura@correo.com | laura123 |

El administrador entra desde el botón "Admin" de la pantalla de inicio de sesión.

## Limitaciones

- Los datos viven solo en memoria: al cerrar la app se pierden los usuarios nuevos, unidades creadas y el progreso, y todo vuelve al estado inicial.
- Las contraseñas se guardan en texto plano. Es solo para pruebas y no debe usarse en producción.
- Los enlaces de video de las unidades precargadas son de ejemplo y deben reemplazarse.

## Licencia

Proyecto de uso no comercial. Consulta el archivo `LICENSE` para más detalles.
