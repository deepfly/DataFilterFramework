/******************************************************************************************************************
* File: MultipleOutputFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions: 27 April 2016 (ZZ)
*
* Description:
*
* This superclass defines a skeletal filter framework that defines a filter in terms of one input and two output
* ports. This class defines the I/O operations associated with each filter and extends the Connections file that 
* specifies how ports need to be linked together.
*
*
* Internal Methods:
*
*	public byte ReadFilterInputPort()
*	public void WriteFilterOutputPort(byte datum, int port)
*	public boolean EndOfInputStream()
*   public int getIDFromFilterInput()
*   public long getMeasurementFromFilterInput()
*   public void writeInt(int data, int port)
*   public void writeDouble(double data, int port)
*   public void writeLong(long data, int port)
*
* @author: Ziping Zheng
* 
******************************************************************************************************************/

package SystemB;
import java.io.*;
import java.nio.*;


public class MultipleOutputFilter extends Connections 
{
	// Putting a limit of 2 on the ports right now
	private static final int MAX_PORTS = 2;
	// Declaring a new output port to make the total number of ports 2
	protected PipedOutputStream OutputWritePort2 = new PipedOutputStream();
	
	/***************************************************************************
	* InnerClass:: EndOfStreamExeception
	* Purpose: This
	*
	*
	*
	* Arguments: none
	*
	* Returns: none
	*
	* Exceptions: none
	*
	****************************************************************************/

	class EndOfStreamException extends Exception {
		
		static final long serialVersionUID = 0; // the version for serializing

		EndOfStreamException () { super(); }

		EndOfStreamException(String s) { super(s); }

	} // class
	/***************************************************************************
	* CONCRETE METHOD:: EndOfInputStream
	* Purpose: This method is used within this framework which is why it is private
	* It returns a true when there is no more data to read on the input port of
	* the instance filter. What it really does is to check if the upstream filter
	* is still alive. This is done because Java does not reliably handle broken
	* input pipes and will often continue to read (junk) from a broken input pipe.
	*
	* Arguments: void
	*
	* Returns: A value of true if the previous filter has stopped sending data,
	*		   false if it is still alive and sending data.
	*
	* Exceptions: none
	*
	****************************************************************************/
	private boolean EndOfInputStream()
	{
		if (InputFilter.isAlive())
		{
			return false;

		} else {

			return true;

		} // if

	}
	
	/***************************************************************************
	* CONCRETE METHOD:: ReadFilterInputPort
	* Purpose: This method reads data from the input port one byte at a time.
	*
	* Arguments: void
	*
	* Returns: byte of data read from the input port of the filter.
	*
	* Exceptions: IOExecption, EndOfStreamException (rethrown)
	*
	****************************************************************************/

