package kjsce.stuart;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private List<ChatCard> cards;
    private Context context;
    private String server;
    private int sessionID;

    ChatAdapter(Context c){
        context = c;
        server = context.getString(R.string.server);
        SharedPreferences prefs = context.getSharedPreferences("Stuart", Context.MODE_PRIVATE);
        cards = new ArrayList<>();
        cards.add(new ChatCard("Hi "+ prefs.getString("NAME", "")+", what can I do for you?", "text", false));
        notifyDataSetChanged();
        sessionID = new Random().nextInt(9999999)+1;
    }

    void addMessage(String msg){
        cards.add(new ChatCard(msg, "text", true));
        notifyDataSetChanged();
        String url = "/chatbot?msg="+msg;
        url += "&sessionID="+sessionID;
        new AsyncHttpClient().get(server+url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("CHAT", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject res = new JSONObject(responseString);
                    String messageType = res.getString("type");
                    String message = res.getString("text");
                    if(messageType.equalsIgnoreCase("FACULTY")){
                        cards.add(new ChatCard(message, messageType, false));
                        message = "NAME: "+res.getString("name");
                        message += "\nEMAIL: "+res.getString("email");
                        message += "\nBRANCH: "+res.getString("branch");
                        message += "\nLOCATION: "+res.getString("location");
                        cards.add(new ChatCard(message, messageType, false));
                        notifyDataSetChanged();
                    }
                    else if(messageType.equalsIgnoreCase("WRITEUP")){
                        new DownloadFile().execute(server+"/files/"+res.getString("path"), res.getString("fileName"));
                        Toast.makeText(context, "File downloading...", Toast.LENGTH_SHORT).show();
                        cards.add(new ChatCard(message, messageType, res.getString("fileName"), false));
                        notifyDataSetChanged();
                    }
                    else if(messageType.equalsIgnoreCase("EVENT")){
                        message += "\nFROM: "+res.getString("startDate");
                        message += "\nTILL: "+res.getString("endDate");
                        message += "\nLOCATION: "+res.getString("location");
                        cards.add(new ChatCard(message, messageType, server+"/images/"+res.getString("imagePath"), false));
                        notifyDataSetChanged();
                    }
                    else if(messageType.equalsIgnoreCase("LOCATION")){
                        message += "\nNAME: "+res.getString("name");
                        message += "\nADDRESS: "+res.getString("address");
                        cards.add(new ChatCard(message, messageType, server+"/images/"+res.getString("imagePath"), false));
                        notifyDataSetChanged();
                    }
                    else{
                        cards.add(new ChatCard(message, messageType, false));
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position) {
        holder.msg.setText(cards.get(position).msg);
        if(cards.get(position).myMessage){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cardBackground));
            holder.msg.setTextColor(context.getResources().getColor(R.color.cardTextColor));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.cardView.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            holder.cardView.setLayoutParams(params);
        }
        else{
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.msg.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.cardView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.removeRule(RelativeLayout.ALIGN_PARENT_END);
            holder.cardView.setLayoutParams(params);
        }

        if(cards.get(position).type.equalsIgnoreCase("TEXT")){
            holder.image.setVisibility(View.GONE);
            holder.fileOpenText.setVisibility(View.GONE);
        }
        else if(cards.get(position).type.equalsIgnoreCase("EVENT") ||
                cards.get(position).type.equalsIgnoreCase("LOCATION")){
            holder.image.setVisibility(View.VISIBLE);
            holder.fileOpenText.setVisibility(View.GONE);
            new ImageLoadTask(cards.get(position).path, holder.image).execute();
        }
        else if(cards.get(position).type.equalsIgnoreCase("WRITEUP")){
            holder.fileOpenText.setText("Click here to view file");
            holder.image.setVisibility(View.GONE);
            holder.fileOpenText.setVisibility(View.VISIBLE);
            holder.fileOpenText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Stuart");
                    if(!dir.mkdirs()) {
                        Log.d("DOWNLOAD", "Error in creating directory");
                    }
                    File file = new File(dir, cards.get(holder.getAdapterPosition()).path);
                    Uri path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".file.provider", file);
                    Intent fileIntent = new Intent(Intent.ACTION_VIEW);
                    fileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.setDataAndType(path, "application/pdf");
                    try {
                        v.getContext().startActivity(fileIntent);
                    }
                    catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ImageView imageView;

        ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        LinearLayout chatLayout;
        ImageView image;
        TextView msg, fileOpenText;

        ChatViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.chatCard);
            chatLayout = itemView.findViewById(R.id.chatLayout);
            image = itemView.findViewById(R.id.image);
            msg = itemView.findViewById(R.id.message);
            fileOpenText = itemView.findViewById(R.id.fileOpenText);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class ChatCard {
        String msg, type, path;
        boolean myMessage;

        ChatCard(String msg, String type, boolean myMessage) {
            this.msg = msg;
            this.type = type;
            this.myMessage = myMessage;
        }

        ChatCard(String msg, String type, String path, boolean myMessage) {
            this.msg = msg;
            this.type = type;
            this.myMessage = myMessage;
            this.path = path;
        }

    }
}
