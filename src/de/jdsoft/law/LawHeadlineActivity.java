package de.jdsoft.law;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import de.jdsoft.law.LawListFragment.Callbacks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import de.jdsoft.law.database.Favorites;

/**
 * An activity representing a single Book detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link LawListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link LawHeadlineFragment}.
 */
public class LawHeadlineActivity extends SherlockFragmentActivity implements Callbacks, ActionBar.OnNavigationListener {

    private static final int OPTION_FAV = 3;

    private LawHeadlineFragment fragment;
    public LawHeadlineFragment headlineFragment;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_law_headline);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(LawHeadlineFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(LawHeadlineFragment.ARG_ITEM_ID));
			fragment = new LawHeadlineFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.law_headline_container, fragment).commit();
		}

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public boolean onCreateOptionsMenu(final Menu menu) {
        boolean isLight = true; // TODO

        // Favorite
        int favDrawable = 0;
        if( Favorites.isFav(getIntent()
                .getStringExtra(LawHeadlineFragment.ARG_ITEM_ID)) ) {
            favDrawable = R.drawable.btn_star_on_convo_holo_light;
        } else {
            favDrawable = R.drawable.btn_star_off_convo_holo_light;
        }

        menu.add(0, OPTION_FAV, 2, R.string.favit)
                .setIcon(favDrawable)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);


        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action buttons
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
//			NavUtils.navigateUpTo(this,
//					new Intent(this, LawListActivity.class));
//            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            onBackPressed();
            return true;

        case OPTION_FAV:
            String id = getIntent().getStringExtra(LawHeadlineFragment.ARG_ITEM_ID);
            if( Favorites.isFav(id) ) {
                Favorites.removeFav(id);
                item.setIcon(R.drawable.btn_star_off_convo_holo_light);
            } else {
                Favorites.addFav(id);
                item.setIcon(R.drawable.btn_star_on_convo_holo_light);
            }

            break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onItemSelected(String id) {
	}
	
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return true;
	}

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

}
