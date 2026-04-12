package ans;

// Holds data for a single product fetched from the DB
public class Product
{
    private final int id;
    private final String title;
    private final double price;
    private final String imagePath;
    private final String description;
    private final boolean isBookable; // true if product requires a booking date


    public Product(int id, String title, double price, String imagePath, String description, boolean isBookable)
    {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imagePath = imagePath;
        this.description = description;
        this.isBookable = isBookable;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }
    public String getDescription() { return description; }
    public boolean isBookable() { return isBookable;  }
}