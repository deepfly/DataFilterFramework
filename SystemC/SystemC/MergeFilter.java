/******************************************************************************************************************
* File:MergeFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This file will read the two files SubsetA.dat and SubsetB.dat. It first reads the time value from each
* file and forwards the smaller value along with the whole frame. Then, it get's another time value from the 
* file which had supplied the smaller value and compares with the time value from the other file that it 
* gotten previously, deciding which one to put forward in the stream, hence forming an ordered stream.
*
* @author: Ziping Zheng
*
******************************************************************************************************************/
package SystemC;
import java.io.*;

public class MergeFilter extends SingleOutputFilter
{
	private static final int EOF_VALUE = -10;

	public void run()
    {
		String fileName1 = "SubSetA.dat";	// Input data file.
		String fileName2 = "SubSetB.dat";	// Input data file.

		int bytesread1 = 0;					// Number of bytes read from the first input file.
		int byteswritten1 = 0;				// Number of bytes written to the stream.
		DataInputStream in1 = null;			// File stream reference.
		
		int bytesread2 = 0;					// Number of bytes read from the second input file.
		int byteswritten2 = 0;				// Number of bytes written to the stream.
		DataInputStream in2 = null;			// File stream reference.

		long measurement1 = Long.MAX_VALUE;
		int id1 = EOF_VALUE;

		long measurement2 = Long.MAX_VALUE;	
		int id2 = EOF_VALUE;
		
		boolean read1 = true;
		boolean read2 = true;

		try
		{
			/***********************************************************************************
			*	Here we open the file and write a message to the terminal.
			***********************************************************************************/
			in1 = new DataInputStream(new FileInputStream(fileName1));
			System.out.println(this.getName() + "::Source reading file..." + fileName1);

			in2 = new DataInputStream(new FileInputStream(fileName2));
			System.out.println(this.getName() + "::Source reading file..." + fileName2);

			/***********************************************************************************
			*	Here we read the data from the file and send it out the filter's output port one
			* 	byte at a time. The loop stops when it encounters an EOFExecption.
			***********************************************************************************/

			while(true)
			{
				// Read from file1
				if (read1) {
					// Get the ID from file1
					id1 = fetchID(fileName1, in1, bytesread1, byteswritten1);
					
					if (id1 != EOF_VALUE) {
						bytesread1 += 4;

						if (id1 != 0) {
						}
						
						measurement1 = fetchData(fileName1, in1, bytesread1, byteswritten1);

						if (measurement1 != Long.MAX_VALUE) {
							bytesread1 += 8;
					
							if (id1 != 0) {
								writeInt(id1);
								byteswritten1 += 4;
								writeLong(measurement1);
								byteswritten1 += 8;
							}
						} else {
							// File 1 EOF
							break;
						}
					} else {
						// File 1 EOF
						break;
					}
				}
 
				// Read from file2
				if (read2) {
					id2 = fetchID(fileName2, in2, bytesread2, byteswritten2);
						
					if (id2 != EOF_VALUE) {
						bytesread2 += 4;
						
						if (id2 != 0) {
						}

						measurement2 = fetchData(fileName2, in2, bytesread2, byteswritten2);

						if (measurement2 != Long.MAX_VALUE) {
							bytesread2 += 8;

							if (id2 != 0) {
								writeInt(id2);
								byteswritten2 += 4;
								writeLong(measurement2);
								byteswritten2 += 8;
							}
						} else {
							// File 2 EOF
							break;
						}
					} else {
						// File 2 EOF
						break;
					}
				}

				// Compare time stamp and hold the larger one in the buffer
				if (id1 == 0 && id2 == 0) {
					if (measurement1 <= measurement2) {
						writeInt(id1);
						writeLong(measurement1);
						byteswritten1 += 12;
						read1 = true;
						read2 = false;
					} else {
						writeInt(id2);
						writeLong(measurement2);
						byteswritten2 += 12;
						read1 = false;
						read2 = true;
					}
				}
			} // while

			// Edge Case 1: File 1 is finished but File 2 still needs to be read
			if (id1 != EOF_VALUE || measurement1 != Long.MAX_VALUE) {
				if (id2 == 0 && measurement2 != Long.MAX_VALUE) {
					writeInt(id2);
					writeLong(measurement2);
					byteswritten2 += 12;
				}
				if (id2 != EOF_VALUE && measurement2 != Long.MAX_VALUE) {
					try 
					{
						while (true) {
							byte databyte = in2.readByte();
							bytesread2++;
							WriteFilterOutputPort(databyte);
							byteswritten2++;
						}
					}
	
					catch (EOFException eoferr)
					{
						in2.close();
						ClosePorts();
						System.out.println(this.getName() + " " + fileName2 + "::End of file reached...\n"+this.getName() +"::Source Exiting; bytes read: " + bytesread2 + " bytes written: " + byteswritten2 );
					}
				}
			}

			// Edge Case 2: File 2 is finished but File 1 still needs to be read
			if (id2 != EOF_VALUE || measurement2 != Long.MAX_VALUE) {
				if (id1 == 0 && measurement1 != Long.MAX_VALUE) {
					writeInt(id1);
					writeLong(measurement1);
					byteswritten1 += 12;
				}
				if (id1 != EOF_VALUE && measurement1 != Long.MAX_VALUE) {
					try
					{
						while (true) {
								byte databyte = in1.readByte();
								bytesread1++;
								WriteFilterOutputPort(databyte);
								byteswritten1++;
						}
					}
					catch (EOFException eoferr)
					{
						in1.close();
						ClosePorts();
						System.out.println(this.getName() + " " + fileName1 + "::End of file reached...\n"+this.getName() +"Source Exiting; bytes read: " + bytesread1 + " bytes written: " + byteswritten1 );
					}
				}
			}
		} //try
		catch ( IOException ioerr )
		{
			System.out.println("\n" + this.getName() + "::Problem reading input data file::" + ioerr );
		}
   }
	
