package gui;


public class cartObjects {

    public String getStockID() {
        return stockID;
    }

    
    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    
    public String getProductName() {
        return productName;
    }

    
    public void setProductName(String productName) {
        this.productName = productName;
    }

    
    public String getSize() {
        return size;
    }

   
    public void setSize(String size) {
        this.size = size;
    }

    
    public String getQty() {
        return qty;
    }

  
    public void setQty(String qty) {
        this.qty = qty;
    }

    
    public String getPrice() {
        return price;
    }

    
    public void setPrice(String price) {
        this.price = price;
    }
    private String stockID;
    private String productName;
    private String size;
    private String qty;
    private String price;
    private String details;


    public String getDetails() {
        return details;
    }


    public void setDetails(String details) {
        this.details = details;
    }
}
