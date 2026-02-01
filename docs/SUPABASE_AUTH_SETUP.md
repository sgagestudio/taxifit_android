# Supabase Auth Setup (TaxiFit)

1) Habilitar verificacion de email
- Supabase Dashboard -> Authentication -> Providers -> Email
- Activar **Confirm email**

2) Configurar Redirect URL
- Supabase Dashboard -> Authentication -> URL Configuration
- Agregar: `taxifit://auth-callback`

3) (Opcional) Email templates / SMTP
- Si usas SMTP propio, configurarlo en Authentication -> SMTP
- Personaliza el template para que el enlace abra la app

4) Testing basico
- Registrar usuario
- Recibir email y abrir el enlace
- La app debe capturar el deep link y completar la sesion
