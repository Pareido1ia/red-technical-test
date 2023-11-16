package simplenem12;


import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {

    static final public DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {
        HashSet<MeterRead> meterReads = new HashSet<>();
        MeterRead currMeterRead = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(simpleNem12File))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            // run simple validation before iterating
            validateFile(lines);

            int index = 0;
            for (String line : lines) {
                String[] values = line.split(",");
                String recordType = values[0];

                switch (recordType) {
                    case "100": // Start of file
                        if (index != 0) handleError(recordType + " only valid at file start");
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
                            handleError("No current meter read for record type: " + recordType);
                        }
                        break;

                    case "900": // Eof
                        if (index != lines.size() - 1) handleError(recordType + " only valid at file end");
                        break;

                    default:
                        handleError("Unknown record type: " + recordType);
                        break;
                }
                index++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return meterReads;
    }

    // Validate and create a new meter read
    public MeterRead createNewMeterRead(String[] values) {
        if (values.length != 3) handleError("Invalid meter read record");

        String nmi = values[1];

        if (nmi == null || nmi.length() != 10) handleError("Invalid nmi");
        if (!Objects.equals(values[2], "KWH")) handleError("Invalid energy unit");

        EnergyUnit energyUnit = EnergyUnit.valueOf(values[2]);

        return new MeterRead(nmi, energyUnit);
    }

    // Validate and create a new meter volume
    public MeterVolume createNewMeterVolume(String[] values) {
        if (values.length != 4) handleError("Invalid meter volume record");

        if (!isNumeric(values[2])) handleError("Invalid volume");
        if (!(values[3].equals("A") || values[3].equals("E"))) handleError("Invalid quality");

        BigDecimal volume = new BigDecimal(values[2]);
        Quality quality = Quality.valueOf(values[3]);

        return new MeterVolume(volume, quality);
    }

    public void validateFile(List<String> lines) {
        if (lines.isEmpty()) {
            handleError("File is empty");
        }
        if (!lines.get(0).equals("100")) {
            handleError("File must start with record type 100");
        }
        if (!lines.get(lines.size() - 1).equals("900")) {
            handleError("File must end with record type 900");
        }
    }

    public void handleError(String error) {
        throw new IllegalArgumentException(error);
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

}
