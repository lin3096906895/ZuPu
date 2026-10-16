FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# 百度地图 AK 在构建期注入 index.html
ARG VITE_BAIDU_AK
ENV VITE_BAIDU_AK=${VITE_BAIDU_AK}
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
