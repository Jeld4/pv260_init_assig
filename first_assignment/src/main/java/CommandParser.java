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
            setFile();
            fileManager.fillMapOfYears();
            processCommands();
        }
    }

    private void setFile() {
        fileManager.setFile(params.get("-d").get(0));
    }

    private void processCommands() {
        setOutputFile();

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

    private void setOutputFile() {
        String outputFilePath = params.get("-o").get(1);
        fileManager.setOutputFile(outputFilePath);
    }

    private boolean parseCommandLine() {

        if (isCommandFull()) return false;

        List<String> options = null;
        for (final String input : commandStr) {
            if (input.charAt(0) == '-') {
                if (isValidCommand(input)) return false;

                options = storeNewCommand(input);
            } else if (options != null) {
                options.add(input);
            } else {
                System.err.println("Illegal parameter usage!");
                return false;
            }
        }

        return !missingMandatoryParameters();
    }

    private boolean missingMandatoryParameters() {
        if (!params.containsKey("-d") || !params.containsKey("-m") || !params.containsKey("-o")) {
            System.err.println("Missing parameters!");
            return true;
        }
        return false;
    }

    private List<String> storeNewCommand(String input) {
        List<String> options;
        options = new ArrayList<>();
        params.put(input, options);
        return options;
    }

    private boolean isValidCommand(String input) {
        if (input.length() != 2) {
            System.err.println("Error at argument " + input + "!");
            return true;
        }
        return false;
    }

    private boolean isCommandFull() {
        if (commandStr.length < 6 ) {
            System.err.println("Invalid input!");
            return true;
        }
        return false;
    }

}
