/******************************************************************************************************************
* File:WriteToFileFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This class will create a filter to write to predefined file (OutputC.dat). It prints a header, and then will  
* print the formatted values from the datastream. 
* Currently prints Time, Temperature, Pressure, Attitude and Altitude.
*
* Parameters: 	None
* 
* @author: Ziping Zheng
*
******************************************************************************************************************/
package SystemC;

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class WriteToFileFilter extends SingleOutputFilter
{
	public String fileName = new String();
	public WriteToFileFilter(String fName){
		fileName = fName;
	}
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
		int byteswritten = 0;
		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		int id;							// This is the measurement id
		String altitudeFrameBuffer = "";
		String pressureFrameBuffer = "";

		System.out.println(this.getName() + "::WriteToFileFilter Reading ");
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			// Creating a header for Easy readibility
			out.write("Time:\t\t   Altitude(m):        Pressure(psi):      Temperature(C):     Attitude(degrees):");
			out.newLine();
			out.write("---------------------------------------------------------------------------------------------------------------");
			while (true)
			{
				try
				{
					id = getIDFromFilterInput();
					measurement = getMeasurementFromFilterInput();
					bytesread+=12;
					if(id == VELOCITY){
						// Not writing Velocity, simply incrementing bytes and using it as sanity check
						byteswritten+=12;
					}
					if (id == TIME){
						out.newLine();
						TimeStamp.setTimeInMillis(measurement);
						out.write(TimeStampFormat.format(TimeStamp.getTime())+"\t\t");
						byteswritten+=12;
						
					}
					else if(id == ALTITUDE){
						altitudeFrameBuffer = String.format("%11s", String.format("%5.5f",Double.longBitsToDouble(measurement))).replace(' ', '0');
						out.write(altitudeFrameBuffer+"\t\t\t");
						byteswritten+=12;
					}
					else if(id == TEMPERATURE){
						out.write(String.format("%9s", String.format("%.5f",Double.longBitsToDouble(measurement)))+"\t\t\t");
						byteswritten+=12;
					}
					else if(id == PRESSURE){
						pressureFrameBuffer = String.format("%8s", String.format("%2.5f",Double.longBitsToDouble(measurement))).replace(' ', '0').replace('.',':');
						out.write(pressureFrameBuffer + "\t\t\t");
						byteswritten+=12;
					}
					else if(id == PRESSURE+WILD_POINT_ID_OFFSET){
						pressureFrameBuffer = String.format("%8s", String.format("%2.5f",Double.longBitsToDouble(measurement))).replace(' ', '0').replace('.',':')+"*";
						out.write(pressureFrameBuffer + "\t\t\t");
						byteswritten+=12;
					}
					else if(id == ATTITUDE){
						out.write(String.format("%8s", String.format("%2.5f",Double.longBitsToDouble(measurement))).replace(' ', '0').replace('.',':'));
						byteswritten+=12;
					}
				}
				catch (EndOfStreamException e)
				{
					ClosePorts();
					out.close();
					System.out.println(this.getName() + "::WriteToFileFilter Exiting; bytes read: " + bytesread +" bytes written: "+byteswritten);
					break;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

   }
}