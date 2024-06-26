package com.example.kitchenwizard;

public class Recipe {
    private final String name;
    private final String[] ingredients;
    private final String steps;
    private final String[] mesures;
    private final String image;
    private final int id;

    public Recipe(int id, String name, String[] ingredients, String[] mesures, String steps, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.mesures = mesures;
        this.steps = steps;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public String getImageURL() {
        return image;
    }

    public String[] getMesures() {
        return mesures;
    }

    public int getId() {
        return id;
    }
}