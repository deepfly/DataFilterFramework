# Datafilter
This is a data filter framework built in pipe and filter pattern. Three examples are provided in the repository

Instructions on how to run System A:

1. cd into the base SystemA directory (the one with all the .java files).
2. You should see the following files in the directory:
	- AltitudeConversionFilter.java
	- Connections.java
	- Plumber.java
	- SanitizeDataFilter.java
	- SingleOutputFilter.java
	- SourceFilter.java
	- TemperatureConversionFilter.java
	- WriteToFileFilter.java
	- AltitudeConversionFilter.class
	- Connections.class
	- Plumber.class
	- SanitizeDataFilter.class
	- SingleOutputFilter.class
	- SourceFilter.class
	- TemperatureConversionFilter.class
	- WriteToFileFilter.class
3. cd into the outer SystemA directory (the one with FlightData.dat).
4. You should see the following files in the directory:
	- SystemA (folder)
	- FlightData.dat
5. Run the system by executing the following command on the command line:
	$ java -classpath . SystemA.Plumber
6. Your output will be produced in the current directory under the name of "OutputA.dat"

Instructions on how to run System B:

1. cd into the base SystemB directory (the one with all the .java files).
2. You should see the following files in the directory:
	- AltitudeConversionFilter.java
	- Connections.java
	- Plumber.java
	- SanitizeDataFilter.java
	- SingleOutputFilter.java
	- SourceFilter.java
	- TemperatureConversionFilter.java
	- WriteToFileFilter.java
	- MultipleOutputFilter.java
	- WildValueExtrapolationFilter.java
	- WriteWildPointsToFileFilter.java
	- AltitudeConversionFilter.class
	- Connections.class
	- Plumber.class
	- SanitizeDataFilter.class
	- SingleOutputFilter.class
	- SourceFilter.class
	- TemperatureConversionFilter.class
	- WriteToFileFilter.class
	- MultipleOutputFilter.class
	- WildValueExtrapolationFilter.class
	- WriteWildPointsToFileFilter.class
3. cd into the outer SystemB directory (the one with FlightData.dat).
4. You should see the following files in the directory:
	- SystemB (folder)
	- FlightData.dat
5. Run the system by executing the following command on the command line:
	$ java -classpath . SystemB.Plumber
6. Your output will be produced in the current directory under the name of "OutputB.dat"

Instructions on how to run System C:

1. cd into the base SystemC directory (the one with all the .java files).
2. You should see the following files in the directory:
	- MergeFilter.java
	- Connections.java
	- LessThan10KFilter.java
	- Plumber.java
	- MultipleOutputFilter.java
	- SingleOutputFilter.java
	- WildValueExtrapolationFilter.java
	- WriteToFileFilter.java
	- WriteWildPointsToFileFilter.java
	- MergeFilter.class
	- Connections.class
	- LessThan10KFilter.class
	- Plumber.class
	- MultipleOutputFilter.class
	- SingleOutputFilter.class
	- WildValueExtrapolationFilter.class
	- WriteToFileFilter.class
	- WriteWildPointsToFileFilter.class
3. cd into the outer SystemC directory (the one with SubSetA.dat and SubSetB.dat).
4. You should see the following files in the directory:
	- SystemC (folder)
	- SubSetA.dat
	- SubSetB.dat
5. Run the system by executing the following command on the command line:
	$ java -classpath . SystemC.Plumber
6. Your output will be produced in the current directory under the name of "OutputC.dat"
