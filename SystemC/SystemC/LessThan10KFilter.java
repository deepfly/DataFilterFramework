package SystemC;

import java.util.ArrayList;
import java.util.List;

public class LessThan10KFilter extends MultipleOutputFilter {
    public void run()
    {
        int bytesread = 0;              
        int byteswritten = 0;           
        int id;
        long time = 0;
        List<Double> measurements = new ArrayList<Double>();
        
        System.out.println(this.getName() + "::LessThan10KFilter Reading ");

        while (true)
        {
            try
            {
                id = getIDFromFilterInput();
                if (id==0)
                    time = getMeasurementFromFilterInput();
                else
                    measurements.add(Double.longBitsToDouble(getMeasurementFromFilterInput()));
                bytesread+=12;
                
                if (measurements.size() == 5) {
                    if (measurements.get(1) >= 10000) {
                        writeInt(0, 1);
                        writeLong(time, 1);
                        byteswritten+=12;
                        for (int i=0; i<measurements.size(); i++){
                            writeInt(i+1, 1);
                            writeDouble(measurements.get(i), 1);
                            byteswritten+=12;
                        }
                    } else {
                        writeInt(0, 2);
                        writeLong(time, 2);
                        byteswritten+=12;
                        for (int i=0; i<measurements.size(); i++){
                            writeInt(i+1, 2);
                            writeDouble(measurements.get(i), 2);
                            byteswritten+=12;
                        }
                    }
                    measurements.clear();
                }
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