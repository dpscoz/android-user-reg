package com.dpscoz.userregister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dpscoz.userregister.model.AppUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public String URL;

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<AppUser> appUsers = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private AppUserAdapter appUserAdapter;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat formatBr = new SimpleDateFormat("dd/MM/yyyy");
    //private String url = "http://192.168.100.8:8080/users";

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    private ImageView userPicture;
    private RelativeLayout layoutPicture;
    private String strBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.URL = "http://" + getString(R.string.ip) + ":" + getString(R.string.port) + getString(R.string.api);
        setContentView(R.layout.activity_main);

        refresh = (SwipeRefreshLayout) findViewById(R.id.swipedown);
        recyclerView = (RecyclerView) findViewById(R.id.app_user);

        dialog = new Dialog(this);

        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                appUsers.clear();
                getData();
            }
        });
    }

    public void getData(){
        appUsers.clear();
        refresh.setRefreshing(true);

        arrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        String strDate = jsonObject.getString("birthday");
                        Date birthday = new Date();
                        if(strDate != null && !strDate.isEmpty() && !strDate.equals("null")) {
                            birthday = formatDb.parse(strDate);
                        }

                        AppUser appUser = new AppUser();
                        appUser.setId(jsonObject.getInt("id"));
                        appUser.setName(jsonObject.getString("name"));
                        appUser.setBirthday(birthday);
                        appUser.setPicture(jsonObject.getString("picture"));

                        appUsers.add(appUser);
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(appUsers);
                refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DIOGOSCOZ", "err: " + error.getMessage());
            }
        });

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<AppUser> appUsers) {
        appUserAdapter = new AppUserAdapter(this, appUsers, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appUserAdapter);
    }

    public void showFileChooser(ImageView userPicture) {
        this.userPicture = userPicture;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userPicture.setImageBitmap(bitmap);
                strBitmap = getStringImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void addAppUser(View v) {
        TextView close, editBirthday;
        EditText editName;
        ImageView btnCalendar;
        Button btnSubmit;


        dialog.setContentView(R.layout.activity_user_detail);

        close = (TextView) dialog.findViewById(R.id.txt_close);
        editBirthday = (TextView) dialog.findViewById(R.id.edit_birthday);
        btnCalendar = (ImageView) dialog.findViewById(R.id.btn_calendar);
        editName = (EditText) dialog.findViewById(R.id.edit_name);
        btnSubmit = (Button) dialog.findViewById(R.id.submit);
        layoutPicture = (RelativeLayout) dialog.findViewById(R.id.layout_picture);
        userPicture = (ImageView) dialog.findViewById(R.id.user_picture);

        layoutPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showFileChooser(userPicture);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUser newUser = new AppUser();
                String name = editName.getText().toString();
                String strBirthday = editBirthday.getText().toString();

                Date birthday = null;
                try {
                    birthday = formatBr.parse(strBirthday);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(name.isEmpty() || strBirthday.isEmpty() || strBitmap.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_fill), Toast.LENGTH_LONG).show();
                } else {
                    newUser.setName(name);
                    newUser.setBirthday(birthday);
                    newUser.setPicture(strBitmap);

                    submit(newUser);
                }
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (month+1) + "/" + year;
                        editBirthday.setText(date);
                    }
                }, 2000, 0, 1);

                datePickerDialog.updateDate(2000,0,1);
                datePickerDialog.show();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(AppUser appUser) {

        JSONObject js = new JSONObject();
        try {
            String strDate = formatDb.format(appUser.getBirthday());
            js.put("name",appUser.getName());
            js.put("birthday", strDate);
            js.put("picture", appUser.getPicture());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new MyJsonObjectRequest(Request.Method.POST, URL, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refresh.post(new Runnable() {
                    @Override
                    public void run() {
                        appUsers.clear();
                        getData();
                    }
                });

                Toast.makeText(getApplicationContext(), getString(R.string.user_saved_success), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(request);

    }

    @Override
    public void onRefresh() {
        appUsers.clear();
        getData();
    }
}