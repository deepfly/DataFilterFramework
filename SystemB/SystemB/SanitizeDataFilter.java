/******************************************************************************************************************
* File:SanitizeDataFilter.java
* Course: 17655
* Project: Assignment 1
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This class will remove the data points that do not have their ids specified
* in the requiredIDs list.
*
*
* Internal Methods: 
* 	public void run()
* 
* @author: Ziping Zheng
*
******************************************************************************************************************/
package SystemB;
import java.util.*;

public class SanitizeDataFilter extends SingleOutputFilter
{
	private List<Integer> requiredIDs = Arrays.asList(TIME, ALTITUDE, TEMPERATURE, PRESSURE);  
	public void run()
    {
		int bytesread = 0;				
		int byteswritten = 0;				
		int id;
		long measurement;

		System.out.println(this.getName() + "::Sanitizing data... ");

		while (true)
		{
			try
			{
				id = getIDFromFilterInput();
				measurement = getMeasurementFromFilterInput();
				bytesread+=12;
				if(requiredIDs.contains(id)){
					writeInt(id);
					writeLong(measurement);
					byteswritten+=12;
				}
			}
			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.println(this.getName() + "::Data sanitization finished; Total bytes read: " + bytesread + " Total bytes written: " + byteswritten );
				break;
			} 
		} 
   } 
} 