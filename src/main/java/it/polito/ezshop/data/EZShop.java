package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;
import java.time.LocalDate;
import java.util.*;

public class EZShop implements EZShopInterface {
	

	private HashMap<String,ProductType> products = new HashMap<String,ProductType>();
	private HashMap<Integer, SaleTransaction> closedSaleTransactions = new HashMap<Integer, SaleTransaction>();
    private HashMap<Integer, ReturnTransactionImpl> openedReturnTransactions = new HashMap<Integer, ReturnTransactionImpl>();
    private HashMap<Integer, User> users = new HashMap<Integer, User>();
	private HashMap<Integer, Customer> customers = new HashMap<Integer, Customer>();
	private HashMap<Integer, Order> orders = new HashMap<Integer, Order>();
    
    /*
     Questa variabile rappresenta il bilancio corrente del sistema (=/= balanceOperation che invece rappresenta una singola operazione)
     Va inizializzata (=0) dentro il metodo reset() 
     */
    private double balance;
    
    /*ipotesi: solo uno alla volta può accedere al sistema, 
     * non posso aprire due GUI contemporaneamente ed accedervi
     * verificare che loggedUSer può essere null*/
    private User loggedUser=null;
    
    /*da qualche parte nel main:
    *loggedUser è un attributo private quindi quando chiamo l'istruzione dal main ho un errore
    * quindi implementare una setLoggedUser public che mi restitusce l'utente loggato*/
    //ezshop.setLoggedUser(ezshop.login(pass,username);
    
    //quindi poi per controllare i diritti di utilizzo di un metodo:
	//if(this.userLogged.getRole() != 'administrator')
	//((	throw new UnauthorizedException();

    

    public void reset() {

    }

@Override
public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
	
	User us = new UserImpl();
	
    if(!username.isEmpty() || username!=null)
	{	
    	//L'username deve essere univoco
    	for(User user : users.values())
		{
			if(user.getUsername().contains(username))
			{
				return -1;
			}
		}
			
		if(!password.isEmpty() || password!=null)
		
		{
			if(!role.isEmpty() || role!=null) {

				us.setUsername(username);
				us.setPassword(password);
				us.setRole(role);
				users.put(us.getId(),us);
			
			}
			else {
					throw new InvalidRoleException();
			 	  }
		}	
		 else {
			throw new InvalidPasswordException();
				}
	}
    else {
		throw new InvalidUsernameException();
		  }
				
			return us.getId();
}

