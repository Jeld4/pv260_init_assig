import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CommandParser {

    private String[] commandStr;

    private LinkedHashMap<String, List<String>> params;

    private FileManager fileManager;

    public CommandParser(String[] commandStr) {
        this.fileManager = new FileManager();
        this.commandStr = commandStr;
        this.params = new LinkedHashMap<>();

        // if the input is correct
        if (parseCommandLine()) {

            fileManager.setFile(params.get("-d").get(0));

            fileManager.fillMapOfYears();
            processCommands();
        }
    }

    private void processCommands() {
        String outputFilePath = params.get("-o").get(1);
        fileManager.setOutputFile(outputFilePath);

        for (String manipulationMethod : params.get("-m")) {
            switch (manipulationMethod) {
                case "AVG_PAID":
                    fileManager.getAveragePriceOfOrdersPerYear(true);
                    break;
                case "AVG_UNPAID":
                    fileManager.getAveragePriceOfOrdersPerYear(false);
                    break;
                case "TOTAL_PRICE":
                    fileManager.getTotalPriceOfPaidOrdersPerYear();
                    break;
                case "TOP3_CUSTOMERS":
                    fileManager.getTop3CustomersWithMostOrders();
                    break;
                case "REMOVE_EMPTYEMAIL":
                    fileManager.processFilterAction("emptyEmail");
                    break;
                case "REMOVE_EMPTYADRESS":
                    fileManager.processFilterAction("emptyAddress");
                    break;
                default:
                    System.err.println("Invalid manipulation method! (" + manipulationMethod + ")");
                    return;
            }
        }
    }

    private boolean parseCommandLine() {

        if (commandStr.length < 6 ) {
            System.err.println("Invalid input!");
            return false;
        }

        List<String> options = null;
        for (final String input : commandStr) {
            if (input.charAt(0) == '-') {
                if (input.length() != 2) {
                    System.err.println("Error at argument " + input + "!");
                    return false;
                }

                options = new ArrayList<>();
                params.put(input, options);
            } else if (options != null) {
                options.add(input);
            } else {
                System.err.println("Illegal parameter usage!");
                return false;
            }
        }

        if (!params.keySet().contains("-d") || !params.keySet().contains("-m") || !params.keySet().contains("-o")) {
            System.err.println("Missing parameters!");
            return false;
        }
        return true;
    }

}
