/******************************************************************************************************************
* File:Plumber.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This class creates a main thread that instantiates and connects a set of filters. It connects all filters
* for system 1
*
* Parameters: 		None
*
* Internal Methods:	main(String argv[])
*
* @author: Ziping
*
******************************************************************************************************************/
package SystemA;
public class Plumber
{
   public static void main(String argv[])
   {
		/****************************************************************************
		* Here we instantiate 5 filters.
		****************************************************************************/

		SourceFilter Source = new SourceFilter();
		SanitizeDataFilter SanitizeData = new SanitizeDataFilter();
		TemperatureConversionFilter TemperatureConversion = new TemperatureConversionFilter();
		AltitudeConversionFilter AltitudeConversion = new AltitudeConversionFilter();
		WriteToFileFilter WriteToFile = new WriteToFileFilter();
		/****************************************************************************
		* Here we connect the filters 
		****************************************************************************/
		WriteToFile.Connect(AltitudeConversion);
		AltitudeConversion.Connect(TemperatureConversion);
		TemperatureConversion.Connect(SanitizeData); 
		SanitizeData.Connect(Source); 

		/****************************************************************************
		* Here we start the filters up. All-in-all,... its really kind of boring.
		****************************************************************************/

		Source.start();
		SanitizeData.start();
		TemperatureConversion.start();
		AltitudeConversion.start();
		WriteToFile.start();

   } // main

} // Plumber