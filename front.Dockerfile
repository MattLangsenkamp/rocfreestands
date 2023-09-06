FROM nginx:stable-alpine
COPY dist/index.html /usr/share/nginx/html
RUN mkdir "usr/share/nginx/html/assets"
COPY dist/assets/* usr/share/nginx/html/assets
COPY assets/* usr/share/nginx/html/assets
COPY front-nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8079
CMD ["nginx", "-g", "daemon off;"]