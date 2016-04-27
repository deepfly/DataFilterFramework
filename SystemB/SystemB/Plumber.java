/******************************************************************************************************************
* File:Plumber.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This class creates a main thread that instantiates and connects a set of filters.
*
* Internal Methods:	public static void main( String argv[])
*
*
* @author: Ziping Zheng
*
******************************************************************************************************************/

package SystemB;
public class Plumber
{
   public static void main( String argv[])
   {
		/****************************************************************************
		* Here we instantiate the filters.
		****************************************************************************/

		SourceFilter Source = new SourceFilter();
		SanitizeDataFilter SanitizeData = new SanitizeDataFilter();
		TemperatureConversionFilter TemperatureConversion = new TemperatureConversionFilter();
		AltitudeConversionFilter AltitudeConversion = new AltitudeConversionFilter();
		WildValueExtrapolationFilter WildValueExtrapolation = new WildValueExtrapolationFilter();
		WriteWildPointsToFileFilter WriteWildPointsToFile = new WriteWildPointsToFileFilter();
		WriteToFileFilter WriteToFile = new WriteToFileFilter();

		/****************************************************************************
		* Here we connect the filters 
		****************************************************************************/

		WriteWildPointsToFile.Connect(WildValueExtrapolation, 2);
		WriteToFile.Connect(WildValueExtrapolation);
		WildValueExtrapolation.Connect(AltitudeConversion);
		AltitudeConversion.Connect(TemperatureConversion);
		TemperatureConversion.Connect(SanitizeData);
		SanitizeData.Connect(Source);
//		WriteToFile.Connect(Source);

		/****************************************************************************
		* Here we start the filters up. All-in-all,... its really kind of boring.
		****************************************************************************/

		Source.start();
		SanitizeData.start();
		TemperatureConversion.start();
		AltitudeConversion.start();
		WildValueExtrapolation.start();
		WriteWildPointsToFile.start();
		WriteToFile.start();
   }
}