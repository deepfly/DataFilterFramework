/******************************************************************************************************************
* File:WildValueExtrapolationFilter.java
* Course: 17655
* Project: Assignment 1
* Versions: 27 April 2016 (AGD)
*
* Description:
*
* This class will filter the wild data points. It will replace them with an average of the last
* valid measurement and the next valid measurement.
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


public class WildValueExtrapolationFilter extends MultipleOutputFilter
{
	HashMap<Integer, Object> map = new HashMap<Integer, Object>();
	ArrayList<Integer> idArr = new ArrayList<Integer>();
	ArrayList<Object> dataArr = new ArrayList<Object>();
	
	public void run()
	{
		Double measurement = (double) 0;				// This is the word used to store all measurements - WildPointss are illustrated.
		long time = 0;
		Integer id;							// This is the measurement id
		int idBefore;
		System.out.print( "\n" + this.getName() + "::WildPoints Reading ");
		int i = 0, framesize = 4;
		Double previous = Double.NaN;
		int bytesread = 0;				
		int byteswritten = 0;

		while (true)
		{
			/*************************************************************
			 *	Here we read a byte and write a byte
			 *************************************************************/

			try {
				/*************************************************************
				 *	Here we read a frame each time and write to map for the use of next step.
				 *************************************************************/
				for(i = 0; i < framesize; i++){
					id = getIDFromFilterInput();
					if(id == TIME){
						time = getMeasurementFromFilterInput();
						bytesread+=12;
						map.put(id, time);
					}else{
						measurement = Double.longBitsToDouble(getMeasurementFromFilterInput());
						bytesread+=12;
						map.put(id, measurement);
					}
				}
				
				/*************************************************************
				 *	Here we read pressure data and judge whether it is a wild point.
				 *************************************************************/
				measurement = (Double) map.get(3);//get the pressure
				if(measurement < 0 || previous != Double.NaN && Math.abs(previous - measurement) > 10){
					saveFrameToList();
				}else{//find the next data which is not a wild point
					/*************************************************************
					 *	Here we can calculate the replace value for wild points, which are saved in the list.
					 *************************************************************/
					i = 0;
					while(i < idArr.size()){
						int j = 0;
						for(; j < framesize; j++, i++){
							idBefore = idArr.get(i);
							if(idBefore == PRESSURE)
								idBefore += WILD_POINT_ID_OFFSET;
							writeInt(idBefore, 1);
							byteswritten+=4;
							
							
							if(idBefore == TIME){//is time, whose type is long
								/*************************************************************
								 *	Write time of the wild point to the next filter connected to port 0.
								 **************************************************************/
								writeLong((long) dataArr.get(i), 1);
								byteswritten+=8;
								/*************************************************************
								 *	Write time of the wild point to the next filter connected to port 1.
								 **************************************************************/
								writeInt(idBefore, 2);
								writeLong((long) dataArr.get(i), 2);
								byteswritten+=12;
							}else if(idBefore == PRESSURE || idBefore == PRESSURE + WILD_POINT_ID_OFFSET){//pressure
								/*************************************************************
								 *	Write replaced pressure value of the wild point to the next filter connected to port 0.
								 **************************************************************/
								if(previous == Double.NaN)
									writeDouble(measurement, 1);
								else
									writeDouble((measurement + previous)/2, 1);
								byteswritten+=8;
								/*************************************************************
								 *	Write the pressure data to the next filter connected to port 1.
								 **************************************************************/
								writeInt(idBefore, 2);
								writeDouble((double) dataArr.get(i), 2);
								byteswritten+=12;
							}else{//other measurements, whose type is double
								/*************************************************************
								 *	Write other measurement data to the next filter connected to port 0.
								 **************************************************************/
								writeDouble((Double)dataArr.get(i), 1);
								byteswritten+=8;
							}
						}
					}
					idArr.clear();
					dataArr.clear();
					/*************************************************************
					 *	Here we write this non-wild point data to output port 0.
					 *************************************************************/
					previous = (Double) map.get(PRESSURE);
					writeFrameTo(1);
					byteswritten+=48;
					writeInt(6, 1);
					writeDouble((double) 0, 1);
					byteswritten+=12;
				}
			} catch (EndOfStreamException e) {
				/*************************************************************
				 *	In case that the last few points are all wild points...
				 *************************************************************/
				i = 0;
				while(i < idArr.size()){
					int j = 0;
					for(; j < framesize; j++, i++){
						idBefore = idArr.get(i);
						if(idBefore == PRESSURE)
							idBefore += WILD_POINT_ID_OFFSET;
						writeInt(idBefore, 1);
						byteswritten+=4;
						if(idBefore == TIME){//is time, whose type is long
							/*************************************************************
							 *	Write time of the wild point to the next filter connected to port 0.
							 **************************************************************/
							writeLong((long) dataArr.get(i), 1);
							
							/*************************************************************
							 *	Write time of the wild point to the next filter connected to port 1.
							 **************************************************************/
							writeInt(idBefore, 2);
							byteswritten+=12;
							writeLong((long) dataArr.get(i), 2);
							byteswritten+=8;
						}else if(idBefore == PRESSURE || idBefore == PRESSURE + WILD_POINT_ID_OFFSET){//pressure
							/*************************************************************
							 *	Write replaced pressure value of the wild point to the next filter connected to port 0.
							 **************************************************************/
							writeDouble(previous, 1);
							/*************************************************************
							 *	Write the pressure data to the next filter connected to port 1.
							 **************************************************************/
							writeInt(idBefore, 2);
							byteswritten+=12;
							writeDouble((double) dataArr.get(i), 2);
							byteswritten+=8;

						}else{//other measurements, whose type is double
							/*************************************************************
							 *	Write other measurement data to the next filter connected to port 0.
							 **************************************************************/
							writeDouble((Double)dataArr.get(i), 1);
							byteswritten+=8;
						}
					}
				}
				idArr.clear();
				dataArr.clear();
				
				// TODO Auto-generated catch block
				ClosePorts();
				System.out.println(this.getName() + "::WildValueExtrapolation Exiting; Total bytes read: " + bytesread + " Total bytes written: " + byteswritten );
				break;
			}
		} // while
	} // run
	
	private void saveFrameToList() {
		// TODO Auto-generated method stub
		idArr.add(0);
		idArr.add(4);
		idArr.add(2);
		idArr.add(3);
		dataArr.add(map.get(0));
		dataArr.add(map.get(4));
		dataArr.add(map.get(2));
		dataArr.add(map.get(3));
	}

	private void writeFrameTo(int index){
		writeInt(0, index);
		writeLong((Long)map.get(0), index);
		writeInt(4, index);
		writeDouble((Double)map.get(4), index);
		writeInt(2, index);
		writeDouble((Double)map.get(2), index);
		writeInt(3, index);
		writeDouble((Double)map.get(3), index);

	}

}