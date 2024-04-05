package com.example.kitchenwizard;


public class RandomRecipeFetcher implements Runnable {

    private RecipeListener listener;

    public RandomRecipeFetcher(RecipeListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            Recipe recipe = RecetteRandom.randomPatata();

            // Notify listener with the fetched recipe
            if (listener != null && recipe != null) {
                listener.onRecipeFetched(recipe);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface RecipeListener {
        void onRecipeFetched(Recipe recipe);
    }
}