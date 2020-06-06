// Transaction.java
/*
 (provided code)
 Transaction is just a dumb struct to hold
 one transaction. Supports toString.
*/
public class Transaction {
	public int from;
	public int to;
	public int amount;
	
   	public Transaction(int from, int to, int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public int getAmount(){
   		return amount;
	}

	public int getFrom(){
   		return from;
	}

	public int getTo(){
   		return to;
	}

	@Override
	public boolean equals(Object obj) {
		Transaction trans = (Transaction)obj;
		return trans.getFrom() == from && trans.getTo() == to && trans.getAmount() == amount;
	}

	public String toString() {
		return("from:" + from + " to:" + to + " amount:" + amount);
	}
}
