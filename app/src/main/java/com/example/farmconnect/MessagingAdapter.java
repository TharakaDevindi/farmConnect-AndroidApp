package com.example.farmconnect;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<MessageModel> messageModelList;
    private Context context;
    private DBHelper dbHelper;
    private MenuItem delete;

    public MessagingAdapter(List<MessageModel> messageModelList, Context context, MenuItem delete) {
        this.messageModelList = messageModelList;
        this.context = context;
        this.delete = delete;
        dbHelper = new DBHelper(context);
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageModelList.get(position);
        if (message.getMsgSender().equals(dbHelper.getLoggedUserID())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_message_send, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_message_recieved, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }
    int position1;
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,int position) {
        MessageModel message = messageModelList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) {
            SentMessageHolder sentMessageHolder = (SentMessageHolder) holder;
            sentMessageHolder.bind(message);
            sentMessageHolder.itemView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Message : " + message.getMsgContent());
                String[] menuContent = {"Edit..", "Delete..", "Share with email..", "Share.."};
                builder.setItems(menuContent, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle("Message : " + message.getMsgContent());
                            final EditText input = new EditText(context);
                            input.setText(message.getMsgContent());
                            builder1.setView(input);
                            builder1.setPositiveButton("OK", (dialog1, which1) -> {
                                String enteredText = input.getText().toString();
                                dbHelper.editMessage(message.getMsgContent(), message.getMsgType(), message.getMsgTime(),
                                        message.getMsgSender(), enteredText);
                                Intent intent = new Intent(context.getApplicationContext(), ChatRoomActivity.class);
                                context.startActivity(intent);
                            });
                            builder1.setNegativeButton("Cancel", (dialog12, which12) -> dialog12.cancel());
                            builder1.show();
                            break;
                        case 1:
                            dbHelper.deleteMessage(message.getMsgContent(), message.getMsgType(), message.getMsgTime(), message.getMsgSender());
                            Intent intent = new Intent(context.getApplicationContext(), ChatRoomActivity.class);
                            context.startActivity(intent);
                            Toast.makeText(context.getApplicationContext(), "Message deleted hi!", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Intent intentEmail = new Intent(Intent.ACTION_SEND);
                            intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                            intentEmail.putExtra(Intent.EXTRA_SUBJECT, "AutoClub Chat App");
                            intentEmail.putExtra(Intent.EXTRA_TEXT, message.getMsgContent());
                            Uri uri = Uri.parse(message.getMsgContent());
                            intentEmail.putExtra(Intent.EXTRA_STREAM, uri);
                            intentEmail.setType("message/rfc822");
                            intentEmail.setPackage("com.google.android.gm");

                            context.startActivity(intentEmail);
                            Toast.makeText(context.getApplicationContext(), "Share email button pressed", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:

                            Intent sendIntent = new Intent();

                            Uri imageUri = Uri.parse(message.getMsgContent());

                            if (imageUri != null && imageUri.getScheme() != null) {
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, message.getMsgContent());
                                sendIntent.setType("text/plain");
                                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                sendIntent.setType("image/*");
                            } else {
                                sendIntent.putExtra(Intent.EXTRA_TEXT, message.getMsgContent());
                                sendIntent.setType("text/plain");
                            }
                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            context.startActivity(shareIntent);
                            Toast.makeText(context.getApplicationContext(), "Share with", Toast.LENGTH_SHORT).show();



                            break;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        } else if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED) {
            ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
            receivedMessageHolder.bind(message);
        }

        delete.setVisible(false);
        holder.itemView.setBackgroundColor(message.isSelected() ? Color.LTGRAY : Color.WHITE);
        holder.itemView.setOnLongClickListener(view -> {
            delete.setVisible(true);
            message.setSelected(!message.isSelected());
            holder.itemView.setBackgroundColor(message.isSelected() ? Color.LTGRAY : Color.WHITE);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView time;
        ImageView imageMessage;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            imageMessage = itemView.findViewById(R.id.imageMessage);
            time = itemView.findViewById(R.id.time);
        }

        void bind(MessageModel messageModel) {
            if (messageModel.getMsgType().equals("text")) {
                message.setText(messageModel.getMsgContent());
                time.setText(messageModel.getMsgTime());
                imageMessage.setVisibility(View.GONE);
            } else if (messageModel.getMsgType().equals("image")) {
                message.setVisibility(View.GONE);
                imageMessage.setVisibility(View.VISIBLE);
                time.setText(messageModel.getMsgTime());

                Uri imageUri = Uri.parse(messageModel.getMsgContent());
                imageMessage.setImageURI(imageUri);
            }
        }
    }
    public void share(){

    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView time;
        TextView username;
        ImageView imageMessage;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            imageMessage = itemView.findViewById(R.id.imageMessage);
            time = itemView.findViewById(R.id.time);
        }

        void bind(MessageModel messageModel) {
            if (messageModel.getMsgType().equals("text")) {
                message.setText(messageModel.getMsgContent());
                time.setText("(" + messageModel.getMsgUser() + ") " + messageModel.getMsgTime());
                imageMessage.setVisibility(View.GONE);
            } else if (messageModel.getMsgType().equals("image")) {
                message.setVisibility(View.GONE);
                imageMessage.setVisibility(View.VISIBLE);
                time.setText("(" + messageModel.getMsgUser() + ") " + messageModel.getMsgTime());

                Uri imageUri = Uri.parse(messageModel.getMsgContent());
                imageMessage.setImageURI(imageUri);
            }
        }
    }
}
