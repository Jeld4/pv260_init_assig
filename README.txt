Here are some examples, how to execute my solution.

-d path of local or remote file - expects double \\ in the path

-m MANIPULATION METHODS:

AVG_PAID           -- calculates the average price of paid orders per year            
AVG_UNPAID         -- calculates the average price of unpaid orders per year
TOTAL_PRICE        -- calculates the total price of orders per year
TOP3_CUSTOMERS     -- top 3 customers with the highest number of orders

REMOVE_EMPTYEMAIL  -- method removes items with empty customer's email
REMOVE_EMPTYADRESS -- method removes items with empty customer's address

-o this app works with plaintext output type and file

----EXAMPLES------
REMOTE ex:
-d https://is.muni.cz//el//fi//jaro2020//PV260//um//seminars//java_groups//initial_assignment//orders_data.csv -m REMOVE_EMPTYEMAIL TOP3_CUSTOMERS TOTAL_PRICE -o PLAINTEXT D:\\School\\Java\\result.txt

LOCAL ex:
-d D:\\School\\Java\\orders_data.csv -m REMOVE_EMPTYADRESS REMOVE_EMPTYEMAIL AVG_PAID AVG_UNPAID TOP3_CUSTOMERS TOTAL_PRICE -o PLAINTEXT D:\\School\\Java\\result.txt


And here is the executable da-tool.jar
\out\artifacts\da_tool_jar