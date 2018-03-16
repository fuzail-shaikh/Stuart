package kjsce.stuart;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Fuzail Shaikh on 16-Mar-18.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private List<ChatCard> cards;
    private Context context;
    private SharedPreferences prefs;

    ChatAdapter(Context c){
        context = c;
        prefs = context.getSharedPreferences("STUART", Context.MODE_PRIVATE);
        cards = new ArrayList<>();
        cards.add(new ChatCard("Hi "+prefs.getString("NAME", "")+", what can I do for you?", "text", false));
    }

    public void addMessage(String msg, String type, boolean myMessage){
        cards.add(new ChatCard(msg, type, myMessage));
        notifyDataSetChanged();
        new AsyncHttpClient().get(prefs.getString("SERVER", "localhost:3000")+"?msg="+msg, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, responseString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                cards.add(new ChatCard(responseString, "text", false));
                notifyDataSetChanged();
            }

        });
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        if(cards.get(position).myMessage){

        }
        else{
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.msg.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.cardView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.removeRule(RelativeLayout.ALIGN_PARENT_END);
            holder.cardView.setLayoutParams(params);
        }
        holder.msg.setText(cards.get(holder.getAdapterPosition()).msg);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout layout;
        TextView msg;

        ChatViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.chatCard);
            layout = itemView.findViewById(R.id.chatCardLayout);
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
