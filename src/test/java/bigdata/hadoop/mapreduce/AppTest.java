package bigdata.hadoop.mapreduce;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testSplit()
    {
        System.out.println("Hello World");
        String s = "f65cb7d1-b9ad-43ca-8a55-9190292e9bdd,jzp://edv#211a.0000,2020-04-01T04:59:58.477+02:00,Environmental,m,5.0,47.0,16.985782623291016,36.0,5.193526268005371,1.7929811477661133,94953.0,0.0,0.0,0.0,0.0,0.0,0.0,,,,,";
        //s = "9c0aeffe-b0d5-4bda-8d9c-9d0cb65102c7,jzp://edv#210c.0000,2020-04-01T04:59:18.775+02:00,LuxMeter,m,4.0,50.0,,,,,94981.0,0.0,0.0,0.0,0.0,0.0,0.0,2.55,,38.0,6.0,1.0269593000411987";
        String[] parts = s.split(",", -1);
        System.out.println(parts.length);

    }
}
