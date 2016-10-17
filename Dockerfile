FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/greenhouse-web.jar /greenhouse-web/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/greenhouse-web/app.jar"]
