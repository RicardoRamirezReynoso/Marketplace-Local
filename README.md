# MarketPlace Local App

**MarketPlace Local App** es una aplicación de Android construida con Kotlin y Jetpack Compose, diseñada para la compra y venta de productos en comunidades cercanas.

## Demostración

<table>
  <tr>
    <td><b>Registro</b></td>
    <td><b>Muro Principal</b></td>
    <td><b>Añadir Producto</b></td>
    <td><b>Detalle del Producto</b></td>
  </tr>
  <tr>
    <td><img width="300" alt="Screenshot_20260324_203126" src="https://github.com/user-attachments/assets/e067e2e0-e7dc-49cc-ab64-4983b2023133" /></td>
    <td><img width="300" alt="Screenshot_20260324_203232" src="https://github.com/user-attachments/assets/2e6c8962-f698-42d6-9190-24e06a346ed4" /></td>
    <td><img width="300" alt="Screenshot_20260324_203306" src="https://github.com/user-attachments/assets/442f71bb-926d-4129-b71b-e52caf19dbec" /></td>
    <td><img width="300" aalt="Screenshot_20260324_203351" src="https://github.com/user-attachments/assets/7bf07f44-29e6-4a0f-8367-c60831a4be1a" /></td>
  </tr>
</table>


## Funcionalidades Implementadas

-   **Autenticación:** Flujo completo de inicio de sesión y registro utilizando Firebase Auth.
-   **Gestión de Productos:** Lectura dinámica desde Firestore, permitiendo actualizaciones en tiempo real de la oferta local, visualización de productos en un Grid de 2 columnas con búsqueda funcional y reactiva por nombre o descripción.
-   **Publicación de Artículos:** Formulario completo: nombre, precio, descripción y lugar de entrega utilizando Firestore. Multi-Imagen, carga de hasta 5 fotos por producto con Firebase Storage. Selector para indicar si el artículo es "Nuevo" o "Usado".
-   **Detalle del Producto:** Vista extendida con carrusel de imágenes (HorizontalPager), información técnica y contacto directo con el vendedor vía email.
-   **Sistema de Favoritos:** Guardado de productos favoritos en Firestore vinculado a la cuenta del usuario.
-   **Gestión de Límites:** Implementación que maneja el límite técnico de 30 favoritos, notificando al usuario mediante un Snackbar si se alcanza el máximo
-   **Pruebas Unitarias y de UI:** El proyecto incluye pruebas para garantizar la robustez del código
   
### Componentes Principales:

*   **Lenguaje:** Kotlin
*   **UI:** Jetpack Compose para toda la interfaz de usuario con Material 3
*   **Arquitectura:** MVVM (Model-View-ViewModel), separación entre lógica (Stateful) y representación visual (Stateless),  facilitando el testing y Previews dinámicas
*   **Inyección de Dependencias:** Hilt para gestionar las dependencias en toda la aplicación
*   **Backend & Monitoreo:**  Firebase (Auth, Firestore, Storage y Crashlytics para reporte de errores en tiempo real)
*   **Carga de Imágenes:** [Coil] 
*   **Testing:** Stack completo con JUnit4, Compose Test para la UI y Hilt Test Support.

## Próximos Pasos
1.  **Recuperación de cuenta:** Implementar logica para el olvido de contraseña 
2.  **Social Sharing:** Integrar Android Sharesheet (Hoja de Compartir), para facilitar el contacto con el vendedor