	private int fetchID(String fileName, DataInputStream in, int bytesread, int byteswritten) throws IOException {
		int id = 0;
		int IdLength = 4;
		byte databyte = 0;

		try{
			for (int i=0; i<IdLength; i++ )
			{
				databyte = in.readByte();	// This is where we read the byte from the stream...
				bytesread++;
		
				id = id | (databyte & 0xFF);		// We append the byte on to ID...
	
				if (i != IdLength-1)				// If this is not the last byte, then slide the
				{									// previously appended byte to the left by one byte
					id = id << 8;					// to make room for the next byte we append to the ID
	
				} // if
			} // for
		}

	catch (EOFException eoferr)
		{
			System.out.println(this.getName() + " " + fileName + "::End of file reached..." );
			try
			{
				id = EOF_VALUE;
				in.close();
			}
		/***********************************************************************************
		*	The following exception is raised should we have a problem closing the file.
		***********************************************************************************/
			catch (Exception closeerr)
			{
				System.out.println("\n" + this.getName() + "::Problem closing input data file::" + fileName + closeerr);

			} // catch

		} // catch

		return id;
	}

	private long fetchData(String fileName, DataInputStream in,  int bytesread, int byteswritten)  throws IOException {
		long measurement = 0;
		int MeasurementLength = 8;

		try {
			for (int i=0; i<MeasurementLength; i++ )
			{
				byte databyte = in.readByte();
				bytesread++;										// Increment the byte count
				measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

				if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
				{												// previously appended byte to the left by one byte
					measurement = measurement << 8;				// to make room for the next byte we append to the measurement
				}
			}
		}
		catch (EOFException eoferr)
		{
			System.out.println("\n" + this.getName() + "End of file reached..." );
			try
			{
				measurement = Long.MAX_VALUE;
				in.close();
				System.out.println( "\n" + this.getName() + "::Read file complete, bytes read::"
						+ bytesread + " bytes written: " + byteswritten );
			}
			catch (Exception closeerr)
			{
				System.out.println("\n" + this.getName() + "::Problem closing input data file::" + fileName + closeerr);

			}
		}
		return measurement;
	}
}