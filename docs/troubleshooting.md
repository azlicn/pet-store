# Troubleshooting Guide

Common issues and solutions for Pawfect Store.

---

## Troubleshooting

### Common Issues

1. **Backend fails to start**
   - Ensure MySQL is running and accessible
   - Check database credentials in `application.properties`
   - Verify Java 17+ is installed

2. **Frontend build errors**
   - Run `npm install` to ensure dependencies are installed
   - Check Node.js version (requires 18+)

3. **CORS errors**
   - Backend is configured to allow requests from `http://localhost:4200`
   - If running on different ports, update CORS configuration

4. **Local environment issues**
   - Check application logs for environment loading messages
   - Ensure `.env` file exists in `pet-store-api` directory
   - Verify file format: `KEY=VALUE` with no spaces around `=`
   - Use actuator endpoint: `http://localhost:8080/actuator/env` (admin access required)

5. **Docker environment issues**
   - Check if variables are loaded: `docker exec petstore-backend env`
   - Verify `.env` file exists in docker directory
   - Ensure variable names match exactly in configuration
   - Check database connection: verify `MYSQL_ROOT_PASSWORD` matches in both containers

### Database Reset
```sql
DROP DATABASE petstore_db;
CREATE DATABASE petstore_db;
```

