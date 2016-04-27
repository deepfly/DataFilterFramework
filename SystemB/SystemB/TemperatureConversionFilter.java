/*****************************************************************************************************
* File:TemperatureConversionFilter.java
* Course: 17655
* Project: Assignment 1
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* Converts temperature units only
* 
* @author: Ziping Zheng
*
*****************************************************************************************************/
package SystemB;
public class TemperatureConversionFilter extends SingleOutputFilter
{
	public void run()
    {
		int bytesread = 0;				
		int byteswritten = 0;				
		int id;
		double measurement;

		System.out.println(this.getName() + "::TemperatureConversion Reading ");

		while (true)
		{
			// Reading a byte and writing a byte

			try
			{
				id = getIDFromFilterInput();
				measurement = Double.longBitsToDouble(getMeasurementFromFilterInput());
				bytesread+=12;
				if(id == TEMPERATURE){
					measurement = (measurement-32)/1.8;
				}
				writeInt(id);
				writeDouble(measurement);
				byteswritten+=12;
			}
			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.println(this.getName() + "::TemperatureConversion Exiting; Total bytes read: " + bytesread + " Total bytes written: " + byteswritten );
				break;
			}
		}
   }
}