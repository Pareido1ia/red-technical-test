// Copyright Red Energy Limited 2017

package simplenem12;

import java.io.File;
import java.util.Collection;

/**
 * Simple test harness for trying out SimpleNem12Parser implementation
 */
public class TestHarness {

  public static void main(String[] args) {
    ClassLoader classLoader = TestHarness.class.getClassLoader();
    // Can swap this with another SimpleNemFile like the larger test "SimpleNem12Larger.csv" or one of your own
    File simpleNem12File = new File(classLoader.getResource("SimpleNem12.csv").getFile());

    System.out.println(simpleNem12File);
    Collection<MeterRead> meterReads = new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File);

    // added to handle different files with new/different NMI's
    meterReads.forEach(o -> System.out.printf("Total volume for NMI %s is %f%n", o.getNmi(), o.getTotalVolume().stripTrailingZeros()));


    // Uncomment below to try out test harness.
//    MeterRead read6123456789 = meterReads.stream().filter(mr -> mr.getNmi().equals("6123456789")).findFirst().get();
//    System.out.println(String.format("Total volume for NMI 6123456789 is %f", read6123456789.getTotalVolume()));  // Should be -36.84
//
//    MeterRead read6987654321 = meterReads.stream().filter(mr -> mr.getNmi().equals("6987654321")).findFirst().get();
//    System.out.println(String.format("Total volume for NMI 6987654321 is %f", read6987654321.getTotalVolume()));  // Should be 14.33
  }
}
