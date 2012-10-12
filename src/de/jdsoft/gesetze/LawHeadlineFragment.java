package de.jdsoft.gesetze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.jdsoft.gesetze.LawListFragment.Callbacks;
import de.jdsoft.gesetze.data.helper.Law;
import de.jdsoft.gesetze.data.helper.LawHeadline;
import de.jdsoft.gesetze.database.LawNamesDb;
import de.jdsoft.gesetze.network.RestClient;

/**
 * A fragment representing a single Book detail screen. This fragment is either
 * contained in a {@link LawListActivity} in two-pane mode (on tablets) or a
 * {@link LawHeadlineActivity} on handsets.
 */
public class LawHeadlineFragment extends SherlockListFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	private String slug = "";
	private HeadlineComposerAdapter adapter;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * A dummy implementation of the {@link HeadlineCallbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		public void onItemSelected(String id) {
		}
	};
	
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * This fragment is presenting.
	 */
	private Law law = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LawHeadlineFragment() {
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.w("HeadlineFragment", "onCreate");

		if ( getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
			Log.w("HeadlineFragment", "onCreate2");
			LawNamesDb dbHandler = new LawNamesDb(this.getActivity().getApplicationContext());
			law = dbHandler.getLaw(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));
			
			if (law != null) {
				this.slug = law.getSlug();
			}
		}
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_law_headline,
				container, false);
		
		adapter = new HeadlineComposerAdapter();
		setListAdapter(adapter);

		return rootView;
	}
	

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;

	}

	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		
		Log.e("onItemSelected", "???");
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		
		ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_in_left, android.R.anim.slide_in_left, android.R.anim.slide_in_left);
		//ft.replace(R.id.law_list, new LawListFragment());
		ft.hide(getFragmentManager().findFragmentById(R.id.law_list));
		ft.addToBackStack(null);

		//ft.setCustomAnimations(android.R.animator.fade_in,   android.R.animator.fade_out);
		//ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		//ft.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,android.R.anim.fade_in,android.R.anim.fade_out);



		//ft.hide(getFragmentManager().findFragmentById(R.id.law_list)); 
		ft.commit(); 

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.	
		//int dbid = ((HeadlineComposerAdapter)listView.getAdapter()).getItem(position).getID();
		//mCallbacks.onItemSelected("1");

	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
		
	}
	
	
	/**
	 * Section Composer... 
	 * @author Jens Dieskau
	 *
	 */
	public class HeadlineComposerAdapter extends BaseAdapter {
		private List<Pair<Integer,String>> headlines = null;
		
		
		public HeadlineComposerAdapter() {
			getHeadlines();
		}
		
	    public void getHeadlines() {
	        RestClient.get(getContext(), "law/"+slug, null, new AsyncHttpResponseHandler() {   	
	            public void onSuccess(String response) {
	            	Log.i("GetLawHeadlines", "onSuccess() Response size: "+response.length());
					if ( response.length() == 0 ) {
						Log.e(HeadlineComposerAdapter.class.getName(), "Cannot download law " + slug);
						return;
					}
					
					headlines = new ArrayList<Pair<Integer,String>>();
					for ( String line : response.split("\\r?\\n")) {
						if ( line.contains(":") ) {
							String[] depthAndText = line.split(":");
							headlines.add(new Pair<Integer, String>(Integer.parseInt(depthAndText[0]), depthAndText[1]));
						}
					}
					notifyDataSetChanged();
	            }
	            // TODO on failure!
	        });
	    }


		public Context getContext() {
			return getActivity().getApplicationContext();
		}

		public int getCount() {
			if ( headlines == null ) {
				return 0;
			}
			return headlines.size();
		}

		public LawHeadline getItem(int position) {
			try {
				return new LawHeadline(headlines.get(position).first, headlines.get(position).second);
			} catch(IndexOutOfBoundsException e){
				return null;
			}
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View res = convertView;

			//if (res == null) res = getActivity().getLayoutInflater().inflate(R.layout.item_headline1, parent, false);

			LawHeadline lineObj = getItem(position);
			res = getActivity().getLayoutInflater().inflate(R.layout.item_headline, parent, false);
			TextView headline = (TextView) res.findViewById(R.id.headline);
			
			switch (lineObj.depth) {
			case 1:
				headline.setTextAppearance(getContext(), R.style.Headline1);
				break;
			case 2:
				headline.setTextAppearance(getContext(), R.style.Headline2);
				break;
			case 3:
				headline.setTextAppearance(getContext(), R.style.Headline3);
				break;
			case 4:
				headline.setTextAppearance(getContext(), R.style.Headline4);
				break;
			case 5:
				headline.setTextAppearance(getContext(), R.style.Headline5);
				break;
			case 6:
				headline.setTextAppearance(getContext(), R.style.Headline6);
				break;
			default:
				break;
			}
		
			headline.setText(lineObj.headline);

			return res;
		}
		
		public String getSlug() {
			return slug;
		}

	}
}