	byte ReadFilterInputPort() throws EndOfStreamException
	{
		byte datum = 0;

		/***********************************************************************
		* Since delays are possible on upstream filters, we first wait until
		* there is data available on the input port. We check,... if no data is
		* available on the input port we wait for a quarter of a second and check
		* again. Note there is no timeout enforced here at all and if upstream
		* filters are deadlocked, then this can result in infinite waits in this
		* loop. It is necessary to check to see if we are at the end of stream
		* in the wait loop because it is possible that the upstream filter completes
		* while we are waiting. If this happens and we do not check for the end of
		* stream, then we could wait forever on an upstream pipe that is long gone.
		* Unfortunately Java pipes do not throw exceptions when the input pipe is
		* broken.
		***********************************************************************/

		try
		{
			while (InputReadPort.available()==0 )
			{
				if (EndOfInputStream())
				{
					throw new EndOfStreamException("End of input stream reached");

				} //if

				sleep(250);

			} // while

		} // try

		catch( EndOfStreamException Error )
		{
			throw Error;

		} // catch

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " Error in read port wait loop::" + Error );

		} // catch

		/***********************************************************************
		* If at least one byte of data is available on the input
		* pipe we can read it. We read and write one byte to and from ports.
		***********************************************************************/

		try
		{
			datum = (byte)InputReadPort.read();
			return datum;

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " Pipe read error::" + Error );
			return datum;

		} // catch

	} // ReadFilterPort
	
	/***************************************************************************
	* CONCRETE METHOD:: WriteFilterOutputPort
	* Purpose: This method writes data to one output port one byte at a time.
	*
	* Arguments:
	* 	byte datum - This is the byte that will be written on the filter's output port
	*	int port - This is the port number that the datum will be written to
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/
	void WriteFilterOutputPort(byte datum, int port)
	{
		try
		{
			switch(port){
			case 1:
				OutputWritePort.write((int) datum );
			   	OutputWritePort.flush();
			   	break;
			case MAX_PORTS:
				OutputWritePort2.write((int) datum );
			   	OutputWritePort2.flush();
			   	break;
			}

		} // try

		catch( Exception Error )
		{
			System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

		} // catch

		return;

	} // WriteFilterPort

	/***************************************************************************
	* CONCRETE METHOD:: ClosePorts
	* Purpose: This method is used to close the input and output ports of the
	* filter. It is important that filters close their ports before the filter
	* thread exits.
	*
	* Arguments: void
	*
	* Returns: void
	*
	* Exceptions: IOExecption
	*
	****************************************************************************/
	void ClosePorts()
	{
		try
		{
			InputReadPort.close();
			OutputWritePort.close();
			OutputWritePort2.close();

		}
		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " ClosePorts error::" + Error );

		} // catch

	} // ClosePorts


	/****************************************************************************
	*
	*
	*
	*
	*	Additional Methods to help with code reusability
	*
	*
	*
	*
	*****************************************************************************


	/***************************************************************************
	* CONCRETE METHOD:: getIDFromFilterInput
	* Purpose: This method is an abstraction taken from SinkFilter.java. It reads 
	* 8 bytes of data at a time and converts them into the ID.
	*
	* Arguments: void
	*
	* Returns: integer after reading 4 bytes 
	*
	* Exceptions: EndOfStreamException
	*
	****************************************************************************/
	int getIDFromFilterInput() throws EndOfStreamException
	{
		int id = 0;
		int IdLength = 4;

		for (int i=0; i<IdLength; i++ )
		{
			byte datum = ReadFilterInputPort();	// This is where we read the byte from the stream...

			id = id | (datum & 0xFF);		// We append the byte on to ID...

			if (i != IdLength-1)				// If this is not the last byte, then slide the
			{									// previously appended byte to the left by one byte
				id = id << 8;					// to make room for the next byte we append to the ID

			} // if
		} // for
		return id;


	}

	/***************************************************************************
	* CONCRETE METHOD:: getMeasurementFromFilterInput
	* Purpose: This method is an abstraction taken from SinkFilter.java. It reads 
	* 8 bytes of data at a time and converts them into the measurement.
	*
	* Arguments: void
	*
	* Returns: long after reading 4 bytes
	*
	* Exceptions: EndOfStreamException
	*
	****************************************************************************/
	long getMeasurementFromFilterInput() throws EndOfStreamException{
		long measurement = 0;
		int MeasurementLength = 8;
		for (int i=0; i<MeasurementLength; i++ )
		{
			byte databyte = ReadFilterInputPort();				
			measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...

			if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
			{												// previously appended byte to the left by one byte
				measurement = measurement << 8;				// to make room for the next byte we append to the measurement
			} // if

		} // if
		return measurement;
	}

	// The following three methods allow for easier data writes to ports so manipulation of data is easier in corresponding filters

	void writeInt(int data, int port) {
		try{
			byte[] bytes = ByteBuffer.allocate(4).putInt(data).array();
			for(byte b:bytes){
					WriteFilterOutputPort(b, port);
			}
		}//try
		catch( Exception Error )
		{
			System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

		} // catch

		return;
	}

	void writeDouble(double data, int port){
		try{
			byte[] bytes = ByteBuffer.allocate(8).putDouble(data).array();
			for(byte b:bytes){
				WriteFilterOutputPort(b, port);
			}
		}//try
		catch( Exception Error )
		{
			System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

		} // catch
	}

	void writeLong(long data, int port) {
		try{
			byte[] bytes = ByteBuffer.allocate(8).putLong(data).array();
			for(byte b:bytes){
				WriteFilterOutputPort(b, port);
			}
		}//try
		catch( Exception Error )
		{
			System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

		} // catch
	}
	
	
}
