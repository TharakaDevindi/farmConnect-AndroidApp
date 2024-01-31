package com.example.farmconnect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {


    private List<MessageModel> messageList =  new ArrayList<>();
    private List<MessageModel> searchList =  new ArrayList<>();
    private RecyclerView messageRecyclerView;
    private MessagingAdapter messagingAdapter;

    private EditText messageInput;
    private ImageView sendButton;

    private ImageView backtochat;
    private TextView userProfileName;
    private ImageView insertImage;
    private ImageView insertItem;
    private Uri selectedImageUri;
    private MenuItem deleteMenuItem;
    private MenuItem searchMenuItem;
    Toolbar mainToolbar;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        messageInput = findViewById(R.id.messageET);
        sendButton = findViewById(R.id.sendBtn);
        userProfileName = findViewById(R.id.profile);
        backtochat = findViewById(R.id.chat_back);

        dbHelper = new DBHelper(this);

        userProfileName.setText(dbHelper.getLoggedUsername());

        Cursor cursor = dbHelper.readMessages();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            MessageModel messageModel = new MessageModel(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5));
            messageList.add(messageModel);
            cursor.moveToNext();
        }
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        messageRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(ChatRoomActivity.this, RecyclerView.VERTICAL, false);
        messageRecyclerView.setLayoutManager(manager);

        messageRecyclerView.smoothScrollToPosition(messageList.size());

        insertItem = findViewById(R.id.insert_attachment);
        insertItem.setOnClickListener(v -> imagePickDialog());


        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString();
            SimpleDateFormat s = new SimpleDateFormat("hh:mm");
            String format = s.format(new Date());
            dbHelper.insertMessage(msg, "text", format, dbHelper.getLoggedUserID(), dbHelper.getLoggedUsername());

            MessageModel model1 = new MessageModel(
                    msg,
                    "text",
                    format,
                    dbHelper.getLoggedUserID(),
                    dbHelper.getLoggedUsername()
            );

            messageList.add(model1);
            messageRecyclerView.smoothScrollToPosition(messageList.size());
            messagingAdapter.notifyDataSetChanged();
            messageInput.setText("");
        });

        mainToolbar = findViewById(R.id.chatScreenToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainToolbar.setOnMenuItemClickListener(menu -> {

            if(menu.getItemId() == R.id.search){
                Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
            } else if(menu.getItemId() == R.id.deleteAllSelectedMessages){
                for (int i = 0; i < messageList.size(); i++) {
                    MessageModel messageModel = messageList.get(i);
                    if (messageModel.isSelected()){
                        dbHelper.deleteMessage(messageModel.getMsgContent(), messageModel.getMsgType(), messageModel.getMsgTime(),
                                messageModel.getMsgSender());
                    }
                }
                finish();
                Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                startActivity(intent);
                messagingAdapter.notifyDataSetChanged();
            } else if (menu.getItemId() == R.id.shareAllSelectedMessages) {
                int selectedCount = 0;
                for (MessageModel messageModel : messageList) {
                    if (messageModel.isSelected()) {
                        selectedCount++;
                    }
                }

                String[] shareAllSelectedMessages = new String[selectedCount];
                int index = 0;
                for (MessageModel messageModel : messageList) {
                    if (messageModel.isSelected()) {
                        shareAllSelectedMessages[index] = messageModel.getMsgContent();
                        index++;
                    }
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                StringBuilder builder = new StringBuilder();
                for (String msg : shareAllSelectedMessages) {
                    builder.append(msg).append("\n");
                }
                String shareText = builder.toString().trim();


                Uri imageUri = Uri.parse(shareText);

                if (imageUri != null && imageUri.getScheme() != null) {
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                } else {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    shareIntent.setType("text/plain");
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
            return false;
        });
    }


    public void ClickLogout(View view)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Logout?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_chat, menu);
        deleteMenuItem = menu.findItem(R.id.deleteAllSelectedMessages);
        deleteMenuItem.setVisible(false);
        messagingAdapter = new MessagingAdapter(messageList, ChatRoomActivity.this, deleteMenuItem);
        messageRecyclerView.setAdapter(messagingAdapter);

        searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setMaxWidth(480);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList.clear();
                Cursor cursor = dbHelper.searchMessages(query);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    MessageModel messageModel = new MessageModel(cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4), cursor.getString(5));
                    searchList.add(messageModel);
                    cursor.moveToNext();
                }
                messagingAdapter = new MessagingAdapter(searchList, ChatRoomActivity.this, deleteMenuItem);
                messageRecyclerView.setAdapter(messagingAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            messagingAdapter = new MessagingAdapter(messageList, ChatRoomActivity.this, deleteMenuItem);
            finish();
            Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
            startActivity(intent);
            messagingAdapter.notifyDataSetChanged();
            return false;
        });
        return true;
    }
    private void imagePickDialog(){
        String[] options = {"Open Camera", "Open Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Use image from : ");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");
        selectedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return true;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Camera and Storage Permissions are Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case  STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage Permissions are Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                selectedImageUri = data.getData();
            }
            String msg = selectedImageUri.toString();
            SimpleDateFormat s = new SimpleDateFormat("hh:mm");
            String format = s.format(new Date());
            dbHelper.insertMessage(msg, "image", format, dbHelper.getLoggedUserID(), dbHelper.getLoggedUsername());

            MessageModel model = new MessageModel(
                    msg,
                    "image",
                    format,
                    dbHelper.getLoggedUserID(),
                    dbHelper.getLoggedUsername()
            );
            messageList.add(model);
            messageRecyclerView.smoothScrollToPosition(messageList.size());
            Toast.makeText(this, "Sending image...", Toast.LENGTH_LONG).show();
            messagingAdapter.notifyDataSetChanged();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}