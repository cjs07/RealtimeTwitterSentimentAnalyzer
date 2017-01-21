package com.deepwelldevelopment.twitteranalysis.csv;

import com.deepwelldevelopment.twitteranalysis.core.Tweet;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {

    private ICsvBeanWriter beanWriter;

    private final String[] header = new String[] { "created_at", "id", "id_str", "text", "lang" };
    private final CellProcessor[] processors = getProcessors();

    public CSVWriter() {
        try {
            beanWriter = new CsvBeanWriter(new FileWriter("src/main/resources/collected_tweets.csv"),
                    CsvPreference.STANDARD_PREFERENCE);
            beanWriter.writeHeader(header);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void write(Tweet tweet) throws IOException {
        // write the header
        beanWriter.write(tweet, header, processors);
    }

    private CellProcessor[] getProcessors() {
        return new CellProcessor[] {
                new NotNull(),
                new UniqueHashCode(),
                new UniqueHashCode(),
                new NotNull(),
                new NotNull()
        };
    }
}
