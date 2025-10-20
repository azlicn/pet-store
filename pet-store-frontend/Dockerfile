# Build stage
FROM node:18-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
# RUN npm run build
# Use Docker-specific environment
RUN npm run build -- --configuration docker

# Production stage
FROM nginx:alpine

COPY --from=build /app/dist/pet-store-frontend/browser /usr/share/nginx/html/browser

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]