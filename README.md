#OpenSky processing Application

Develop application using Scala programming language, SBT and Akka framework.
Use Opensky as datasource, https://opensky-network.org/apidoc/
Ingest, validate and transform input data within application.

Application needs to ingest data, transform it into JSON, calculate results in batch of the data for the particular time window and write transformed data into Kafka.
Calculated results should be available from REST API of the application.

Calculate per time window:
1. airplaine(s) with highest altitude
2. airplaine(s) with highest speed
3. count of airplaines around pre-defined set of airports, within pre-defined range.