package com.bigdistributer.dataexchange.utils.readfile;

import com.bigdistributer.dataexchange.utils.DEFAULT;
import com.google.common.base.Joiner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVReader {
    private String path;

    public CSVReader(String path) {
        this.path = path;
    }

    public Map<String, List<String>> read() {
        Map<String, List<String>> result = new HashMap();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            if (line == null)
                throw new NullPointerException("Empty file!");
            String[] elms = line.split(",");
            List<String> keys = Arrays.asList(elms);

            for (String k : keys)
                result.put(k, new ArrayList<>());
            while ((line = br.readLine()) != null) {
                elms = line.split(",");
                for (int i = 0; i < elms.length; i++)
                    result.get(keys.get(i)).add(elms[i]);
                keys = Arrays.asList(elms);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        Map<String, List<String>> all = read();
        Joiner.MapJoiner mapJoiner = Joiner.on(",").withKeyValueSeparator("=");
        return mapJoiner.join(all);
    }

    public static void main(String[] args) {
        final String csv_path = DEFAULT.AWS_CREDENTIALS_PATH;
        CSVReader reader = new CSVReader(csv_path);
        System.out.println(reader);
    }
}
