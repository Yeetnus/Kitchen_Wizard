package com.example.kitchenwizard;

import java.util.List;

public class Recipe {
    private final String name;
    private final List<String> ingredients;
    private final String steps;
    private final List<String> mesures;
    private final String image;

    public Recipe(String name, List<String> ingredients, List<String> mesures, String steps, String image) {
        this.name = name;
        this.ingredients = ingredients;
        this.mesures = mesures;
        this.steps = steps;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public String getImage() {
        return image;
    }

    public List<String> getMesures() {
        return mesures;
    }
}
