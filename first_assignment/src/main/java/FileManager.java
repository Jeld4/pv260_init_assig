import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FileManager {

    private File file;
    private File outputFile;

    private Map<Integer, Integer> years;
    private Map<Integer, Integer> ordersCountInYear;

    private static final int DATE_INDEX = 1;
    private static final int EMAIL_INDEX = 2;
    private static final int ADRESS_INDEX = 3;
    private static final int PRICE_INDEX = 4;
    private static final int STATUS_INDEX = 5;

    private Date date;

    public void processFilterAction(String action) {
        try {
            File tempFile = new File("modifiedFile.csv");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            rewriteToFile(action, reader, writer);

            reader.close();
            writer.close();

            copyFileUsingStream(new FileInputStream(tempFile), file);
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    private void rewriteToFile(String action, BufferedReader reader, BufferedWriter writer) throws IOException {
        String line;
        while((line = reader.readLine()) != null) {

            switch (action) {
                case "emptyEmail":
                    if (hasEmptyEmail(line))
                        continue;
                    break;
                case "emptyAddress":
                    if (hasEmptyAddress(line))
                        continue;
                    break;
            }

            writer.write(line);
            writer.write("\n");
        }
    }

    public void getAveragePriceOfOrdersPerYear(boolean isOrderPaid) {

        try
        {
            FileReader fr = new FileReader(file);

            BufferedReader reader = new BufferedReader(fr);

            String line;

            while((line = reader.readLine()) != null) {
                getPriceFromOrder(line, isOrderPaid);
            }
            reader.close();
            fr.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        String status = isOrderPaid ? "paid" : "unpaid";
        String output = "";

        for (Integer year : years.keySet()) {
            int result = 0;
            if (ordersCountInYear.get(year) != 0) {
                result = years.get(year) / ordersCountInYear.get(year);
            }
            output += "Average price of " + status + " orders in year " + year + " is " + result + "\n";
        }

        printToOutputFile(output);
    }

    private void printToOutputFile(String output) {
        try
        {
            FileWriter fr = new FileWriter(outputFile, true);
            BufferedWriter reader = new BufferedWriter(fr);
            reader.write(output + "\n");

            reader.close();
            fr.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void getTotalPriceOfPaidOrdersPerYear() {

        try
        {
            FileReader fr = new FileReader(file);

            BufferedReader reader = new BufferedReader(fr);

            String line;

            while((line = reader.readLine()) != null) {
                getPriceFromOrder(line);
            }
            reader.close();
            fr.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        String output = "";

        for (Integer year : years.keySet()) {
            output += "Total price per year " + year + " is " + years.get(year) + ".\n";
        }

        printToOutputFile(output);
    }

    public void getTop3CustomersWithMostOrders() {
        HashMap<String, Integer> usersOrderMap = new HashMap<>();
        try
        {
            FileReader fr = new FileReader(file);

            BufferedReader reader = new BufferedReader(fr);

            String line;

            while((line = reader.readLine()) != null) {
                storeUserWithOrders(line, usersOrderMap);
            }
            reader.close();
            fr.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        Map sortedMap = getMap(usersOrderMap);

        String output = "TOP 3 CUSTOMERS WITH MOST ORDERS" + "\n";
        for (int i = 0; i < sortedMap.entrySet().toArray().length; i++) {
            if (i < 3) {
                output += "" + (i+1) + ". " + sortedMap.entrySet().toArray()[i] + "\n";
            }
        }
        printToOutputFile(output);
    }

    private Map getMap(HashMap<String, Integer> usersOrderMap) {
        return usersOrderMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private void storeUserWithOrders(String order, HashMap<String, Integer> usersOrderMap) {
        int currentCount = 1;
        String[] values = order.split(",");
        if (values.length < 3 || values[EMAIL_INDEX].isEmpty())
            return;
        if (usersOrderMap.containsKey(values[EMAIL_INDEX])) {
            currentCount = usersOrderMap.get(values[EMAIL_INDEX]) + 1;
        }
        usersOrderMap.put(values[EMAIL_INDEX], currentCount);
    }

    private void getPriceFromOrder(String order, boolean isOrderPaid) {
        String[] values = order.split(",");
        if (values.length < 5) {
            return;
        }
        if ((isOrderPaid && values[STATUS_INDEX].equals("PAID")) ||
                !isOrderPaid && values[STATUS_INDEX].equals("UNPAID")) {

            getDate(values);
            Integer year = date.getYear() + 1900;
            Integer currentValue = years.get(year);
            years.replace(year, currentValue + Integer.parseInt(values[PRICE_INDEX]));
        }
    }

    private void getPriceFromOrder(String order) {
        String[] values = order.split(",");
        if (values.length < 5) {
            return;
        }

        getDate(values);
        Integer year = date.getYear() + 1900;
        Integer currentValue = years.get(year);
        years.replace(year, currentValue + Integer.parseInt(values[PRICE_INDEX]));
    }

    private void getDate(String[] values) {
        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(values[DATE_INDEX]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean hasEmptyAddress(String line) {
        String[] values = line.split(",");
        return values[ADRESS_INDEX].isEmpty() || values.length < 3;
    }

    private boolean hasEmptyEmail(String line) {
        String[] values = line.split(",");
        return values[EMAIL_INDEX].isEmpty() || values.length < 2;
    }

    public void setFile(String filePath) {
        this.file = new File("initFile.csv");
        try {
            if (filePath.startsWith("http")) {
                URL url = new URL(filePath);
                InputStream is = url.openStream();
                copyFileUsingStream(is, file);
                is.close();
            } else {
                copyFileUsingStream(new FileInputStream(new File(filePath)), file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOutputFile(String outputFilePath) {
        outputFile = new File(outputFilePath);
    }

    public void fillMapOfYears() {
        years = new HashMap<>();
        ordersCountInYear = new HashMap<>();
        try
        {
            FileReader fr = new FileReader(file);

            BufferedReader reader = new BufferedReader(fr);

            String line;

            while((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Date date = new SimpleDateFormat("dd.MM.yyyy").parse(values[DATE_INDEX]);
                Integer year = date.getYear() + 1900;
                updateMapOfYears(year);
            }
            reader.close();
            fr.close();
        }
        catch(IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void updateMapOfYears(Integer year) {
        if (!years.containsKey(year)) {
            years.put(year, 0);
            ordersCountInYear.put(year, 1);
        } else {
            Integer currentValue = ordersCountInYear.get(year);
            ordersCountInYear.replace(year, currentValue + 1);
        }
    }

    private static void copyFileUsingStream(InputStream source, File dest) throws IOException {
        PrintWriter writer = new PrintWriter(dest);
        writer.print("");
        writer.close();
        try (InputStream is = source; OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
