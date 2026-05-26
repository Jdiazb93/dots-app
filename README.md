# MindShift Anxiety Tracker

Aplicación móvil para registrar niveles de ansiedad mediante clicks, con backend en Node.js + NestJS y base de datos MongoDB.

---

# Estructura del proyecto

/mobile → Aplicación Android (Kotlin + Jetpack Compose)
/server → Backend API (Node.js + NestJS + Docker Compose)

---

# Requisitos

- Node.js 18+
- npm
- Android Studio
- Java 17+
- Docker y Docker Compose

---

# BASE DE DATOS (MongoDB con Docker)

La base de datos se levanta desde la carpeta /server.

## Levantar MongoDB

Entrar a la carpeta del backend:

cd server

Ejecutar Docker Compose:

```Bash
docker-compose up -d
```

MongoDB quedará disponible en:

```Bash
mongodb://localhost:27017
```

---

# BACKEND SETUP

## 1. Entrar al backend

```Bash
cd server
```

## 2. Instalar dependencias

```Bash
npm install
```

## 3. Variables de entorno (.env en /server)

```Bash
MONGO_URI=mongodb://admin:admin123@localhost:27017/anxiety-db?authSource=admin
```

## 4. Ejecutar backend

```Bash
npm run start:dev
```

Backend disponible en:

```Bash
http://localhost:3000
```

---

# MOBILE SETUP (ANDROID)

## 1. Abrir proyecto

Abrir carpeta /mobile en Android Studio

## 2. Sincronizar Gradle

Esperar sincronización completa

## 3. Configurar API (Retrofit)

Emulador Android:

```Bash
http://10.0.2.2:3000
```

Dispositivo físico:

```Bash
http://TU_IP_LOCAL:3000
```

Ejemplo:

```Bash
http://192.168.1.10:3000
```

## 4. Ejecutar aplicación

Run desde Android Studio

---

# DOCKER (SERVER)

El backend incluye docker-compose.yml dentro de /server para levantar MongoDB.

Comando:

```Bash
docker-compose up -d
```

Detener:

```Bash
docker-compose down
```

---

# ENDPOINTS

### GET /anxiety/:user
Obtiene registros de ansiedad del usuario

### POST /anxiety
Guarda registros diarios de clicks de ansiedad

---

# FUNCIONALIDADES

- Registro de clicks de ansiedad
- Envío automático al backend
- Agrupación de datos por día
- Visualización de últimos 7 días
- Gráfico de barras dinámico
- Cálculo de total y promedio semanal

---

# FLUJO DE LA APP

1. Usuario presiona “Tengo ansiedad”
2. Se incrementan clicks localmente
3. Al llegar a 100 o timeout:
   Se envía POST al backend
4. Backend guarda en MongoDB
5. Mobile consulta últimos 7 días
6. Se actualiza gráfico semanal

---

# NOTAS IMPORTANTES

- Backend debe estar corriendo antes de abrir la app
- MongoDB se levanta desde /server con Docker Compose
- Emulador Android usa 10.0.2.2
- Dispositivo físico usa IP local del computador
- Verificar que Docker esté corriendo antes de iniciar backend
