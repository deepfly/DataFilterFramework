/*****************************************************************************************************
* File:AltitudeConversionFilter.java
* Course: 17655
* Project: Assignment 1
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* Converts altitude units only
*
* 
* @author: Ziping Zheng
*
*****************************************************************************************************/
package SystemA;
public class AltitudeConversionFilter extends SingleOutputFilter
{
	public void run()
    {
		int bytesread = 0;				
		int byteswritten = 0;			
		int id;
		double measurement;

		System.out.println(this.getName() + "::AltitudeConversion Reading ");

		while (true)
		{
			try
			{
				id = getIDFromFilterInput();
				measurement = Double.longBitsToDouble(getMeasurementFromFilterInput());
				bytesread+=12;
				if(id == ALTITUDE){
					measurement = 0.3048*measurement;
				}
				writeInt(id);
				writeDouble(measurement);
				byteswritten+=12;
			}
			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.println(this.getName() + "::AltitudeConversion Exiting; Total bytes read: " + bytesread + " Total bytes written: " + byteswritten );
				break;

			}
		}
    }
} 
