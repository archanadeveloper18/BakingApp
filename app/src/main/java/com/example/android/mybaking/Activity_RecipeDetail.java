package com.example.android.mybaking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

public class Activity_RecipeDetail extends AppCompatActivity implements OnStepClickListener{

    Recipe recipe;
    private boolean isTwoPane;

    static String SHARED_PREFERENCES_NAME="com.example.android.bakingapp";
    static String RECIPE_NAME_KEY="recipeName";
    static String INGREDIENTS_KEY="recipeIngredients";

    private int mClickedItemIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipe = (Recipe) getIntent().getParcelableExtra("recipe");
        setTitle(recipe.getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        if(savedInstanceState != null){
            mClickedItemIndex = savedInstanceState.getInt("mClickedItemIndex");
        }

        if(findViewById(R.id.separatorView) !=null){
            isTwoPane=true;

            Bundle bundle1 = new Bundle();
            bundle1.putInt("index",mClickedItemIndex);
            bundle1.putString("step_title",recipe.getSteps().get(mClickedItemIndex).getShortDescription());
            bundle1.putBoolean("isTwoPane",isTwoPane);
            bundle1.putParcelableArrayList("steps",recipe.getSteps());

            Fragment_StepDescription stepDescriptionFrag = new Fragment_StepDescription();
            stepDescriptionFrag.setArguments(bundle1);

            fm.beginTransaction()
                    .add(R.id.recipe_step_frame, stepDescriptionFrag)
                    .commit();
        }
        else{
            isTwoPane=false;
        }

        Bundle bundle2 = new Bundle();
        bundle2.putParcelable("recipe", getIntent().getParcelableExtra("recipe"));
        bundle2.putBoolean("isTwoPane",isTwoPane);
        bundle2.putInt("mClickedItemIndex", mClickedItemIndex);

        Fragment_RecipeDetail recipeDetail = new Fragment_RecipeDetail();
        recipeDetail.setArguments(bundle2);

        fm.beginTransaction()
                .add(R.id.recipe_detail_frame, recipeDetail)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.addToWidget:

                SharedPreferences preferences = this.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

                Gson gson = new Gson();
                String json = gson.toJson(recipe.getIngredients());

                preferences.edit().putString(RECIPE_NAME_KEY, recipe.getName()).apply();
                preferences.edit().putString(INGREDIENTS_KEY, json).apply();

                BakingAppWidgetProvider.sendRefreshBroadcast(this);

                Toast.makeText(this, "Added to Widget", Toast.LENGTH_SHORT).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int index) {
        mClickedItemIndex = index;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mClickedItemIndex", mClickedItemIndex);
    }
}
