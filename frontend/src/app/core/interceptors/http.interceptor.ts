import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor HTTP que asegura que todas las peticiones tengan el Content-Type correcto
 * Esto soluciona el problema de "Required request body is missing" en Spring Boot
 */
export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  // Solo agregar Content-Type para peticiones con body (POST, PUT, PATCH)
  if (req.method === 'POST' || req.method === 'PUT' || req.method === 'PATCH') {
    // Si ya tiene Content-Type, no lo sobreescribimos
    if (!req.headers.has('Content-Type') && req.body) {
      const clonedReq = req.clone({
        headers: req.headers.set('Content-Type', 'application/json')
      });
      console.log('Request interceptado:', clonedReq.method, clonedReq.url, clonedReq.body);
      return next(clonedReq);
    }
  }
  
  console.log('Request:', req.method, req.url, req.body);
  return next(req);
};