@Override
public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
	//TODO: gestire eccezione per utente non autorizzato
	
	if(id == 0 || id == null || id > users.size()) 
		throw new InvalidUserIdException();
	
		users.remove(id);
	
	//checking if user has really been removed
		if(users.containsKey(id)) {
			return false;
		}
		else {
			return true;
		}
	
}

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
    	//TODO: gestire eccezione per utente non autorizzato
    	
    	//if(this.userLogged.getRole() != 'administrator')
    	//	throw new UnauthorizedException();
    	return new ArrayList<User>(users.values());
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
    	//TODO gestire eccezione per utente non autorizzato
    	
    	if(id <= 0 || id == null || id > users.size())
    		throw new InvalidUserIdException();
    	
    	User us = new UserImpl();
    	
    	if(users.containsKey(id))
    		us = users.get(id);

    	return us;

    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
    	//TODO gestire eccezione per utente non autorizzato
    	if( id <= 0 || id==null)
        	throw new InvalidUserIdException();
       
        if (role.isEmpty() || role == null)
        	throw new InvalidRoleException();
        
        for(User user : users.values())
        	
        	if(user.getId() == id) {
        		user.setRole(role);
        		}
        	else {
        		return false;
        	}
        
        return true;
    }
    


    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
    	
    	if(username == null || username.isEmpty())
    		throw new InvalidUsernameException();
    	if(password == null || password.isEmpty())
    		throw new InvalidPasswordException();
    	
    	
    	
    	for(User user : users.values())
		{
			if(user.getUsername().contains(username) && user.getPassword().contains(password)) {
					return user;
							
			}

				
		}
    	return null;
    	

    }

    @Override
    public boolean logout() {
    	if(this.loggedUser == null)
    		return false;
    	else {
    		this.loggedUser=null;
    		return true;
    	}
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
    	//TODO gestire eccezione per utente non autorizzate
    	
        if (description.isEmpty() || description == null)
        	throw new InvalidProductDescriptionException();
        
        if (productCode.isEmpty() || productCode == null)
        	throw new InvalidProductCodeException();

        if (pricePerUnit <= 0 || pricePerUnit == 0)
        	throw new InvalidPricePerUnitException();
        
        //BarCode must be unique
        
		for(ProductType product : products.values())
		{
			if(product.getBarCode().equals(productCode))
			{
				return -1;
			}
		}
		
		ProductType pt = new ProductTypeImpl();
		pt.setBarCode(productCode);
		pt.setProductDescription(description);
		pt.setNote(note);
		pt.setPricePerUnit(pricePerUnit);
		products.put(pt.getBarCode(),pt);

        
        return pt.getId();
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
    	//TODO gestire eccezione per utente non autorizzate
        if (newDescription.isEmpty() || newDescription == null)
        	throw new InvalidProductDescriptionException();
        
        if (newCode.isEmpty() || newCode == null)
        	throw new InvalidProductCodeException();

        if (newPrice <= 0 || newPrice == 0)
        	throw new InvalidPricePerUnitException();
        if (id <= 0 || id == 0)
        	throw new InvalidPricePerUnitException();
        
       for ( ProductType product : products.values())
       {
    	   if(product.getBarCode().equals(newCode))
    	   {
    		   return false;
    	   } 
       }
    	   
    	for ( ProductType product : products.values())
    	{
    		if(product.getId()==id)
    		{
    			product.setBarCode(newCode);
    			product.setProductDescription(newDescription);
    			product.setNote(newNote);
    			product.setPricePerUnit(newPrice);
    		 }
    	 }

    	   return true;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
    	if(id == 0 || id == null) 
    		throw new InvalidProductIdException();
    	
    	for(ProductType product : products.values())
		{
			if(product.getId().equals(id))
			{	
				String BarCodeToRemove;
				BarCodeToRemove = product.getBarCode();
				products.remove(BarCodeToRemove);
				}
			else {
				return false;
			}
			
		}
    	return true;

    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
    	//TODO gestire eccezione per utente non autorizzate
    	return new ArrayList<ProductType>(products.values());
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
    	if((barCode != null) && (barCode.length() != 0) && (barCodeIsValid(barCode)))
    	{
    		for(ProductType product : products.values()) 
    		{
    	        if (product.getBarCode().equals(barCode)) 
    	        {
    	            return product;
    	        }
    	    }
    	}
    	else
    	{
    		throw new InvalidProductCodeException();
    	}

    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
    	
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
        List<ProductType> searchedProducts = new ArrayList<ProductType>();
        if (description.length()==0 || description == null) 
        	description = "";
        
        for(ProductType product : products.values())
        {
        	if(product.getProductDescription().contains(description))
        	{
        		searchedProducts.add(product);
        	}
        }
        
        return searchedProducts;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
    	
        int newQuantity = 0;
        
        if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
        
        if(productId>0 && productId!=null)
        {
        	for(ProductType product : products.values())
        	{
        		if(product.getId()==productId)
        		{
        			if(product.getLocation().length()!=0 && product.getLocation()!=null)
        			{
        				newQuantity = product.getQuantity() + toBeAdded;
            			if(newQuantity>=0)
            			{
            				product.setQuantity(newQuantity);
            				return true;
            			}
            			
            			return false;
        			}
        			else 
        			{
        				return false;
        			}
        		}
        	}
        }
    	
    	//Non ho trovato il prodotto
    	return false;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
    	if(productId>0 && productId!=null)
    	{
    		if(newPos.matches("[0-9]+[-][a-zA-Z]+[-][0-9]+"))
    		{
    			//La location deve essere univoca
				//Scorro tutti i prodotti per vedere se esiste già la locazione
				//Se esiste non è univoca e ritorno false
    			for(ProductType product : products.values())
    			{
    				if(product.getLocation().equals(newPos))
    				{
    					return false;
    				}
    			}
    			
    			//La location è univoca
    			//Aggiorno il prodotto alla nuova locazione
    			for(ProductType product : products.values())
    			{
    				if(product.getId()==productId)
    				{
    					product.setLocation(newPos);
    					return true;
    				}
    			}
    			
    		}
    		else
    		{
    			throw new InvalidLocationException();
    		}
    	}
    	else
    	{
    		throw new InvalidProductIdException();
    	}
    	
    	return false;
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
    	if((productCode != null) && (productCode.length() != 0) && (barCodeIsValid(productCode)))
    	{
    		if(quantity>0)
    		{
    			if(pricePerUnit>0)
    			{
    				//Creo nuovo ordine e lo aggiungo alla lista
					Order o = new OrderImpl(productCode, pricePerUnit, quantity);
					orders.put(o.getOrderId(), o);
					return o.getOrderId();
    			}
    			else
    			{
    				throw new InvalidPricePerUnitException();
    			}
    		}
    		else
    		{
    			throw new InvalidQuantityException();
    		}
    	}
    	else
    	{
    		throw new InvalidProductCodeException();
    	}
    	
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
    	if((productCode != null) && (productCode.length() != 0) && (barCodeIsValid(productCode)))
    	{
    		if(quantity>0)
    		{
    			if(pricePerUnit>0)
    			{
					
					if(this.balance >= (quantity*pricePerUnit))
		    		{
						//Creo balance operation relativa al precedente ordine
		    			BalanceOperation bp = new BalanceOperationImpl(quantity*pricePerUnit, "ORDER");
		    			//Creo nuovo ordine e lo aggiungo alla lista
						Order o = new OrderImpl(productCode, pricePerUnit, quantity);
						o.setStatus("PAYED");
						o.setBalanceId(bp.getBalanceId());
						orders.put(o.getOrderId(), o);
		    			//Decremento bilancio
		    			this.balance -= quantity*pricePerUnit;
		    			return o.getOrderId();
		    		}
		    		else
		    		{
		    			return -1;
		    		}
					
    			}
    			else
    			{
    				throw new InvalidPricePerUnitException();
    			}
    		}
    		else
    		{
    			throw new InvalidQuantityException();
    		}
    	}
    	else
    	{
    		throw new InvalidProductCodeException();
    	}
    	
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
    	if((orderId!=null) && (orderId>=0))
    	{
    		if(orders.containsKey(orderId))
    		{
    			//L'ordine è presente
    			if((orders.get(orderId).getStatus()=="ISSUED") || (orders.get(orderId).getStatus()=="PAYED"))
    			{
    				if(orders.get(orderId).getStatus()=="PAYED")
    				{
    					//L'ordine è già stato pagato torno senza fare nulla
    					return true;
    				}
    				
    				//L'ordine è in stato ISSUED quindi va pagato
    				double pricePerUnit = orders.get(orderId).getPricePerUnit();
    				int quantity = orders.get(orderId).getQuantity();
    				if(this.balance >= (quantity*pricePerUnit))
    				{
    					BalanceOperation bp = new BalanceOperationImpl(quantity*pricePerUnit, "ORDER");
        				orders.get(orderId).setBalanceId(bp.getBalanceId());
        				orders.get(orderId).setStatus("PAYED");
        				this.balance -= quantity*pricePerUnit;
        				return true;
    				}
    				else
    				{
    					return false;
    				}
    				
    			}
    			else
    			{
    				return false;
    			}
    			
    		}
    		else
    		{
    			return false;
    		}
    	}
    	else
    	{
    		throw new InvalidOrderIdException();
    	}
    	
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
    	if((orderId!=null) && (orderId>0))
    	{
    		if(orders.containsKey(orderId))
    		{
	    		ProductType p = products.get(orders.get(orderId).getProductCode());
	    		Order o = orders.get(orderId);
	    		if((p.getLocation()!=null) && (!p.getLocation().isEmpty()))
	    		{
	    			if(orders.get(orderId).getStatus()=="PAYED")
	    			{
	    				//L'ordine è stato pagato ed è arrivato
	    				//Aumento la quantità del prodotto e imposto lo stato dell'ordine a completato
	    				String barCode = p.getBarCode();
	    				int oldQuantity = p.getQuantity();
	    				int quantityToAdd = o.getQuantity();
	    				products.get(barCode).setQuantity(oldQuantity + quantityToAdd);
	    				orders.get(orderId).setStatus("COMPLETED");
	    				return true;
	    			}
	    			else if(orders.get(orderId).getStatus()=="COMPLETED")
	    			{
	    				//Se l'ordine è stato già completato il metodo non fa nulla
	    				return true;
	    			}
	    			else
	    			{
	    				//L'ordine non è né in stato PAYED né in stato completed
	    				return false;
	    			}
	    		}
	    		else
	    		{
	    			//L'ordine non ha una locazione
	    			throw new InvalidLocationException();
	    		}
    		}
    		else
    		{
    			//L'ordine non esiste
    			return false;
    		}
    	}
    	else
    	{
    		throw new InvalidOrderIdException();
    	}
    	
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
    	
    	if(this.loggedUser==null || this.loggedUser.getRole().contains("Cashier"))
    		throw new UnauthorizedException();
    	
    	List<Order> searchedOrders = new ArrayList<Order>();
    	for(Order order : orders.values())
        {
        		searchedOrders.add(order);
        }
    	
        return searchedOrders;
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {


    	if(this.loggedUser==null)
    		throw new UnauthorizedException();
    	
    	for(Customer customer : customers.values())
    	{
    		//Devo controllare che il nome che sto inserendo non sia già di un altro customer
    		//deve essere univoco
    		if(customer.getCustomerName()==customerName)
    		{
    			return -1;
    		}
    	}
    	//Il nome è univoco
    	
    	if((customerName!=null) && (!customerName.isEmpty()))
    	{
    		Customer customer = new CustomerImpl(customerName);
    		customers.put(customer.getId(), customer);
    		return customer.getId();
    	}
    	else
    	{
    		throw new InvalidCustomerNameException();
    	}
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        
    	if(this.loggedUser==null)
    		throw new UnauthorizedException();
    	
    	//Il nome non deve essere nullo o vuoto
    	if((newCustomerName==null) || (newCustomerName.isEmpty()))
    		throw new InvalidCustomerNameException();
    	//Il nome deve essere unico (dovrebbe, dalla documentazione del metodo non si capisce ma nel precedente metodo era così)
    	for(Customer customer : customers.values())
    	{
    		//Devo scorrere tutti i clienti tranne quello attuale, sennò il nome risulterà sempre duplicato (nel caso in cui non venisse modificato)
    		if(customer.getId()!=id)
    		{
        		//Devo controllare che il nome che sto inserendo non sia già di un altro customer
        		//deve essere univoco
    			if(customer.getCustomerName()==newCustomerName)
        		{
        			return false;
        		}
    			
    		}
    	}
    	//Aggiorno il nome del cliente
		customers.get(id).setCustomerName(newCustomerName);
    	
    	//Se la nuova carta è nulla si conclude qui l'operazione
    	if(newCustomerCard==null)
    		return true;
    	//Se la nuova carta è una stringa vuota si stacca la carta dal cliente
    	if(newCustomerCard.isEmpty())
    	{
    		customers.get(id).setCustomerCard(null);
    		return true;
    	}
    	//Se la nuova carta è del formato corretto (10 digits) possiamo procedere all'aggiornamento
    	if(newCustomerCard.matches("[0-9]{10}")) 
    	{
    		//controllo che il codice della carta non sia già di qualche utente
    		for(Customer customer : customers.values())
    		{
    			//Se la carta è già in possesso di qualcuno ritorno falso
    			if(customer.getCustomerCard()==newCustomerCard)
    			{
    				return false;
    			}
    		}
    		//La carta non è in possesso di nessun cliente
    		//Aggiorno il cliente
    		customers.get(id).setCustomerCard(newCustomerCard);
    		return true;
        	
    	}
    	else
    	{
    		throw new InvalidCustomerCardException();
    	}
    		
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
    	
    	if(this.loggedUser==null)
    		throw new UnauthorizedException();
    	
    	if((id!=null) && (id>0))
    	{
    		if(customers.containsKey(id))
    		{
    			//Rimuovo il customer
    			customers.remove(id);
    			return true;
    		}
    		else
    		{
    			//Il customer non esiste
    			return false;
    		}
    	}
    	else
    	{
    		throw new InvalidCustomerIdException();
    	}
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
    	// TODO add check for logged user
        return new ArrayList<Customer>(customers.values());
    }

    @Override
    public String createCard() throws UnauthorizedException {
    	// TODO add check for logged user
    	List<Customer> customersList = getAllCustomers();
    	// If the db is not reachable, return an empty string
    	if(customersList == null)
    		return "";
    	// Else loop until you generate a unique CardId and return it
    	while(true) {
    	String retCard = UUID.randomUUID().toString();
    	if(customersList.stream().filter(c -> !retCard.equals(c.getCustomerCard())).count() == 0)
    		return retCard;
    	}
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
    	// TODO add check for logged user
    	// Check for exceptions
    	if(customerId == null || customerId <= 0)
    		throw new InvalidCustomerIdException();
    	if(customerCard == null || customerCard.isEmpty())
    		throw new InvalidCustomerCardException();
    	List<Customer> customersList = getAllCustomers();
    	// If the db is not reachable, return an empty string
    	if(customersList == null)
    		return false;
    	// Check if no other customer is using the same card and if the customer exists. If true, assign the card
    	if(customersList.stream().filter(c -> !customerCard.equals(c.getCustomerCard())).count() == 0 && customers.containsKey(customerId)) {
			customers.get(customerId).setCustomerCard(customerCard);
    		return true;
    	}
    	return false;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
    	return false;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
    	return null;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
    	return false;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
    	return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
    	return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
    	return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
        return 0;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
        return false;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
    	// TODO add check for logged user
    	// TODO mars: to be implemented
        return false;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        //TODO Ruggero
    	//TODO gestire eccezione per utente non autorizzato
    	if(transactionId <= 0 || transactionId == null)
    		throw new InvalidTransactionIdException();
    	
    	SaleTransaction st = null;
    	
    	if(closedSaleTransactions.containsKey(transactionId))
    		st = closedSaleTransactions.get(transactionId);

    	return st;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        //TODO gestire eccezione per utente non autorizzato
    	if(saleNumber <= 0 || saleNumber == null)
    		throw new InvalidTransactionIdException();
    	
    	if(!closedSaleTransactions.containsKey(saleNumber))
    		return -1;
    	
    	ReturnTransactionImpl rt = new ReturnTransactionImpl();
    	rt.setSaleTransaction(closedSaleTransactions.get(saleNumber));
    	openedReturnTransactions.put(rt.getId(), rt);
    	
    	return rt.getId();
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        //TODO gestire eccezione per utente non autorizzato
    	if(returnId <= 0 || returnId == null)
    		throw new InvalidTransactionIdException();
    	
    	if(productCode == null || productCode.isEmpty() || !barCodeIsValid(productCode))
			throw new InvalidProductCodeException();
    	
    	if(amount <= 0)
    		throw new InvalidQuantityException();
    	
    	if(!products.containsKey(productCode) || !openedReturnTransactions.containsKey(returnId))
    		return false;
    	
    	ReturnTransactionImpl rt = openedReturnTransactions.get(returnId);
    	SaleTransaction st = rt.getSaleTransaction();
    	TicketEntry te;
    	
    	try {
    		 te = st.getEntries().stream().filter((TicketEntry t) -> {return t.getBarCode().equals(productCode);}).findFirst().get();
    	}catch(NoSuchElementException e) {
    		return false;
    	}
    	
    	if(te.getAmount() < amount)
    		return false;
    	
    	try {
    		Optional<TicketEntry> ote = rt.getEntry(productCode);
    		if(ote.get().getAmount() + amount > te.getAmount()) {
    			return false;
    		} else {
    			ote.get().setAmount(ote.get().getAmount() + amount);
    		}
    	}catch(NoSuchElementException e) {
        	rt.addEntry(new TicketEntryImpl(productCode, amount));
    	}
    	return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return false;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        return false;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        return null;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        return 0;
    }
    
    //Metodo per verificare la validità di un barcode
    // https://www.gs1.org/services/how-calculate-check-digit-manually
    private static boolean barCodeIsValid(String barCode) {
    	int bcSize = barCode.length();
    	boolean r = false;
    	
    	if(barCode.matches("[0-9]{12,14}"))
    	{
    		//Il bar code deve essere una stringa composta da numeri
    	
	    	if( (bcSize == 12) || (bcSize == 13) || (bcSize == 14) )
	    	{
	    		int sum = 0;
	    		int mul; //Può essere 1 o 3
	    		int digit; 
	    		switch(bcSize)
	    		{
		    		case 12:
		    			for(int i = 0; i < 11; i++)
		    			{
		    				mul = (i%2 == 0) ? 3 : 1;
		    				digit = Integer.parseInt(Character.toString(barCode.charAt(i))); //Estraggo i-esima cifra
		    				digit *= mul; //Moltiplico per 1 o 3
		    				sum += digit; //Sommo
		    			}
		    			sum = RoundUp(sum) - sum;
		    			if(sum == Integer.parseInt(Character.toString(barCode.charAt(11)))){
		    				r = true;
		    			}else {
		    				r = false;
		    			}
		    			break;
		    		case 13:
		    			for(int i = 0; i < 12; i++)
		    			{
		    				mul = (i%2 == 0) ? 1 : 3;
		    				digit = Integer.parseInt(Character.toString(barCode.charAt(i))); //Estraggo i-esima cifra
		    				digit *= mul; //Moltiplico per 1 o 3
		    				sum += digit; //Sommo
		    			}
		    			sum = RoundUp(sum) - sum;
		    			if(sum == Integer.parseInt(Character.toString(barCode.charAt(12)))){
		    				r = true;
		    			}else {
		    				r = false;
		    			}
		    			break;
		    		case 14:
		    			for(int i = 0; i < 13; i++)
		    			{
		    				mul = (i%2 == 0) ? 3 : 1;
		    				digit = Integer.parseInt(Character.toString(barCode.charAt(i))); //Estraggo i-esima cifra
		    				digit *= mul; //Moltiplico per 1 o 3
		    				sum += digit; //Sommo
		    			}
		    			sum = RoundUp(sum) - sum;
		    			if(sum == Integer.parseInt(Character.toString(barCode.charAt(13)))){
		    				r = true;
		    			}else {
		    				r = false;
		    			}
		    			break;
	    		}
	    		
	    	}
	    	else
	    	{
	    		r = false;
	    	}
    	}
    	else
    	{
    		r = false;
    	}
    	
    	return r;
    }
    
    //Metodo per arrotondare al multiplo di 10 successivo
    //Necessario per la validazione del barcode
    private static int RoundUp(int toRound)
    {
        if (toRound % 10 == 0) return toRound;
        return (10 - toRound % 10) + toRound;
    }
}
