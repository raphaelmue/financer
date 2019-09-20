FROM openjdk:11-oracle

EXPOSE 3000
COPY de.raphaelmuesseler.financer.server/target/financer-server.jar financer-server.jar
CMD java -jar financer-server.jar --database=prod
