package menu.pojo;

public class Item {
    int id;
    String name;
    String description;
    String ingredient;
    double weight;
    double price;
    String categoryName;
    int categoryId;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return  "Id: " + id +
                "\nName: " + name +
                "\nCategory: " + categoryName.toUpperCase() +
                "\nDescription: " + description +
                "\nIngredient: " + ingredient +
                "\nWeight: " + weight + "g"+
                "\nPrice: $" + price ;
    }

}
