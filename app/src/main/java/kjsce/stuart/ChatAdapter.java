package kjsce.stuart;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private List<ChatCard> cards;
    private Context context;
    private SharedPreferences prefs;
    private int sessionID;

    ChatAdapter(Context c){
        context = c;
        prefs = context.getSharedPreferences("STUART", Context.MODE_PRIVATE);
        cards = new ArrayList<>();
        cards.add(new ChatCard("Hi "+prefs.getString("NAME", "")+", what can I do for you?", "text", false));
        sessionID = new Random().nextInt(9999999)+1;
    }

    void addMessage(String msg, String type, boolean myMessage){
        cards.add(new ChatCard(msg, type, myMessage));
        notifyDataSetChanged();
        final String server = context.getString(R.string.server);
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
                        notifyDataSetChanged();
                        message = "NAME: "+res.getString("name");
                        message += "\nEMAIL: "+res.getString("email");
                        message += "\nBRANCH: "+res.getString("branch");
                        message += "\nLOCATION: "+res.getString("location");
                    }
                    else if(messageType.equalsIgnoreCase("WRITEUP")){
                        message += "\nFile: "+res.getString("path");
                        new DownloadFile().execute(server+"/files/"+res.getString("path"), res.getString("fileName"));
                        Toast.makeText(context, "File downloading...", Toast.LENGTH_SHORT).show();
                    }
                    cards.add(new ChatCard(message, messageType, false));
                    notifyDataSetChanged();
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
        if(!cards.get(holder.getAdapterPosition()).myMessage){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.msg.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.cardView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.removeRule(RelativeLayout.ALIGN_PARENT_END);
            holder.cardView.setLayoutParams(params);
        }
        holder.msg.setText(cards.get(holder.getAdapterPosition()).msg);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout textLayout;
        TextView msg;

        ChatViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.chatCard);
            textLayout = itemView.findViewById(R.id.textLayout);
            msg = itemView.findViewById(R.id.message);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class ChatCard {
        String msg, type;
        boolean myMessage;

        ChatCard(String msg, String type, boolean myMessage) {
            this.msg = msg;
            this.type = type;
            this.myMessage = myMessage;
        }
    }
}
