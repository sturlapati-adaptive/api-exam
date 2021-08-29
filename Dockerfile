FROM adoptopenjdk/openjdk16:alpine AS dist
WORKDIR /home/src
COPY ./ ./
RUN ./gradlew installDist
FROM adoptopenjdk/openjdk16:jre as app
RUN mkdir /app
COPY --from=dist /home/src/build/install/api-exam/bin /app/bin
COPY --from=dist /home/src/build/install/api-exam/lib /app/lib
EXPOSE 8080
ENTRYPOINT ["./app/bin/api-exam"]

