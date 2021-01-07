package org.ifaco.aminyab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.ifaco.aminyab.L.*;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.ifaco.aminyab.Fun.OA;
import static org.ifaco.aminyab.Fun.VA;
import static org.ifaco.aminyab.Fun.capitalize;
import static org.ifaco.aminyab.Fun.compileTime;
import static org.ifaco.aminyab.Fun.dm;
import static org.ifaco.aminyab.Fun.doNothing;
import static org.ifaco.aminyab.Fun.dp;
import static org.ifaco.aminyab.Fun.exit;
import static org.ifaco.aminyab.Fun.explode;
import static org.ifaco.aminyab.Fun.isOnline;
import static org.ifaco.aminyab.Fun.sp;

public class Main extends AppCompatActivity implements OnMapReadyCallback {
    ConstraintLayout mBody;
    View mMotor1, mRadar, mListsTab1, mListsTab2, mListsSwitch, mBGVLeft, mBGVRight, mLeftEye, mRightEye;
    SupportMapFragment mMap;
    static View mCover;
    static ConstraintLayout mLists;
    TextView mListsTitle, mListsTab1TV, mListsTab2TV;
    ImageView mListsOptions, mListsFilterMark, mLeftSkull, mRightSkull, mLeftJaw, mRightJaw;
    RecyclerView mList1, mList2;
    Spinner mListsFilter;
    EditText mET;

