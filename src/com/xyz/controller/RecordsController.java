package com.xyz.controller;

import com.xyz.controllers.base.BaseController;
import com.xyz.db.DBAccess;
import com.xyz.log.Logger;

/**
 * Middle layer controller that is responsible for orchestrating record
 * operations in the XYZ application. Note that some operations may require
 * semaphore access to the parallel coordinator. Not all operations are allowed
 * for all callees and will result in Level-II exceptions, which will be
 * reported on the XYZ SIEM systems.
 * 
 * @author Tony Bennett (tbennett@xyz.crooners.com)
 *
 */
@Controller
public class RecordsController extends BaseController {

	Logger log = Logger.NewLogger();

	protected DBAccess _dbAccess;

	/**
	 * Constructs the records controller that will respond to record operations from
	 * the user interface as well as publicly available APIs.
	 */
	public RecordsController() {
		_dbAccess = super.getDBAccess();
	}

	/**
	 * Gets the first from a list of Customers that match the given name.
	 * 
	 * @param name name of customer to match
	 * @return the first customer that matches given name
	 */
	@GetMapping(value = "/customer/{name}")
	@ResponseBody
	public Customer getFirstCustomerByName(@PathVariable("name") String name, HttpServletRequest request,
			HttpServletResponse response) {
		
		Customer customer = null;
		
		// Ensure we have a customer name to work with
		if (StringUtils.isBlank(name)) {
			log.error("Provided name was empty. Nothing to see here. Move along.");
			return customer;
		}
		
		// Request the database for all customers that match given name
		List<Customer> customers = _dbAccess.getCustomer(name);
		
		// Read the first customer from list if not empty
		if (CollectionUtils.isNotEmpty(customers)) {
			customer = customers.get(0);
		}
		
		return customer;
	}

	/**
	 * Updates records with given address for customers that match given name.
	 * 
	 * @param name    customer name to match
	 * @param address address to use
	 * @return number of customers that were updated
	 */
	@RequestMapping(value = "/customer/address/{address}", method = RequestMethod.POST)
	@ResponseBody
	public Integer updateCustomerAddressByName(@RequestParam String name, @RequestParam String address) {
		
		// Ensure we have a customer name to work with
		if (StringUtils.isBlank(name) || StringUtils.isBlank(address)) {
			log.error("Provided name or address was empty. Nothing to see here. Move along.");
			return customer;
		}
		
		// Update address for customers that match given name
		Integer nUpdated = _dbAccess.updateCustomerAddress(name, address);
		return nUpdated;
	}
}
