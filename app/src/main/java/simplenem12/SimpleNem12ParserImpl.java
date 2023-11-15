package simplenem12;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {

    static final public DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public MeterRead createNewMeterRead(String[] values) {
        if (values.length != 3) System.err.println("Invalid line for record type:" + values[0]);

        String nmi = values[1];
        EnergyUnit energyUnit = EnergyUnit.valueOf(values[2]);
        return new MeterRead(nmi, energyUnit);
    }

    public MeterVolume createNewMeterVolume(String[] values) {
        if (values.length != 4) System.err.println("Invalid line for record type:" + values[0]);

        BigDecimal volume = new BigDecimal(values[2]);
        Quality quality = Quality.valueOf(values[3]);
        return new MeterVolume(volume, quality);
    }

    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {

        HashSet<MeterRead> meterReads = new HashSet<>();
        MeterRead currMeterRead = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(simpleNem12File))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            validateLines(lines);

            int index = 0;
            for (String line : lines) {
                String[] values = line.split(",");
                String recordType = values[0];

                switch (recordType) {
                    case "100": //
                        if (index != 0) System.err.println(recordType + " only valid at file start");
                        break;

                    case "200": // Add new MeterRead
                        currMeterRead = createNewMeterRead(values);
                        meterReads.add(currMeterRead);
                        break;

                    case "300": // Add MeterVolume to the current MeterRead
                        if (currMeterRead != null) {
                            LocalDate date = LocalDate.parse(values[1], FORMATTER);
                            currMeterRead.appendVolume(date, createNewMeterVolume(values));
                        } else {
                            System.err.println("No current meter read for record type: " + recordType);
                        }
                        break;

                    case "900": // Eof
                        if (index != lines.size() - 1) System.err.println(recordType + " only valid at file end");
                        break;

                    default:
                        System.err.println("Unknown record type: " + recordType);
                        break;
                }
                index++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return meterReads;
    }

    public void validateLines(List<String> lines) {
        if (lines.isEmpty()) {
            System.err.println("File is empty");
        }
        if (!lines.get(0).equals("100")) {
            System.err.println("File must start with record type 100");
        }
        if (!lines.get(lines.size() - 1).equals("900")) {
            System.err.println("File must end with record type 900");
        }
    }

}
