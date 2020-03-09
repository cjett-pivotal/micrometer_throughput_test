Build the project:

`./mvnw clean package`



Run it: 

`./mvnw spring-boot:run`

Watch the console output.  The application will write out how many HTTP hits have occured on the `/greeting` endpoint in the past 10 seconds.

Hit http://localhost:8080/greeting to increment the counter.

Navigate to http://localhost:8080/actuator/prometheus and look for the `custom` gauge to see the counter implemented as a custom prometheus gauge.