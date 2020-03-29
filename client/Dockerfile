FROM nginx:stable

COPY resources/public /usr/share/nginx/html
COPY build/js/ /usr/share/nginx/html/js

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