    FusedLocationProviderClient flpc;
    public static Context c;
    final int loadDur = 180, skullDur = 2220, openDur = 9990, vibDur = 1265, REQUEST_CHECK_SETTINGS = 1110, LOCATION_PERMISSION = 212;
    int curList = -1, list1Filter = 0, list2Filter = 0, spOpening1, spOpening2, spType1, spImpact1, spImpact2, spImpact3, spImpact4,
            spImpact5, spClick1, spClick2, spClick3, spTouch1, spExplode1, spBoldImpact1;
    public static int spOpening3, spOpening4;
    ImageView[] skulls;
    RequestQueue check;
    Vibrator vib;
    static final String manager = "http://ifaco.org/android/aminyab/manager.php", gotPass = "gotPass", gotCords = "gotCords",
            database = "aminyab", exLastSynced = "lastSync", defCords = "::::::::", splitCoorsBy = ":";
    static GoogleMap map;
    boolean firstBackToExit = false, locationOn = false, locationPerm = false, mListsFilterTouched = false,
            checkingCancelled = false;
    static boolean loggedIn = false, showingLists = false;
    Marker Me, Him;
    public static String MyName = "", HisName;
    public static Location here = null;
    LocationRequest locreq = null;
    public static Handler navHandler, hViewer;
    long locInterval = 30000;
    ValueAnimator whirlRadar;
    public static SoundPool sounds;
    MediaPlayer music;
    static ObjectAnimator oaCover;
    static AnimatorSet asLists;
    AnimatorSet switchList;
    static float mListsRotat = -180f;
    public static ArrayList<Coordinates> allMyCoors, allHisCoors, MyCoors, HisCoors;
    LinearLayoutManager mList1Mngr = null, mList2Mngr = null;
    AdapterCoor mList1Adapter = null, mList2Adapter = null;
    public static ArrayList<Boolean> itemCreated1, itemCreated2;
    public static ArrayList<Marker> MyMarkers = new ArrayList<>(), HisMarkers = new ArrayList<>();
    ArrayList<Filter> filters1 = null, filters2 = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBody = findViewById(R.id.mBody);
        mMotor1 = findViewById(R.id.mMotor1);
        mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mMap);
        mRadar = findViewById(R.id.mRadar);
        mCover = findViewById(R.id.mCover);
        mLists = findViewById(R.id.mLists);
        mListsTitle = findViewById(R.id.mListsTitle);
        mListsOptions = findViewById(R.id.mListsOptions);
        mListsTab1 = findViewById(R.id.mListsTab1);
        mListsTab2 = findViewById(R.id.mListsTab2);
        mListsTab1TV = findViewById(R.id.mListsTab1TV);
        mListsTab2TV = findViewById(R.id.mListsTab2TV);
        mListsSwitch = findViewById(R.id.mListsSwitch);
        mList1 = findViewById(R.id.mList1);
        mList2 = findViewById(R.id.mList2);
        mListsFilter = findViewById(R.id.mListsFilter);
        mListsFilterMark = findViewById(R.id.mListsFilterMark);
        mBGVLeft = findViewById(R.id.mBGVLeft);
        mBGVRight = findViewById(R.id.mBGVRight);
        mLeftEye = findViewById(R.id.mLeftEye);
        mRightEye = findViewById(R.id.mRightEye);
        mLeftSkull = findViewById(R.id.mLeftSkull);
        mRightSkull = findViewById(R.id.mRightSkull);
        mET = findViewById(R.id.mET);
        mLeftJaw = findViewById(R.id.mLeftJaw);
        mRightJaw = findViewById(R.id.mRightJaw);

        c = getApplicationContext();
        skulls = new ImageView[]{mLeftSkull, mRightSkull, mLeftJaw, mRightJaw};
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sp = PreferenceManager.getDefaultSharedPreferences(c);
        mMap.getMapAsync(this);
        mListsRotat = mLists.getRotation();


        // Request Location
        locreq = LocationRequest.create();
        locreq.setInterval(locInterval);
        locreq.setFastestInterval((int) (locInterval / 2));
        locreq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder lrBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locreq);
        Task<LocationSettingsResponse> lrTask = LocationServices.getSettingsClient(c).checkLocationSettings(lrBuilder.build());
        lrTask.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locationOn = true;
                start();
            }
        });
        lrTask.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(Main.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignored) {
                }
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        } else {
            locationPerm = true;
            start();
        }
        navHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        explode(c, mRadar, R.drawable.circle_cp, 1265, 5f, 0.55f);
                        new Viewer(c, MyName, HisName).start();
                        try {
                            sounds.play(spExplode1, 1f, 1f, 3, 0, 1f);
                        } catch (Exception ignored) {
                        }
                        break;
                }
            }
        };

        // Sounds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder spLoopBuilder = new SoundPool.Builder().setMaxStreams(10);
            sounds = spLoopBuilder.build();
        } else sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        if (sounds != null) {
            spOpening1 = sounds.load(c, R.raw.pigs_darthsidious_idle_04, 3);
            spOpening2 = sounds.load(c, R.raw.pigs_darthsidious_special_02, 3);
            spImpact1 = sounds.load(c, R.raw.pigs_darthsidious_impact_01, 1);
            spImpact2 = sounds.load(c, R.raw.pigs_darthsidious_impact_02, 1);
            spImpact3 = sounds.load(c, R.raw.pigs_darthsidious_impact_03, 1);
            spImpact4 = sounds.load(c, R.raw.pigs_darthsidious_impact_04, 1);
            spImpact5 = sounds.load(c, R.raw.pigs_darthsidious_impact_05, 1);
            spType1 = sounds.load(c, R.raw.coin_collect_01, 2);
            spClick1 = sounds.load(c, R.raw.menu_button_open, 2);
            spClick2 = sounds.load(c, R.raw.ui_button_general_01, 2);
            spClick3 = sounds.load(c, R.raw.level_locked, 2);
            spTouch1 = sounds.load(c, R.raw.enter_atmosphere, 1);
            spExplode1 = sounds.load(c, R.raw.medium_explosion_02, 3);
            spBoldImpact1 = sounds.load(c, R.raw.level_end_emperor_angry_01, 1);
            spOpening3 = sounds.load(c, R.raw.pigs_darthsidious_idle_06, 2);
            spOpening4 = sounds.load(c, R.raw.pigs_darthsidious_idle_03, 2);
        }

        // Loading
        ValueAnimator anLoad = VA(mMotor1, "translationX", loadDur, 1f, 0f);
        anLoad.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mET.setTextSize(mETSize(false));

                switchList(0);
            }
        });

        // Skulls's Configurations
        for (ImageView sk : skulls) sk.setColorFilter(ContextCompat.getColor(c, R.color.CP));
        mET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (loggedIn) return;
                try {
                    sounds.play(spType1, 1f, 1f, 2, 0, 1f);
                } catch (Exception ignored) {
                }
                if (mET.getText().toString().length() == 4) check(mET.getText().toString(), 0);
            }
        });
        View[] impacted = new View[]{mLeftSkull, mRightSkull, mLeftJaw, mRightJaw};
        for (int j = 0; j < impacted.length; j++) {
            impacted[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int[] impacts = new int[]{spImpact1, spImpact2, spImpact3, spImpact4, spImpact5};
                    try {
                        sounds.play(impacts[new Random().nextInt(impacts.length)],
                                0.79f, 0.79f, 1, 0, 1f);
                    } catch (Exception ignored) {
                    }
                }
            });
            impacted[j].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (checkingCancelled) {
                        if (mET.getText().toString().length() == 4)
                            check(mET.getText().toString(), 0);
                        else if (sp.contains(gotPass)) check(sp.getString(gotPass, ""), 1);
                    }
                    try {
                        sounds.play(spBoldImpact1, 0.66f, 0.66f, 1, 0, 1f);
                    } catch (Exception ignored) {
                    }
                    return true;
                }
            });
        }

        // Radar
        //ShapeDrawable mUSD = new ShapeDrawable();
        //mUSD.setShape(new OvalShape());
        GradientDrawable mRGD = new GradientDrawable();
        mRGD.setShape(GradientDrawable.OVAL);
        //mRGD.setStroke(dp(3), ContextCompat.getColor(c, R.color.mUGD));
        mRGD.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        mRGD.setColors(new int[]{Color.TRANSPARENT, ContextCompat.getColor(c, R.color.mRGD)});
        mRadar.setBackground(mRGD);
        whirlRadar();
        mRadar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sounds.play(spClick1, 1f, 1f, 3, 0, 1f);
                } catch (Exception ignored) {
                }
                cover(true);
                showLists(true);
            }
        });

        // Lists
        mLists.setOnClickListener(doNothing);
        mListsTab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchList(0);
            }
        });
        mListsTab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchList(1);
            }
        });
        mList1.setHasFixedSize(false);
        mList2.setHasFixedSize(false);
        mList1Mngr = new LinearLayoutManager(c);
        mList2Mngr = new LinearLayoutManager(c);
        mList1Mngr.setMeasurementCacheEnabled(false);
        mList2Mngr.setMeasurementCacheEnabled(false);
        mList1.setLayoutManager(mList1Mngr);
        mList2.setLayoutManager(mList2Mngr);
        mList1.addItemDecoration(AdapterCoor.itemDecoration1);
        mList2.addItemDecoration(AdapterCoor.itemDecoration2);
        hViewer = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:// Me
                        allMyCoors = (ArrayList<Coordinates>) msg.obj;
                        if (allMyCoors != null) {
                            filters1 = filter(allMyCoors);
                            if (filters1 != null) list1Filter = filters1.size() - 1;
                            filterList(0);
                            arrangeList(0);// Don't put this inside "if"
                            if (filters1 != null) setSpinner(0);
                        }
                        break;
                    case 1:// Him
                        allHisCoors = (ArrayList<Coordinates>) msg.obj;
                        if (allHisCoors != null) {
                            filters2 = filter(allHisCoors);
                            if (filters2 != null) list2Filter = filters2.size() - 1;
                            filterList(1);
                            arrangeList(1);
                        }
                        break;
                }
            }
        };

        // Lists' Filtering
        mListsFilterMark.setColorFilter(ContextCompat.getColor(c, R.color.mListsFilterMark));
        mListsFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mListsFilterTouched = true;
                return false;
            }
        });
        mListsFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!mListsFilterTouched) return;
                switch (curList) {
                    case 0:
                        list1Filter = i;
                        break;
                    case 1:
                        list2Filter = i;
                        break;
                }
                filterList(curList);
                arrangeList(curList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Lists' Options
        final AppCompatActivity THAT = this;
        mListsOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(new ContextThemeWrapper(THAT, R.style.ThemeOverlay_AppCompat_Dark), mListsOptions);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mListsOptionsReload:
                                new Viewer(c, MyName, HisName).start();
                                return true;
                            case R.id.mListsOptionsSync:
                                Alarm.syncNow(c);
                                return true;
                            case R.id.mListsOptionsSignOut:
                                AlertDialog.Builder adb = new AlertDialog.Builder(THAT)
                                        .setTitle(R.string.mListsOptionsSignOut)
                                        .setIcon(ContextCompat.getDrawable(c, R.drawable.ntf_icon_1))
                                        .setMessage(R.string.mListsOptionsSignOutMsg)
                                        .setPositiveButton(R.string.yes1, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                logOut();
                                            }
                                        }).setNegativeButton(R.string.no1, null);
                                adb.create().show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.m_lists_options);
                popup.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sounds != null) try {
            sounds.autoResume();
        } catch (Exception ignored) {
        }
        if (music != null) try {
            music.start();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sounds != null) try {
            sounds.autoPause();
        } catch (Exception ignored) {
        }
        if (music != null) try {
            music.pause();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sounds != null) {
            try {
                sounds.release();
            } catch (Exception ignored) {
            }
            sounds = null;
        }
        if (music != null) {
            try {
                music.release();
            } catch (Exception ignored) {
            }
            music = null;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ValueAnimator anUpdate = VA(mMotor1, "rotationX", loadDur, 180f, 0f);
        anUpdate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mET.setTextSize(mETSize(false));
                mLeftJaw.setTranslationY(mETSize(true));
                mRightJaw.setTranslationY(mETSize(true));

                switchList(0);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;//map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark));
        //map.setMinZoomPreference(10f);map.setOnMarkerDragListener();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (loggedIn) try {
                    sounds.play(spClick3, 0.1f, 0.1f, 2, 0, 1f);
                } catch (Exception ignored) {
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (loggedIn) try {
                    sounds.play(spClick2, 1f, 1f, 2, 0, 1f);
                } catch (Exception ignored) {
                }
                return false;
            }
        });
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (loggedIn) try {
                    sounds.play(spTouch1, 0.08f, 0.08f, 1, 0, 1f);
                } catch (Exception ignored) {
                }
            }
        });
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(c);
                info.setOrientation(LinearLayout.VERTICAL);
                info.setPadding(dp(10), dp(8), dp(10), dp(10));

                TextView title = new TextView(c);
                title.setTextColor(ContextCompat.getColor(c, R.color.mInfoTitle));
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setTextSize(dm.density * 8f);
                title.setText(marker.getTitle());
                info.addView(title);

                TextView snippet = new TextView(c);
                snippet.setPadding(0, dp(7), 0, 0);
                snippet.setTextColor(ContextCompat.getColor(c, R.color.mInfoSnippet));
                snippet.setGravity(Gravity.CENTER);
                snippet.setTextSize(dm.density * 5.5f);
                snippet.setText(marker.getSnippet());
                snippet.setLineSpacing(dp(27), 0);
                info.addView(snippet);
                return info;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK) {
                    locationOn = true;
                    start();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPerm = true;
                    start();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (showingLists) {
            showLists(false);
            return;
        }
        if (!firstBackToExit) {
            firstBackToExit = true;
            Toast.makeText(c, R.string.toExit, Toast.LENGTH_SHORT).show();
            new CountDownTimer(4000, 4000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    firstBackToExit = false;
                }
            }.start();
            return;
        }
        exit(this);
    }


    @SuppressLint("MissingPermission")
    void start() {
        if (!locationOn || !locationPerm) return;
        flpc = LocationServices.getFusedLocationProviderClient(this);
        flpc.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        here = location;
                    }
                });
        LocationCallback locCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) here = location;
                whereAmI(false, false);
            }
        };
        flpc.requestLocationUpdates(locreq, locCallback, Looper.getMainLooper());

        for (int s = 0; s < skulls.length; s++) {
            ObjectAnimator oa = OA(skulls[s], "alpha", 1f, skullDur);
            if (s == skulls.length - 1) oa.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!sp.contains(gotPass)) openMouth();
                    else check(sp.getString(gotPass, ""), 1);
                    //open(sp.getString(gotCords, defCords), false);
                }
            });
        }

        try {
            sounds.play(spOpening1, 1f, 1f, 3, 0, 1f);
        } catch (Exception ignored) {
        }
    }

    float mETSize(boolean mar) {
        float r;
        if (mLeftJaw.getHeight() == 0) {
            int criterion = dm.widthPixels;
            if (dm.widthPixels > dm.heightPixels) criterion = dm.heightPixels;
            r = criterion / 28f;
            if (mar) r += dp(45);
        } else {
            r = mLeftJaw.getHeight() / 11f;
            if (mar) r += dp(45);
        }
        return r;
    }

    void open(String coordinates, boolean first) {
        whereIsHe(coordinates, true);
        whereAmI(true, true);
        Alarm.awaken(c);
        Alarm.loopSync(c);

        if (first) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //for API 26+
                vib.vibrate(VibrationEffect.createOneShot(vibDur, VibrationEffect.DEFAULT_AMPLITUDE));
            else vib.vibrate(vibDur);
        }

        int openEyes = openDur / 10;
        OA(mLeftEye, "alpha", 1f, openEyes);
        OA(mRightEye, "alpha", 1f, openEyes);
        float t = dm.widthPixels * 3f;
        AnimatorSet ass = new AnimatorSet().setDuration(openDur);
        ass.setStartDelay(openEyes);
        ass.playTogether(
                ObjectAnimator.ofFloat(mBGVLeft, "translationX", 0f - t),
                ObjectAnimator.ofFloat(mLeftEye, "translationX", 0f - t),
                ObjectAnimator.ofFloat(mLeftSkull, "translationX", 0f - t),
                ObjectAnimator.ofFloat(mLeftJaw, "translationX", 0f - t),
                ObjectAnimator.ofFloat(mBGVRight, "translationX", t),
                ObjectAnimator.ofFloat(mRightSkull, "translationX", t),
                ObjectAnimator.ofFloat(mRightEye, "translationX", t),
                ObjectAnimator.ofFloat(mRightJaw, "translationX", t),
                ObjectAnimator.ofFloat(mLeftEye, "alpha", 0f),
                ObjectAnimator.ofFloat(mRightEye, "alpha", 0f)
        );
        ass.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                OA(mET, "alpha", 0f, openDur / 30);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                View[] removal = {mET, mBGVLeft, mLeftSkull, mLeftJaw, mBGVRight, mRightSkull, mRightJaw};
                for (View r : removal) if (r != null) r.setVisibility(View.GONE);
            }
        });
        ass.start();
        try {
            sounds.play(spOpening2, 1f, 1f, 3, 0, 1f);
        } catch (Exception ignored) {
        }

        music = MediaPlayer.create(c, R.raw.cue2_sm_poa_dementoramba_03);
        music.setLooping(true);
        music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                music.start();
                music.setVolume(0.6f, 0.6f);
            }
        });

        mListsTab1TV.setText(capitalize(MyName));
        mListsTab2TV.setText(capitalize(HisName));
        new Viewer(c, MyName, HisName).start();
    }

    void check(String code, final int mode) {
        if (mode < 2 && loggedIn) return;
        if (!isOnline(c)) {
            Toast.makeText(c, R.string.noInternet, Toast.LENGTH_LONG).show();
            checkingCancelled = true;
            return;
        }
        code = Integer.toString(Integer.parseInt(code));
        final String CODE = code;
        check = Volley.newRequestQueue(c);
        StringRequest srt = new StringRequest(Request.Method.POST, manager + "?action=check",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        if (res == null || res.equals("") || res.length() < 4) {
                            checkingCancelled = true;
                            if (mode == 1) openMouth();
                            return;
                        }
                        if (res.substring(0, 4).equals("yeah")) {
                            if (mode < 2) loggedIn = true;
                            String cords = res.substring(4);
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString(gotPass, CODE);
                            ed.putString(gotCords, cords);
                            ed.apply();
                            if (mode < 2) open(cords, true);
                            else if (mode == 2) whereIsHe(cords, false);
                            checkingCancelled = false;
                        } else {
                            checkingCancelled = true;
                            if (mode == 1) openMouth();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkingCancelled = true;
                if (mode == 1) openMouth();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("code", CODE);
                return params;
            }
        };
        srt.setTag("check");
        check.add(srt);
    }

    void openMouth() {
        AnimatorSet ass = new AnimatorSet().setDuration(skullDur);
        ass.playTogether(
                ObjectAnimator.ofFloat(mLeftJaw, "translationY", mETSize(true)),
                ObjectAnimator.ofFloat(mRightJaw, "translationY", mETSize(true))
        );
        ass.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator showET = OA(mET, "alpha", 1f, skullDur);
                showET.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mET.requestFocus();
                    }
                });
            }
        });
        ass.start();
    }

    void whereAmI(boolean toast, boolean zoom) {
        if (here != null) {
            LatLng myCord = new LatLng(here.getLatitude(), here.getLongitude());
            if (Me != null) try {
                Me.remove();
            } catch (Exception ignored) {
            }
            Me = map.addMarker(new MarkerOptions()
                    .position(myCord)
                    .title(capitalize(MyName))
                    .snippet(compileTime(here.getTime()))
            );
            Me.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            if (zoom) checkMarkers();
        } else if (toast)
            Toast.makeText(c, getResources().getString(R.string.youreMissing), Toast.LENGTH_LONG).show();
    }

    void whereIsHe(String coordinates, boolean zoom) {
        double[] cord = null;
        long time = 0;
        if (coordinates != null) {
            String[] cords = coordinates.split(splitCoorsBy);
            try {
                MyName = cords[0];
            } catch (Exception ignored) {
            }
            try {
                HisName = cords[1];
            } catch (Exception ignored) {
            }
            try {
                cord = new double[]{Double.parseDouble(cords[2]), Double.parseDouble(cords[3])};
            } catch (Exception ignored) {
            }
            try {
                time = Long.parseLong(cords[4]);
            } catch (Exception ignored) {
            }
            try {
                Alarm.alarmType = Integer.parseInt(cords[5]);
            } catch (Exception ignored) {
            }
            try {
                Alarm.alarmStart = Integer.parseInt(cords[6]);
            } catch (Exception ignored) {
            }
            try {
                Alarm.interval = Long.parseLong(cords[7]);
            } catch (Exception ignored) {
            }
            try {
                Alarm.onlineInterval = Long.parseLong(cords[8]);
            } catch (Exception ignored) {
            }
        }
        if (cord != null) {
            LatLng amin = new LatLng(cord[1], cord[0]);
            Him = map.addMarker(new MarkerOptions()
                    .position(amin)
                    .title(capitalize(HisName))
                    .snippet(compileTime(time) + Fun.dist(c, here, amin))
            );
            Him.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            if (zoom) checkMarkers();
        } else Snackbar.make(mBody, capitalize(HisName) +
                getResources().getString(R.string.missing), Snackbar.LENGTH_LONG).show();
    }

    void checkMarkers() {
        if (Me == null && Him == null) return;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (Me != null) builder.include(Me.getPosition());
        if (Him != null) builder.include(Him.getPosition());
        LatLngBounds bounds = builder.build();
        int sWisth = dm.widthPixels, sHeight = dm.heightPixels;
        if (mBody.getWidth() > 0) sWisth = mBody.getWidth();
        if (mBody.getHeight() > 0) sHeight = mBody.getHeight();
        int padding = (int) (sWisth * 0.16); // offset from edges of the map 16% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, sWisth, sHeight, padding);
        map.animateCamera(cu);
    }

    void whirlRadar() {
        whirlRadar = VA(mRadar, "rotation", 2220, 0f, 360f);
        whirlRadar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                whirlRadar();
            }
        });
    }

    static void showLists(boolean b) {
        if (b == showingLists) return;
        showingLists = b;
        asLists = new AnimatorSet().setDuration(444);
        if (showingLists) {
            asLists.playTogether(
                    ObjectAnimator.ofFloat(mLists, "scaleX", 0f, 1f),
                    ObjectAnimator.ofFloat(mLists, "scaleY", 0f, 1f),
                    ObjectAnimator.ofFloat(mLists, "rotation", mListsRotat, 0f)
            );
            mLists.setVisibility(View.VISIBLE);
            mCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLists(false);
                }
            });
        } else {
            asLists.playTogether(
                    ObjectAnimator.ofFloat(mLists, "scaleX", 1f, 0f),
                    ObjectAnimator.ofFloat(mLists, "scaleY", 1f, 0f),
                    ObjectAnimator.ofFloat(mLists, "rotation", 0f, mListsRotat)
            );
            mCover.setOnClickListener(doNothing);
            cover(false);
        }

        asLists.start();
        asLists.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!showingLists) mLists.setVisibility(View.GONE);
            }
        });
    }

    void switchList(int which) {
        if (curList == which) return;
        curList = which;
        int normalDur = 444;
        switchList = new AnimatorSet();
        int size = (int) (dm.widthPixels * 0.79f), switchSize = size / 2;
        if (mList1.getWidth() > 0) size = mList1.getWidth();
        if (mListsSwitch.getWidth() > 0) switchSize = mListsSwitch.getWidth();
        ObjectAnimator swScY = ObjectAnimator.ofFloat(mListsSwitch, "scaleX",
                mListsSwitch.getScaleX(), 1f).setDuration(normalDur / 2);
        swScY.setStartDelay(normalDur / 2);
        switchList.playTogether(
                ObjectAnimator.ofFloat(mList1, "translationX",
                        mList1.getTranslationX(), (size * 0) - (size * which)).setDuration(normalDur),
                ObjectAnimator.ofFloat(mList2, "translationX",
                        mList2.getTranslationX(), (size * 1) - (size * which)).setDuration(normalDur),
                ObjectAnimator.ofFloat(mListsSwitch, "translationX",
                        mListsSwitch.getTranslationX(), switchSize * which).setDuration(normalDur),
                ObjectAnimator.ofFloat(mListsSwitch, "scaleX",
                        mListsSwitch.getScaleX(), 0.48f).setDuration(normalDur / 2), swScY
        );
        switchList.start();
        setSpinner(curList);
    }

    void arrangeList(int which) {
        switch (which) {
            case 0:
                for (int m = 0; m < MyMarkers.size(); m++)
                    if (MyMarkers.get(m) != null) MyMarkers.get(m).remove();
                MyMarkers.clear();
                for (int n = 0; n < MyCoors.size(); n++) MyMarkers.add(null);
                // Make a function out of these later...
                itemCreated1 = new ArrayList<>();
                for (int i = 0; i < MyCoors.size(); i++) itemCreated1.add(false);
                mList1Adapter = new AdapterCoor(c, MyCoors, which);
                mList1.setAdapter(mList1Adapter);
                break;
            case 1:
                for (int m = 0; m < HisMarkers.size(); m++)
                    if (HisMarkers.get(m) != null) HisMarkers.get(m).remove();
                HisMarkers.clear();
                for (int n = 0; n < HisCoors.size(); n++) HisMarkers.add(null);
                // Make a function out of these later...
                itemCreated2 = new ArrayList<>();
                for (int i = 0; i < HisCoors.size(); i++) itemCreated2.add(false);
                mList2Adapter = new AdapterCoor(c, HisCoors, which);
                mList2.setAdapter(mList2Adapter);
                break;
        }
    }

    ArrayList<Filter> filter(ArrayList<Coordinates> coors) {// Don't use "c" as Context!
        ArrayList<Filter> filters = new ArrayList<>();
        for (int c = 0; c < coors.size(); c++) {
            Coordinates coor = coors.get(c);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(coor.time);
            SolarHijri shamsi = new SolarHijri(cal);
            int cYear = shamsi.Y, cMonth = shamsi.M;

            boolean filterExists = false;
            for (int f = 0; f < filters.size(); f++)
                if (filters.get(f).year == cYear && filters.get(f).month == cMonth) {
                    filterExists = true;
                    Filter put = filters.get(f);
                    put.put(c);
                    filters.set(f, put);
                }
            if (!filterExists) {
                ArrayList<Integer> items = new ArrayList<>();
                items.add(c);
                filters.add(new Filter(cYear, cMonth, items));
            }
        }
        return filters;
    }

    ArrayList<String> filterTitles(ArrayList<Filter> filters) {
        ArrayList<String> titles = new ArrayList<>();
        for (int f = 0; f < filters.size(); f++) titles.add(filters.get(f).titleInShamsi(c));
        return titles;
    }

    void setSpnFilter(ArrayList<String> options) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(c, R.layout.spinner_1, options);
        dataAdapter.setDropDownViewResource(R.layout.spinner_1_dd);
        mListsFilter.setAdapter(dataAdapter);
        mListsFilterTouched = false;
    }

    void setSpinner(int which) {
        switch (which) {
            case 0:
                if (filters1 == null) return;
                setSpnFilter(filterTitles(filters1));
                list1Filter = filters1.size() - 1;
                mListsFilter.setSelection(list1Filter, true);
                break;
            case 1:
                if (filters2 == null) return;
                setSpnFilter(filterTitles(filters2));
                list2Filter = filters2.size() - 1;
                mListsFilter.setSelection(list2Filter, true);
                break;
        }
    }

    void filterList(int which) {
        switch (which) {
            case 0:
                if (filters1 == null) MyCoors = allMyCoors;
                else {
                    MyCoors = new ArrayList<>();
                    ArrayList<Integer> items = filters1.get(list1Filter).items;
                    for (int o = 0; o < items.size(); o++)
                        MyCoors.add(allMyCoors.get(items.get(o)));
                }
                break;
            case 1:
                if (filters2 == null) HisCoors = allHisCoors;
                else {
                    HisCoors = new ArrayList<>();
                    ArrayList<Integer> items = filters2.get(list2Filter).items;
                    for (int o = 0; o < items.size(); o++)
                        HisCoors.add(allHisCoors.get(items.get(o)));
                }
                break;
        }
    }

    void logOut() {
        SharedPreferences.Editor ed = sp.edit();
        ed.remove(gotPass);
        ed.remove(gotCords);
        ed.remove(exLastSynced);
        ed.apply();
        loggedIn = false;
        AlarmManager alarmMgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) alarmMgr.cancel(Alarm.sync(c));
        final AppCompatActivity that = this;
        new CountDownTimer(555, 555) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                exit(that);
            }
        }.start();
    }

    static void cover(boolean b) {
        float alpha = 1f;
        if (!b) alpha = 0f;
        oaCover = OA(mCover, "alpha", alpha, 444);
        if (b) mCover.setVisibility(View.VISIBLE);
        else oaCover.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCover.setVisibility(View.GONE);
            }
        });
    }
}
