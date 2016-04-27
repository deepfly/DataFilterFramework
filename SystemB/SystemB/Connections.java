/******************************************************************************************************************
* File: Connections.java
* Version: 27 April 2016 (ZZ)
*
* Description:
*
* This superclass simply defines a connection framework for different filters by providing the bare minimum connection
* functionality. The other I/o operations are not handled in this class. It merely acts as a connect interface for different
* filters so filters can derive their varying number of input and output ports from a single source of truth.
*
* Internal Methods:
*
*	void Connect(SingleOutputFilter Filter)
*
* @author: Ziping Zheng
*
******************************************************************************************************************/
package SystemB;
import java.io.*;

public class Connections extends Thread
{
	// Final variables to store the IDs given in the assignment table
	protected static final int TIME 				= 0;
	protected static final int VELOCITY 			= 1;
	protected static final int ALTITUDE 			= 2;
	protected static final int PRESSURE 			= 3;
	protected static final int TEMPERATURE 			= 4;
	protected static final int ATTITUDE 			= 5;
	protected static final int WILD_POINT_ID_OFFSET = 1000;
	
	// Define filter input and output ports

	protected PipedInputStream InputReadPort = new PipedInputStream();
	protected PipedOutputStream OutputWritePort = new PipedOutputStream();

	// The following reference to a filter is used because java pipes are able to reliably
	// detect broken pipes on the input port of the filter. This variable will point to
	// the previous filter in the network and when it dies, we know that it has closed its
	// output pipe and will send no more data.

	protected Connections InputFilter;

	/***************************************************************************
	* CONCRETE METHOD:: Connect
	* Purpose: This method connects filters to each other. All connections are
	* through the inputport of each filter. That is each filter's inputport is
	* connected to another filter's output port through this method.
	*
	* Arguments:
	* 	SingleOutputFilter Filter - this is the filter that this filter will connect to.
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/

	void Connect(SingleOutputFilter Filter)
	{
		try
		{
			// Connect this filter's input to the upstream pipe's output stream

			InputReadPort.connect( Filter.OutputWritePort );
			InputFilter = Filter;

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " SingleOutputFilter error connecting::"+ Error );

		} // catch
	}

	/***************************************************************************
	* CONCRETE METHOD:: Connect
	* Purpose: This method connects filters to each other. All connections are
	* through the inputport of each filter. That is each filter's inputport is
	* connected to another filter's output port through this method.
	*
	* Arguments:
	* 	MultipleOutputFilter Filter - this is the filter that this filter 
	* 	will connect to. For a MultiOutputFilterFramework, no port means that the
	* 	default first port will be connected.
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/
	void Connect (MultipleOutputFilter Filter){
		try
		{
			// Connect this filter's input to the upstream pipe's output stream
				InputReadPort.connect( Filter.OutputWritePort );
				InputFilter = Filter;

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " MultipleOutputFilter error connecting::"+ Error );

		} // catch
	}

	/***************************************************************************
	* CONCRETE METHOD:: Connect
	* Purpose: This method connects filters to each other. All connections are
	* through the inputport of each filter. That is each filter's inputport is
	* connected to another filter's output port through this method.
	*
	* Arguments:
	* 	MultipleOutputFilter Filter - this is the filter that this filter 
	* 	will connect to.
	* 	int port- The output port number of the port to connect to.
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/
	void Connect (MultipleOutputFilter Filter, int port){
		try
		{
			switch(port){
			case 1:
				InputReadPort.connect( Filter.OutputWritePort );
				InputFilter = Filter;
				break;
			case 2:
				InputReadPort.connect( Filter.OutputWritePort2 );
				InputFilter = Filter;
				break;
			}
		}
		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " MultipleOutputFilter error connecting::"+ Error );
		}
	}
}
