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

package SystemC;
public class Plumber
{
   public static void main( String argv[])
   {
		/****************************************************************************
		* Here we instantiate the filters.
		****************************************************************************/
	   
		MergeFilter Source = new MergeFilter();
		LessThan10KFilter Less = new LessThan10KFilter();
		WriteToFileFilter LessSink = new WriteToFileFilter("LessThan10K.dat");
		WildValueExtrapolationFilter Extrapolation = new WildValueExtrapolationFilter(6);
		WriteToFileFilter WriteToFile = new WriteToFileFilter("OutputC.dat");
		WriteWildPointsToFileFilter WPSink = new WriteWildPointsToFileFilter();

		/****************************************************************************
		* Here we connect the filters 
		****************************************************************************/

		WPSink.Connect(Extrapolation, 2);
		WriteToFile.Connect(Extrapolation, 1);
		Extrapolation.Connect(Less);
		LessSink.Connect(Less, 2);
		Less.Connect(Source);

		/****************************************************************************
		* Here we start the filters up. All-in-all,... its really kind of boring.
		****************************************************************************/

		Source.start();
		Less.start();
		LessSink.start();
		Extrapolation.start();
		WriteToFile.start();
		WPSink.start();
   }
}