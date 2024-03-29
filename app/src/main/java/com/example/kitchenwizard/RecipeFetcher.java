package com.example.kitchenwizard;


public class RecipeFetcher implements Runnable {

    private int id;
    private RecipeListener listener;

    public RecipeFetcher(int id, RecipeListener listener) {
        this.id = id;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            Recipe recipe = MainActivity.patata(id);

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