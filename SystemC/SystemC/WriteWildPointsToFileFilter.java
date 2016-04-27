/******************************************************************************************************************
* File:WriteWildPointsToFileFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This class will create a filter to write to predefined file, WildPoints.dat.
*
*
* Internal Methods: 
* 	run()
* 
* @author: Ziping Zheng
*
******************************************************************************************************************/
package SystemC;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

public class WriteWildPointsToFileFilter extends SingleOutputFilter
{
	public void run()
    {
		Calendar TimeStamp = Calendar.getInstance();
		SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

		int bytesread = 0;	

		long measurement;
		int id;	
		String pressureFrameBuffer = "";

		System.out.println(this.getName() + "::WriteWildValuesToFile Reading ");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("WildPoints.dat"));

			out.write("Time:\t\t   Pressure(psi):");
			out.newLine();
			out.write("-----------------------------------------------");

			while (true)
			{
				try
				{
					id = getIDFromFilterInput();
					measurement = getMeasurementFromFilterInput();
					bytesread+=12;
					if (id == TIME)
					{
						out.newLine();
						TimeStamp.setTimeInMillis(measurement);
						out.write(TimeStampFormat.format(TimeStamp.getTime())+"\t\t");
					}
					else if(id == PRESSURE || id == PRESSURE+WILD_POINT_ID_OFFSET){
						if(Double.longBitsToDouble(measurement) >= 0){//If greater than zero, append 0s at the beginning
							pressureFrameBuffer = String.format("%10s", String.format("%4.5f",Double.longBitsToDouble(measurement))).replace(' ', '0').replace('.',':');
							out.write(pressureFrameBuffer);
						}else{
							pressureFrameBuffer = String.format("%10s", String.format("%4.5f",Double.longBitsToDouble(measurement))).replace('.',':');
							out.write(pressureFrameBuffer);
						}
					}
				}
				catch (EndOfStreamException e)
				{
					ClosePorts();
					out.close();
					System.out.println(this.getName() + "::WriteWildValuesToFile Exiting; bytes read: " + bytesread );
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
}