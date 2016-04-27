/******************************************************************************************************************
* File:WriteToFileFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This is a filter to write data to a preset file location.

* 
* @author: Ziping Zheng
*
******************************************************************************************************************/
package SystemA;

import java.util.*;						// This class is used to interpret time words
import java.io.*;
import java.text.SimpleDateFormat;		// This class is used to format and write time in a string format.

public class WriteToFileFilter extends SingleOutputFilter
{
	public void run()
    {
		/************************************************************************************
		*	TimeStamp is used to compute time using java.util's Calendar class.
		* 	TimeStampFormat is used to format the time value so that it can be easily printed
		*	to the terminal.
		*************************************************************************************/

		Calendar TimeStamp = Calendar.getInstance();
		SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

		int bytesread = 0;				// This is the number of bytes read from the stream

		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		int id;							// This is the measurement id
		String altitudeFrameBuffer = "";
		/*************************************************************
		*	First we announce to the world that we are alive...
		**************************************************************/

		System.out.println(this.getName() + "::WriteToFile Reading ");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("OutputA.dat"));
			// Creating a header for Easy readibility
			out.write("Time\t\t    Temperature(C)     Altitude(m)");
			out.newLine();
			out.write("---------------------------------------------------------");
			while (true)
			{
				try
				{
					id = getIDFromFilterInput();

					measurement = getMeasurementFromFilterInput();
					bytesread+=12;

					// Case: Time
					if (id == TIME)
					{
						out.newLine();
						TimeStamp.setTimeInMillis(measurement);
						out.write(TimeStampFormat.format(TimeStamp.getTime())+"\t\t");
					} 
					// Case: Altitude
					else if (id == ALTITUDE){
						if(Double.longBitsToDouble(measurement) > 0){
							// Append zeros at the beginning of positive output
							altitudeFrameBuffer = String.format("%11s", String.format("%5.5f",Double.longBitsToDouble(measurement))).replace(' ', '0');
						}else{
							// Don't append zeros since the output is negative
							altitudeFrameBuffer = String.format("%11s", String.format("%5.5f",Double.longBitsToDouble(measurement)));
						}
					}
					// Case: Temperature
					else if (id == TEMPERATURE){
						if(Double.longBitsToDouble(measurement) > 0){
							// Append zeros at the beginning of positive output
							out.write(String.format("%9s", String.format("%.5f",Double.longBitsToDouble(measurement)).replace(' ', '0'))+"\t\t\t");
						}else{
							// Don't append zeros since the output is negative
							out.write(String.format("%9s", String.format("%.5f",Double.longBitsToDouble(measurement)))+"\t\t\t");
						}

						// Append the created buffer at the end of each line
						out.write(altitudeFrameBuffer);
					}

				}
				catch (EndOfStreamException e)
				{
					ClosePorts();
					out.close();
					System.out.println(this.getName() + "::WriteToFile Exiting; Total bytes read: " + bytesread );
					break;
				} 

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
}