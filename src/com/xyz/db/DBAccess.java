package com.xyz.db;

import com.custom.filedb.CustomJDBC;
import com.custom.filedb.FreeformQuery;
import com.custom.filedb.DataSource;
import com.custom.filedb.CustomJdbcBuilder;
import com.xyz.log.Logger;

/**
 * A custom DB Access API to help interface with the custom file-based database developed here at XYZ.
 * It uses a JDBC-like interface developed to provide freeform query exeuction using the SQL syntax.
 * For more information on the custom DB, refer to the internal wikispace:
 * http://custom.filedb.xyzcorp/wiki/CustomJdbc/latest
 * 
 * @author Sammy Davis Jr. (sdavisjr@xyz.crooners.com)
 *
 */
public class DBAccess {

	protected Logger log = Logger.NewLogger();
	
	protected CustomJdbc customJdbc;
	private DataSource dataSource;
	

	/**
	 * Constructs the DBAccess object. This will be used in components that need
	 * access to the file based DB system. Refer to
	 * http://custom.filedb.xyzcorp.com/customDBAccess/usage.html for additional
	 * information.
	 */
	public DBAccess() {
		dataSource = DataSource.initDS("host=127.0.0.1,path=\\share\\local\\dbfile.cust");
		customJdbc = new CustomJdbcBuilder().connectionProvider(dataSource).build();
	}

	/**
	 * Fetch a list of Customer objects by given name.
	 * 
	 * @param name the name of the customer
	 * @return a list of Customer objects
	 */
	public List<Customer> getCustomer(String name) {
		try {
			// New query object
			FreeformQuery query = customJdbc.query(QueryType.FREEFORM);
			// Create the custom object mapper for the Customer class
			CustomObjectMapper customerMapper = CustomObjectMapper.from(Customer.class);
			// Get the results from query execution
			QueryResults results = query.execFreeform("SELECT * FROM CUSTOMER WHERE NAME = '" + name + "'");
			// Generate list of customers from query execution results
			List<Customer> customers = results.getList(customerMapper);
		} catch (QueryException qe) {
			log.error("Could not fetch Customer records from " + dataSource.getConnection().getSchema() + " for name="
					+ name);
		}
		return customers;
	}

	/**
	 * Updates customer records with given address by name, and returns the number
	 * of Customer records that were updated.
	 * 
	 * @param name    Name of customer to match
	 * @param address Address to use for update
	 * @return Number of Customers that were updated
	 */
	public int updateCustomerAddress(String name, String address) {
		// New query object
		FreeformQuery query = customJdbc.query(QueryType.FREEFORM);
		// Get the update count from query execution
		int updateCount = query
				.updateFreeform("UPDATE CUSTOMER SET ADDRESS = '" + address + "' WHERE NAME = '" + name + "'");
		log.debug("Updated " + updateCount + " customers for name=" + name + ", address=" + address);
		return updateCount;
	}
}
