import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aarushiarya.tryfrag.Model.Detail;
import com.example.aarushiarya.tryfrag.Model.Result;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;

public class Tab1Res extends Fragment {
        private static final String TAG = "";
        private EditText keyword, distance;
        private TextView keyErr, locErr;
        Button search, clear;
        Spinner category;
        RadioGroup from;
        private Double lat, lng;


        protected GeoDataClient mGeoDataClient;

        private PlaceAutocompleteAdapter mAdapter;

        private AutoCompleteTextView mAutocompleteView;

        private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
                new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                final View rootView = inflater.inflate(R.layout.tab1res, container, false);
                String[] values = {"Default", "Airport", "Amusement Park", "Aquarium", "Art Gallery", "Bakery", "Bar", "Beauty Salon", "Bowling Alley", "Bus Station", "Cafe",
                        "Campground", "Car Rental", "Casino", "Lodging", "Movie Theatre", "Museum", "Night Club", "Park", "Parking", "Restaurant",
                        "Shopping Mall", "Stadium", "Subway Station", "Taxi Station", "Train Station", "Transit Station", "Travel Agency", "Zoo"};
                //Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                //spinner.setAdapter(adapter);

                keyword = rootView.findViewById(R.id.keyword);
                distance = rootView.findViewById(R.id.distance);
                //category = rootView.findViewById(R.id.spinner);
                from = rootView.findViewById(R.id.fromGroup);
                keyErr = rootView.findViewById(R.id.keywordErr);
                locErr = rootView.findViewById(R.id.fromErr);

                final MainActivity activity = (MainActivity) getActivity();
                lat = activity.getLat();
                lng = activity.getLng();

                from.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId) {
                                        case R.id.from1:
                                                locErr.setText("");
                                                mAutocompleteView.setEnabled(false);
                                                break;
                                        case R.id.from2:
                                                mAutocompleteView.setEnabled(true);
                                                break;
                                }
                        }
                });


                search = rootView.findViewById(R.id.search);
                search.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String dis, key, cat, url, options;
                                key = keyword.getText().toString();
                                if (key.matches("")) {
                                        keyErr.setText(R.string.validate);
                                } else {
                                        keyErr.setText("");
                                }
                                dis = distance.getText().toString();
                                if (dis.matches("")) {
                                        dis = "16090";
                                }
                                //cat = category.getSelectedItem().toString().toLowerCase();
                                cat = "restaurant";

                                int rId = from.getCheckedRadioButtonId();
                                RadioButton fromBtn = rootView.findViewById(rId);
                                options = (String) fromBtn.getText();
                                if (options.matches("Current location")) {
                                        locErr.setText("");
                                } else if (options.matches("Other. Specify Location")) {
                                        if (mAutocompleteView.getText().toString().trim().matches("")) {
                                                locErr.setText(R.string.validate);
                                        }else{
                                                locErr.setText("");
                                        }

                                }


                                String keyError, locError;
                                keyError = keyErr.getText().toString();
                                locError = locErr.getText().toString();
                                if ((keyError.matches("")) && (locError.matches(""))) {
                                        url = "http://webhw81-env.us-east-2.elasticbeanstalk.com/getPlaces/" + key + "/" + cat + "/" + dis + "/" + lat + "/" + lng + "/AIzaSyBsqqvSXgfdba5wWPx2YGTJvyWg4UUUsCM";

                                        Intent intent = new Intent(getActivity(), RecycleActivity.class);
                                        //Create the bundle
                                        Bundle bundle = new Bundle();

                                        //Add your data to bundle
                                        bundle.putString("stuff", url);

                                        bundle.putString("activity", "main");
                                        //Add the bundle to the intent
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                } else {
                                        Toast.makeText(getContext(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                                }

                        }
                });

                clear = rootView.findViewById(R.id.clear);
                clear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                keyword.setText("");
                                distance.setText("");
                                keyErr.setText("");

                                locErr.setText("");
                                category.setSelection(0);
                                from.check(R.id.from1);
                        }
                });

                mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
                mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete_places);
                mAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final AutocompletePrediction item = mAdapter.getItem(position);
                                final String placeId = item.getPlaceId();
                                final CharSequence primaryText = item.getPrimaryText(null);
                                }
                });
                mAdapter = new PlaceAutocompleteAdapter(getContext(), mGeoDataClient, BOUNDS_GREATER_SYDNEY, null);
                mAutocompleteView.setAdapter(mAdapter);
                return rootView;
        }
}
