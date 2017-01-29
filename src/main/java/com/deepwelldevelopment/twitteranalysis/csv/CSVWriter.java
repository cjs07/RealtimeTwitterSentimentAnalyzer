package com.deepwelldevelopment.twitteranalysis.csv;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

    private ICsvBeanWriter beanWriter;

    private final String directory = "src/main/python/";

    private final String[] header = new String[] { "created_at", "id", "id_str", "text", "lang" };
    private final CellProcessor[] processors = getProcessors();

    public CSVWriter() {
        try {
            beanWriter = new CsvBeanWriter(new FileWriter(directory + "collected_tweets.csv"),
                    CsvPreference.STANDARD_PREFERENCE);
            beanWriter.writeHeader(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Object... tweets) throws IOException {
        for (Object tweet : tweets) {
            beanWriter.write(tweet, header, processors);
        }
    }

    private CellProcessor[] getProcessors() {
        return new CellProcessor[] {
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull()
        };
    }
}
