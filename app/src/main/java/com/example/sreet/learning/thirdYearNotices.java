package com.example.sreet.learning;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class thirdYearNotices extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> myarraylist = new ArrayList<>();
    ArrayList<String> dateAndTimeList = new ArrayList<String>();
    ListView list;
    EditText myedittext,keyvaluetext;
    ImageButton myApplyBt,imageButton;
    String myString,keyvaluedata;
    Firebase myfire;
    StorageReference imageStorage;
    StorageReference multipleImageStorage;
    ProgressDialog uploadProgress;
    private  static final int GALLERY_INTENT = 1;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,listViewClick.class);
        String positionDate = dateAndTimeList.get(position);
        intent.putExtra("Date",positionDate);
        String positionDes = myarraylist.get(position);
        intent.putExtra("Description",positionDes);
        intent.putExtra("Year","thirdYear");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            if(data.getClipData()!=null){
                uploadProgress.setMessage("Uploading ... ");
                uploadProgress.show();
                int totalItemsSelected = data.getClipData().getItemCount();
                EditText FilePathName = (EditText) findViewById(R.id.editText);
                final String filePathValue = FilePathName.getText().toString();
                for(int i=0;i<totalItemsSelected;i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String s = String.valueOf(i);
                    StorageReference filetopath = multipleImageStorage.child("Notices").child("thirdYear").child(filePathValue).child(s);
                    filetopath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(thirdYearNotices.this, "done", Toast.LENGTH_SHORT).show();
                            uploadProgress.dismiss();
                        }
                    });
                }
                Date todaysDate = new Date();
                DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String date = df2.format(todaysDate);
                Firebase childbase = myfire.child(date);
                childbase.setValue(filePathValue);
                FilePathName.setText(null);
            }
            else if(data.getData()!=null){
                Uri uri = data.getData();
                final EditText FilePathName = (EditText) findViewById(R.id.editText);
                uploadProgress.setMessage("Uploading ... ");
                uploadProgress.show();
                final String filePathValue = FilePathName.getText().toString();
                StorageReference filepath = imageStorage.child("Notices").child("thirdYear").child(filePathValue).child("0");
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(thirdYearNotices.this, "Upload Done", Toast.LENGTH_SHORT).show();
                        uploadProgress.dismiss();
                        Date todaysDate = new Date();
                        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date = df2.format(todaysDate);
                        Firebase childbase = myfire.child(date);
                        childbase.setValue(filePathValue);
                        FilePathName.setText(null);
                    }
                });
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_year_notices);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setTitle("Third Year Notices");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(thirdYearNotices.this, "Click on a specific notification to open it for the detailed information about the notification", Toast.LENGTH_SHORT).show();
        myedittext = (EditText) findViewById(R.id.editText);
        myApplyBt = (ImageButton) findViewById(R.id.button);
        imageButton = (ImageButton) findViewById(R.id.addImageButton);
        uploadProgress = new ProgressDialog(this);
        imageStorage = FirebaseStorage.getInstance().getReference();
        multipleImageStorage = FirebaseStorage.getInstance().getReference();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myedittext.getText().toString().trim().length()>0) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT);
                }
                else{
                    Toast.makeText(thirdYearNotices.this, "Enter the notice before adding image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Firebase.setAndroidContext(this);
        myfire = new Firebase("https://learning-2b334.firebaseio.com/users/Notices/thirdYear");
        final ArrayAdapter<String> myarrayadapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,myarraylist);
        list = (ListView) findViewById(R.id.listview);
        list.setOnItemClickListener(this);
        list.setAdapter(myarrayadapter);
        //list.setSelection(list.getAdapter().getCount()-1);
        myApplyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myString = myedittext.getText().toString();
                //keyvaluedata = keyvaluetext.getText().toString();
                Date todaysDate = new Date();
                DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String date = df2.format(todaysDate);
                Firebase childbase = myfire.child(date);
                childbase.setValue(myString);
                myedittext.setText(null);
                Toast.makeText(thirdYearNotices.this, "success", Toast.LENGTH_SHORT).show();
            }
        });




        myfire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String childvalaue = dataSnapshot.getValue(String.class);
                String keyvalue = dataSnapshot.getKey();
                dateAndTimeList.add(keyvalue);
                myarraylist.add(childvalaue);
                myarrayadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                myarrayadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.favorite:
                Toast.makeText(thirdYearNotices.this, "Added to your favorite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about:
                Intent i = new Intent(this,about.class);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}