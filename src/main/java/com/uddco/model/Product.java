package com.uddco.model;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private String type;
    private String fabric;
    private String price;
    private List<Color> colors; // Changed to use a custom Color class
    private List<String> customizationOptions;
    private List<String> availableSizes;
    private String description;
    private boolean available;
    private String imageUrl;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFabric() { return fabric; }
    public void setFabric(String fabric) { this.fabric = fabric; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public List<Color> getColors() { return colors; }
    public void setColors(List<Color> colors) { this.colors = colors; }

    public List<String> getCustomizationOptions() { return customizationOptions; }
    public void setCustomizationOptions(List<String> customizationOptions) { this.customizationOptions = customizationOptions; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getAvailableSizes() {return availableSizes; }
    public void setAvailableSizes(List<String> availableSizes) { this.availableSizes = availableSizes; }

}
