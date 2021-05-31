package com.dpscoz.userregister;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dpscoz.userregister.model.AppUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AppUserAdapter extends RecyclerView.Adapter<AppUserAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<AppUser> appUsers;

    private MainActivity main;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat formatBr = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");

    private ImageView userPicture;
    private RelativeLayout layoutPicture;

    public AppUserAdapter(Context context, ArrayList<AppUser> appUsers, MainActivity main) {
        this.context = context;
        this.appUsers = appUsers;
        this.main = main;
    }


    @NonNull
    @Override
    public AppUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.app_user_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppUserAdapter.MyViewHolder holder, int position) {
        AppUser appUser = appUsers.get(position);

        holder.name.setText(appUser.getName());
        byte[] imageBytes = Base64.decode(appUser.getPicture(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.userPicture.setImageBitmap(decodedImage);

        holder.editAppUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editAppUser(appUser);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.deleteAppUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppUser(appUser);
            }
        });
    }


    private void deleteAppUser(AppUser appUser) {
        TextView close;
        Button submit;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.delete_user);

        close = (TextView) dialog.findViewById(R.id.txt_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit = (Button) dialog.findViewById(R.id.delete);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDelete(dialog, appUser);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void editAppUser(AppUser appUser) throws UnsupportedEncodingException {
        TextView close, title, editBirthday;
        ImageView btnCalendar;
        EditText editName;
        Button submit;

        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_user_detail);

        close = (TextView) dialog.findViewById(R.id.txt_close);
        title = (TextView) dialog.findViewById(R.id.title);
        editName = (EditText) dialog.findViewById(R.id.edit_name);
        editBirthday = (TextView) dialog.findViewById(R.id.edit_birthday);
        btnCalendar = (ImageView) dialog.findViewById(R.id.btn_calendar);
        submit = (Button) dialog.findViewById(R.id.submit);
        layoutPicture = (RelativeLayout) dialog.findViewById(R.id.layout_picture);
        userPicture = (ImageView) dialog.findViewById(R.id.user_picture);

        title.setText(main.getString(R.string.user_detail_ID) + String.valueOf(appUser.getId()));
        editName.setText(appUser.getName());
        editBirthday.setText(formatBr.format(appUser.getBirthday()));

        byte[] imageBytes = Base64.decode(appUser.getPicture(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        userPicture.setImageBitmap(decodedImage);

        layoutPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                main.showFileChooser(userPicture);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String strBirthday = editBirthday.getText().toString();
                Date birthday = null;
                try {
                    birthday = formatBr.parse(strBirthday);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(name.isEmpty() || strBirthday.isEmpty()) {
                    Toast.makeText(context, main.getString(R.string.please_fill), Toast.LENGTH_LONG).show();
                } else {
                    appUser.setName(name);
                    appUser.setBirthday(birthday);

                    submit(dialog, appUser);
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void submitDelete(final Dialog dialog, AppUser appUser) {
        JSONObject js = new JSONObject();
        try {
            js.put("id", appUser.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new MyJsonObjectRequest(Request.Method.DELETE, main.URL + "/" + appUser.getId(), js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                Toast.makeText(context, main.getString(R.string.user_deleted), Toast.LENGTH_LONG).show();
                main.getData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(context).add(request);
    }

    private void submit(final Dialog dialog, AppUser appUser) {
        JSONObject js = new JSONObject();
        try {
            String strDate = formatDb.format(appUser.getBirthday());

            js.put("id", appUser.getId());
            js.put("name", appUser.getName());
            js.put("birthday", strDate);
            js.put("picture", appUser.getPicture());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new MyJsonObjectRequest(Request.Method.PUT, main.URL + "/" + appUser.getId(), js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                Toast.makeText(context, main.getString(R.string.user_updated), Toast.LENGTH_LONG).show();
                main.getData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(context).add(request);
    }

    @Override
    public int getItemCount() {
        return appUsers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView editAppUser, deleteAppUser, userPicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.editAppUser = (ImageView) itemView.findViewById(R.id.edit_user);
            this.deleteAppUser = (ImageView) itemView.findViewById(R.id.delete_user);
            this.userPicture = (ImageView) itemView.findViewById(R.id.user_picture);
        }
    }
}
